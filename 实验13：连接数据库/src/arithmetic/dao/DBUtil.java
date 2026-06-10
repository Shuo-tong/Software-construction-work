package arithmetic.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具类。
 * 提供获取连接、关闭连接的静态方法。
 * 连接参数集中管理，后续可改为读取配置文件。
 */
public final class DBUtil {

    // ====== 连接参数（按需修改） ======
    private static final String URL  = "jdbc:mysql://localhost:3306/swork?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useUnicode=true";
    private static final String USER = "root";
    private static final String PASS = "050203Asd#";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL 驱动加载失败，请确认 mysql-connector-java.jar 已加入 classpath", e);
        }
    }

    private DBUtil() { }

    /** 获取数据库连接 */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** 安全关闭连接（忽略 null） */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try { r.close(); } catch (Exception ignored) { }
            }
        }
    }
}
