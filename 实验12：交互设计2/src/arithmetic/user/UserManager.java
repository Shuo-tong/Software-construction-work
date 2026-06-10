package arithmetic.user;

import arithmetic.dao.UserDAO;
import arithmetic.dao.WrongEntryDAO;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理器（单例）。
 * 负责注册、登录、当前登录态。
 * 持久化已迁移至 MySQL（通过 UserDAO / WrongEntryDAO）。
 */
public final class UserManager {
    private static final UserManager INSTANCE = new UserManager();

    private final UserDAO userDAO = new UserDAO();
    private final WrongEntryDAO wrongEntryDAO = new WrongEntryDAO();

    /** userId -> User（内存缓存，启动时从 DB 加载） */
    private final Map<String, User> users = new LinkedHashMap<>();
    private User currentUser;

    private UserManager() {
        loadFromDB();
    }

    public static UserManager getInstance() { return INSTANCE; }

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
        if (!userDAO.insert(u)) {
            System.out.println("注册失败：数据库写入异常。");
            return false;
        }
        users.put(userId, u);
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
        // 从数据库加载该用户的错题本
        u.getWrongBook().loadFromDB(userId, wrongEntryDAO);
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
    //  数据库持久化
    // ========================

    /** 从数据库加载所有用户到内存缓存 */
    public void loadFromDB() {
        users.clear();
        List<User> all = userDAO.findAll();
        for (User u : all) {
            users.put(u.getUserId(), u);
        }
    }

    /** 保存当前用户的档案 + 错题本到数据库 */
    public void saveCurrentUser() {
        if (currentUser == null) return;
        userDAO.update(currentUser);
        currentUser.getWrongBook().saveToDB(currentUser.getUserId(), wrongEntryDAO);
    }
}
