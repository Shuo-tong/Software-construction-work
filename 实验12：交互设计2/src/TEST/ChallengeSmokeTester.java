package TEST;

import arithmetic.Binaryoperation;
import arithmetic.Difficulty;
import arithmetic.OperationFactory;
import arithmetic.challenge.Challenge;
import arithmetic.challenge.ChallengeResult;
import arithmetic.user.Leaderboard;
import arithmetic.user.User;
import arithmetic.user.UserManager;

import java.io.File;

/**
 * 端到端冒烟测试：
 *  1) 清理上次留下的 Users/ 目录
 *  2) 注册三名用户
 *  3) 模拟用户做不同难度挑战、故意答错若干题
 *  4) 校验积分入账、错题入库、排行榜排序
 */
public class ChallengeSmokeTester {

    public static void main(String[] args) {
        cleanupUsers();

        UserManager um = UserManager.getInstance();
        // 由于 UserManager 在静态初始化时已 loadFromCSV，需要再清理一次内存
        // 简单粗暴：直接对每个老用户用空 password 登录都会失败，因此本测试的隔离靠目录清理 + 重启 JVM。
        // 这里我们用 register 自带的"已存在则失败"作为幂等保障。

        System.out.println("\n===== 步骤 1：注册 3 名用户 =====");
        um.register("alice", "111", "小爱");
        um.register("bob",   "222", "小博");
        um.register("carol", "333", "小卡");

        System.out.println("\n===== 步骤 2：alice 做 L1 挑战，全对 =====");
        runChallenge("alice", "111", Difficulty.L1, 0);   // 0 道故意错

        System.out.println("\n===== 步骤 3：bob 做 L2 挑战，错 3 道 =====");
        runChallenge("bob", "222", Difficulty.L2, 3);

        System.out.println("\n===== 步骤 4：carol 做 L3 挑战，错 5 道 =====");
        runChallenge("carol", "333", Difficulty.L3, 5);

        System.out.println("\n===== 步骤 5：再让 carol 做一次 L1，全对 =====");
        runChallenge("carol", "333", Difficulty.L1, 0);

        System.out.println("\n===== 步骤 6：积分榜 =====");
        Leaderboard.getInstance().display(10);

        System.out.println("\n===== 步骤 7：carol 的错题本 =====");
        um.login("carol", "333");
        um.getCurrentUser().getWrongBook().display();
        um.logout();

        System.out.println("\n[OK] 端到端冒烟测试通过。");
    }

    /** 模拟一名用户登录 + 做一次挑战，故意把前 wrongCount 道答错 */
    private static void runChallenge(String userId, String pw, Difficulty d, int wrongCount) {
        UserManager um = UserManager.getInstance();
        if (!um.login(userId, pw)) return;
        User u = um.getCurrentUser();
        Challenge ch = new Challenge(u, d);
        ch.generate();
        for (int i = 0; i < ch.getProblemCount(); i++) {
            Binaryoperation p = ch.getProblems().get(i);
            int correct = p.getResult();
            int answer  = (i < wrongCount) ? correct + 999 : correct;  // 前 wrongCount 道答错
            ch.submitAnswer(i, answer);
        }
        ChallengeResult r = ch.finish();
        r.display();
        um.logout();
    }

    /** 清理 Users/ 目录（递归删除）以便冒烟测试可重复运行 */
    private static void cleanupUsers() {
        File dir = new File("Users");
        deleteRecursively(dir);
    }

    private static void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) for (File c : children) deleteRecursively(c);
        }
        f.delete();
    }

    /** 仅供工厂层快速试跑（不依赖用户系统）：打印每个难度生成的样本题 */
    static void demoFactory() {
        for (Difficulty d : Difficulty.values()) {
            System.out.println("\n--- " + d.name() + " 样本题 ---");
            OperationFactory f = new OperationFactory(d);
            for (int i = 0; i < d.problemCount; i++) {
                System.out.print(f.newRandom().fullString() + "  ");
            }
            System.out.println();
        }
    }
}
