package arithmetic.dao;

import arithmetic.*;
import arithmetic.user.WrongEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 错题本数据访问对象。
 * 替代原 WrongBook 中的 CSV 读写逻辑。
 */
public class WrongEntryDAO {

    /**
     * 插入一条错题记录。
     * 若该用户同一题已存在（唯一键冲突），则更新 user_answer、retry_count+1、mastered=0。
     */
    public boolean insertOrUpdate(String userId, WrongEntry entry) {
        String sql = "INSERT INTO wrong_entries(user_id, left_operand, operator, right_operand, correct_answer, user_answer, difficulty, timestamp_ms, retry_count, mastered) "
                   + "VALUES(?,?,?,?,?,?,?,?,?,?) "
                   + "ON DUPLICATE KEY UPDATE user_answer=VALUES(user_answer), retry_count=retry_count+1, mastered=0";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            Binaryoperation p = entry.getProblem();
            ps.setString(1, userId);
            ps.setInt(2, p.getLeftOperand());
            ps.setString(3, String.valueOf(p.getOperator()));
            ps.setInt(4, p.getRightOperand());
            ps.setInt(5, p.getResult());
            ps.setInt(6, entry.getUserAnswer());
            ps.setString(7, entry.getDifficulty().name());
            ps.setLong(8, entry.getTimestamp());
            ps.setInt(9, entry.getRetryCount());
            ps.setInt(10, entry.isMastered() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("插入/更新错题失败: " + e.getMessage());
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    /** 查询某用户的全部错题 */
    public List<WrongEntry> findByUserId(String userId) {
        String sql = "SELECT left_operand, operator, right_operand, correct_answer, user_answer, difficulty, timestamp_ms, retry_count, mastered "
                   + "FROM wrong_entries WHERE user_id=? ORDER BY timestamp_ms";
        List<WrongEntry> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询错题失败: " + e.getMessage());
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    /** 标记某道错题为已掌握 */
    public boolean markMastered(String userId, int leftOp, char operator, int rightOp) {
        String sql = "UPDATE wrong_entries SET mastered=1 WHERE user_id=? AND left_operand=? AND operator=? AND right_operand=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setInt(2, leftOp);
            ps.setString(3, String.valueOf(operator));
            ps.setInt(4, rightOp);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("标记掌握失败: " + e.getMessage());
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    /** 查询某用户未掌握的错题数 */
    public int activeCount(String userId) {
        String sql = "SELECT COUNT(*) FROM wrong_entries WHERE user_id=? AND mastered=0";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            return 0;
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 批量保存错题（事务：先删后插，保证与内存一致） */
    public boolean saveAll(String userId, List<WrongEntry> entries) {
        String deleteSql = "DELETE FROM wrong_entries WHERE user_id=?";
        String insertSql = "INSERT INTO wrong_entries(user_id, left_operand, operator, right_operand, correct_answer, user_answer, difficulty, timestamp_ms, retry_count, mastered) "
                         + "VALUES(?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement delPs = null;
        PreparedStatement insPs = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            delPs = conn.prepareStatement(deleteSql);
            delPs.setString(1, userId);
            delPs.executeUpdate();

            insPs = conn.prepareStatement(insertSql);
            for (WrongEntry entry : entries) {
                Binaryoperation p = entry.getProblem();
                insPs.setString(1, userId);
                insPs.setInt(2, p.getLeftOperand());
                insPs.setString(3, String.valueOf(p.getOperator()));
                insPs.setInt(4, p.getRightOperand());
                insPs.setInt(5, p.getResult());
                insPs.setInt(6, entry.getUserAnswer());
                insPs.setString(7, entry.getDifficulty().name());
                insPs.setLong(8, entry.getTimestamp());
                insPs.setInt(9, entry.getRetryCount());
                insPs.setInt(10, entry.isMastered() ? 1 : 0);
                insPs.addBatch();
            }
            insPs.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("批量保存错题失败: " + e.getMessage());
            if (conn != null) { try { conn.rollback(); } catch (SQLException ignored) {} }
            return false;
        } finally {
            DBUtil.close(insPs, delPs, conn);
        }
    }

    /** ResultSet 行映射为 WrongEntry 对象 */
    private WrongEntry mapRow(ResultSet rs) throws SQLException {
        int left  = rs.getInt("left_operand");
        char oper = rs.getString("operator").charAt(0);
        int right = rs.getInt("right_operand");
        int correctAns = rs.getInt("correct_answer");

        Binaryoperation problem;
        switch (oper) {
            case '+': problem = new Addition();       break;
            case '-': problem = new Substraction();   break;
            case '*': problem = new Multiplication(); break;
            case '/': problem = new Division();       break;
            default:  problem = new Addition();       break;
        }
        problem.setLeftOperand(left);
        problem.setRightOperand(right);
        problem.setValue(correctAns);

        int userAns   = rs.getInt("user_answer");
        Difficulty d  = Difficulty.valueOf(rs.getString("difficulty"));
        long ts       = rs.getLong("timestamp_ms");
        int retry     = rs.getInt("retry_count");
        boolean mast  = rs.getInt("mastered") == 1;

        return new WrongEntry(problem, userAns, d, ts, retry, mast);
    }
}
