package arithmetic.dao;

import arithmetic.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象。
 * 替代原 UserManager 中的 CSV 读写逻辑。
 */
public class UserDAO {

    /** 插入新用户 */
    public boolean insert(User user) {
        String sql = "INSERT INTO users(user_id, password, nickname, score, total_challenges, total_correct) VALUES(?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNickname());
            ps.setInt(4, user.getScore());
            ps.setInt(5, user.getTotalChallenges());
            ps.setInt(6, user.getTotalCorrect());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("插入用户失败: " + e.getMessage());
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    /** 根据 userId 查询用户，不存在返回 null */
    public User findById(String userId) {
        String sql = "SELECT user_id, password, nickname, score, total_challenges, total_correct FROM users WHERE user_id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            return null;
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 查询所有用户 */
    public List<User> findAll() {
        String sql = "SELECT user_id, password, nickname, score, total_challenges, total_correct FROM users";
        List<User> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有用户失败: " + e.getMessage());
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    /** 更新用户积分和统计信息 */
    public boolean update(User user) {
        String sql = "UPDATE users SET password=?, nickname=?, score=?, total_challenges=?, total_correct=? WHERE user_id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getNickname());
            ps.setInt(3, user.getScore());
            ps.setInt(4, user.getTotalChallenges());
            ps.setInt(5, user.getTotalCorrect());
            ps.setString(6, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新用户失败: " + e.getMessage());
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    /** 判断用户是否存在 */
    public boolean exists(String userId) {
        String sql = "SELECT 1 FROM users WHERE user_id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 查询用户总数 */
    public int count() {
        String sql = "SELECT COUNT(*) FROM users";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            return 0;
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** ResultSet 行映射为 User 对象 */
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("user_id"),
                rs.getString("password"),
                rs.getString("nickname"),
                rs.getInt("score"),
                rs.getInt("total_challenges"),
                rs.getInt("total_correct")
        );
    }
}
