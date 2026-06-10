package arithmetic.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户管理器（单例）。
 * 负责注册、登录、当前登录态、CSV 持久化（用户档案 + 各自错题本）。
 */
public final class UserManager {
    private static final UserManager INSTANCE = new UserManager();

    /** 数据目录 */
    public static final String DATA_DIR = "Users";
    /** 用户档案文件 */
    private static final String USERS_FILE = DATA_DIR + "/users.csv";
    /** 错题本文件命名前缀 */
    private static final String WRONG_PREFIX = DATA_DIR + "/wrong_";

    /** userId -> User */
    private final Map<String, User> users = new LinkedHashMap<>();
    private User currentUser;

    private UserManager() {
        ensureDir();
        loadFromCSV();
    }

    public static UserManager getInstance() { return INSTANCE; }

    private void ensureDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ========================
    //  注册 / 登录 / 登出
    // ========================

    /** 注册新用户；用户名重复返回 false */
    public boolean register(String userId, String password, String nickname) {
        if (userId == null || userId.isEmpty()) return false;
        if (users.containsKey(userId)) {
            System.out.println("注册失败：用户名 " + userId + " 已存在。");
            return false;
        }
        User u = new User(userId, password, nickname);
        users.put(userId, u);
        saveToCSV();
        System.out.println("注册成功：" + u);
        return true;
    }

    /** 登录；成功后 currentUser 被设置 */
    public boolean login(String userId, String password) {
        User u = users.get(userId);
        if (u == null) {
            System.out.println("登录失败：用户不存在。");
            return false;
        }
        if (!u.verifyPassword(password)) {
            System.out.println("登录失败：密码错误。");
            return false;
        }
        this.currentUser = u;
        // 加载该用户的错题本
        File wrongFile = new File(WRONG_PREFIX + userId + ".csv");
        u.getWrongBook().readCSV(wrongFile);
        System.out.println("欢迎回来，" + u.getNickname() + "！");
        return true;
    }

    public void logout() {
        if (currentUser != null) {
            saveCurrentUser();   // 登出前保存
            System.out.println(currentUser.getNickname() + " 已登出。");
            currentUser = null;
        }
    }

    public User getCurrentUser() { return currentUser; }
    public boolean isLoggedIn()  { return currentUser != null; }

    public User getUser(String userId) { return users.get(userId); }
    public Collection<User> allUsers()  { return users.values(); }
    public int userCount()              { return users.size(); }

    // ========================
    //  CSV 持久化
    // ========================
    // users.csv 行格式：userId,password,nickname,score,totalChallenges,totalCorrect

    public void saveToCSV() {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.println("# userId,password,nickname,score,totalChallenges,totalCorrect");
            for (User u : users.values()) {
                pw.printf("%s,%s,%s,%d,%d,%d%n",
                        u.getUserId(), u.getPassword(), u.getNickname(),
                        u.getScore(), u.getTotalChallenges(), u.getTotalCorrect());
            }
        } catch (IOException e) {
            System.err.println("写入用户档案失败: " + e.getMessage());
        }
    }

    public void loadFromCSV() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) continue;
                String[] cols = line.split(",", -1);
                if (cols.length < 6) continue;
                User u = new User(cols[0].trim(), cols[1].trim(), cols[2].trim(),
                        Integer.parseInt(cols[3].trim()),
                        Integer.parseInt(cols[4].trim()),
                        Integer.parseInt(cols[5].trim()));
                users.put(u.getUserId(), u);
            }
        } catch (IOException e) {
            System.err.println("读取用户档案失败: " + e.getMessage());
        }
    }

    /** 保存当前用户的档案 + 错题本 */
    public void saveCurrentUser() {
        if (currentUser == null) return;
        saveToCSV();
        File wrongFile = new File(WRONG_PREFIX + currentUser.getUserId() + ".csv");
        currentUser.getWrongBook().writeCSV(wrongFile);
    }
}
