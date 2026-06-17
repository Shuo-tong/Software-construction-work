package arithmetic.challenge;

import arithmetic.Binaryoperation;
import arithmetic.Difficulty;
import arithmetic.user.Leaderboard;
import arithmetic.user.User;
import arithmetic.user.UserManager;

import java.util.Scanner;

/**
 * 控制台交互入口：登录 / 注册 / 挑战 / 错题本 / 积分榜。
 */
public class ChallengeRunner {

    private final Scanner scanner = new Scanner(System.in);
    private final UserManager userManager = UserManager.getInstance();
    private final Leaderboard leaderboard = Leaderboard.getInstance();

    public void run() {
        printWelcome();
        while (true) {
            if (!userManager.isLoggedIn()) {
                if (!authMenu()) return;
            } else {
                if (!mainMenu()) return;
            }
        }
    }

    private void printWelcome() {
        System.out.println("========================================");
        System.out.println("        小学口算挑战系统");
        System.out.println("========================================");
    }

    /** 未登录菜单。返回 false 表示退出程序。 */
    private boolean authMenu() {
        System.out.println("\n[1] 登录   [2] 注册   [0] 退出");
        System.out.print("请选择：");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": doLogin();    return true;
            case "2": doRegister(); return true;
            case "0":
                System.out.println("再见！");
                return false;
            default:
                System.out.println("无效输入。");
                return true;
        }
    }

    /** 已登录主菜单。返回 false 表示退出程序。 */
    private boolean mainMenu() {
        User u = userManager.getCurrentUser();
        System.out.printf("%n>>> [%s] 当前积分：%d   错题：%d   排名：%d%n",
                u.getNickname(), u.getScore(),
                u.getWrongBook().activeCount(),
                leaderboard.rankOf(u.getUserId()));
        System.out.println("[1] 开始挑战   [2] 查看错题本   [3] 积分榜 TOP10");
        System.out.println("[4] 个人信息   [9] 登出       [0] 退出");
        System.out.print("请选择：");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": doChallenge(); return true;
            case "2": u.getWrongBook().display(); return true;
            case "3": leaderboard.display(10); return true;
            case "4": System.out.println(u); return true;
            case "9": userManager.logout(); return true;
            case "0":
                userManager.logout();
                System.out.println("再见！");
                return false;
            default:
                System.out.println("无效输入。");
                return true;
        }
    }

    private void doRegister() {
        System.out.print("用户名：");      String id = scanner.nextLine().trim();
        System.out.print("密码：");        String pw = scanner.nextLine().trim();
        System.out.print("昵称（回车默认用账号）：");
        String nick = scanner.nextLine().trim();
        userManager.register(id, pw, nick);
    }

    private void doLogin() {
        System.out.print("用户名：");  String id = scanner.nextLine().trim();
        System.out.print("密码：");    String pw = scanner.nextLine().trim();
        userManager.login(id, pw);
    }

    private void doChallenge() {
        // 第一步：选择模式
        System.out.println("\n选择模式：");
        Difficulty.Mode[] modes = Difficulty.Mode.values();
        for (int i = 0; i < modes.length; i++) {
            System.out.printf("  [%d] %s — %s%n", i + 1, modes[i].displayName, modes[i].description);
        }
        System.out.print("请选择：");
        String modeChoice = scanner.nextLine().trim();
        Difficulty.Mode mode;
        try {
            int mi = Integer.parseInt(modeChoice) - 1;
            if (mi < 0 || mi >= modes.length) throw new NumberFormatException();
            mode = modes[mi];
        } catch (NumberFormatException e) {
            System.out.println("无效选择，已取消。");
            return;
        }

        // 第二步：选择难度
        System.out.println("\n选择难度：[1] L1 入门 [2] L2 进阶 [3] L3 满级");
        System.out.print("请选择：");
        String s = scanner.nextLine().trim();
        Difficulty d;
        switch (s) {
            case "1": d = Difficulty.L1; break;
            case "2": d = Difficulty.L2; break;
            case "3": d = Difficulty.L3; break;
            default:  System.out.println("无效难度，已取消。"); return;
        }

        int actualCount = d.problemCount * mode.problemMultiplier;
        System.out.printf("=== 模式：%s  难度：%s  共 %d 题 ===%n", mode.displayName, d.name(), actualCount);

        Challenge ch = new Challenge(userManager.getCurrentUser(), d, mode);
        ch.generate();
        for (int i = 0; i < ch.getProblemCount(); i++) {
            Binaryoperation p = ch.getProblems().get(i);
            System.out.printf("第%2d题: %s = ", (i + 1), p.toString());
            String line = scanner.nextLine().trim();
            try {
                int ans = Integer.parseInt(line);
                ch.submitAnswer(i, ans);
            } catch (NumberFormatException e) {
                System.out.println("(已记为未作答)");
            }
        }
        ChallengeResult result = ch.finish();
        result.display();
    }

    public static void main(String[] args) {
        new ChallengeRunner().run();
    }
}
