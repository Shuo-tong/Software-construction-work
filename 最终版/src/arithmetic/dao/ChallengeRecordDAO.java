package arithmetic.dao;

import arithmetic.Difficulty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 挑战记录数据访问对象。
 * 新增持久化层——原系统挑战结束后仅更新内存积分，现在同步写入 DB。
 */
public class ChallengeRecordDAO {

    /** 插入一条挑战记录（含模式） */
    public boolean insert(String userId, Difficulty difficulty, Difficulty.Mode mode,
                          int total, int correct, int wrong,
                          int scoreEarned, long timeUsedMs) {
        String sql = "INSERT INTO challenge_records(user_id, difficulty, mode, total, correct, wrong, score_earned, time_used_ms) "
                   + "VALUES(?,?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setString(2, difficulty.name());
            ps.setString(3, mode != null ? mode.name() : "MIXED");
            ps.setInt(4, total);
            ps.setInt(5, correct);
            ps.setInt(6, wrong);
            ps.setInt(7, scoreEarned);
            ps.setLong(8, timeUsedMs);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("插入挑战记录失败: " + e.getMessage());
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    /** 查询某用户的全部挑战记录（按时间倒序） */
    public List<ChallengeRecord> findByUserId(String userId) {
        String sql = "SELECT id, user_id, difficulty, mode, total, correct, wrong, score_earned, time_used_ms, created_at "
                   + "FROM challenge_records WHERE user_id=? ORDER BY created_at DESC";
        List<ChallengeRecord> list = new ArrayList<>();
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
            System.err.println("查询挑战记录失败: " + e.getMessage());
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    /** 查询某用户最近 n 条挑战记录 */
    public List<ChallengeRecord> findRecent(String userId, int limit) {
        String sql = "SELECT id, user_id, difficulty, mode, total, correct, wrong, score_earned, time_used_ms, created_at "
                   + "FROM challenge_records WHERE user_id=? ORDER BY created_at DESC LIMIT ?";
        List<ChallengeRecord> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setInt(2, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询最近挑战记录失败: " + e.getMessage());
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    /** 查询某用户挑战总次数 */
    public int countByUserId(String userId) {
        String sql = "SELECT COUNT(*) FROM challenge_records WHERE user_id=?";
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

    private ChallengeRecord mapRow(ResultSet rs) throws SQLException {
        String modeStr = rs.getString("mode");
        Difficulty.Mode mode = null;
        if (modeStr != null) {
            try {
                mode = Difficulty.Mode.valueOf(modeStr);
            } catch (IllegalArgumentException ignored) {}
        }

        return new ChallengeRecord(
                rs.getLong("id"),
                rs.getString("user_id"),
                Difficulty.valueOf(rs.getString("difficulty")),
                mode,
                rs.getInt("total"),
                rs.getInt("correct"),
                rs.getInt("wrong"),
                rs.getInt("score_earned"),
                rs.getLong("time_used_ms"),
                rs.getTimestamp("created_at")
        );
    }

    // ====== 内部值对象 ======

    /** 挑战记录值对象（含模式） */
    public static class ChallengeRecord {
        public final long id;
        public final String userId;
        public final Difficulty difficulty;
        public final Difficulty.Mode mode;
        public final int total;
        public final int correct;
        public final int wrong;
        public final int scoreEarned;
        public final long timeUsedMs;
        public final Timestamp createdAt;

        public ChallengeRecord(long id, String userId, Difficulty difficulty, Difficulty.Mode mode,
                               int total, int correct, int wrong,
                               int scoreEarned, long timeUsedMs, Timestamp createdAt) {
            this.id = id;
            this.userId = userId;
            this.difficulty = difficulty;
            this.mode = mode;
            this.total = total;
            this.correct = correct;
            this.wrong = wrong;
            this.scoreEarned = scoreEarned;
            this.timeUsedMs = timeUsedMs;
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s %s 题数=%d 正确=%d 积分=%d 耗时=%.1fs %s",
                    difficulty.name(),
                    mode != null ? mode.displayName : "混合",
                    userId, total, correct, scoreEarned,
                    timeUsedMs / 1000.0, createdAt);
        }
    }
}
