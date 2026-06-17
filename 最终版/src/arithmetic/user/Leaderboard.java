package arithmetic.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 积分榜（独立单例）。
 * 不持有原始数据，只是 UserManager 的视图：每次查询时实时排序。
 * 排序规则：score desc，并列时 totalChallenges asc（同分时挑战次数少者居前），
 * 再并列时按 userId 字典序。
 */
public final class Leaderboard {
    private static final Leaderboard INSTANCE = new Leaderboard();

    private Leaderboard() { }

    public static Leaderboard getInstance() { return INSTANCE; }

    private static final Comparator<User> ORDER =
            Comparator.comparingInt(User::getScore).reversed()
                    .thenComparingInt(User::getTotalChallenges)
                    .thenComparing(User::getUserId);

    /** 返回积分榜前 n 名（n 超过总人数则全部返回） */
    public List<User> topN(int n) {
        List<User> all = new ArrayList<>(UserManager.getInstance().allUsers());
        all.sort(ORDER);
        if (n < 0) n = 0;
        return all.subList(0, Math.min(n, all.size()));
    }

    /** 返回完整榜单 */
    public List<User> all() {
        List<User> all = new ArrayList<>(UserManager.getInstance().allUsers());
        all.sort(ORDER);
        return all;
    }

    /** 返回某用户当前排名（从 1 起；用户不存在返回 -1） */
    public int rankOf(String userId) {
        List<User> sorted = all();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getUserId().equals(userId)) return i + 1;
        }
        return -1;
    }

    /** 屏幕展示前 n 名 */
    public void display(int n) {
        List<User> top = topN(n);
        System.out.println("================== 积分榜 TOP " + n + " ==================");
        if (top.isEmpty()) {
            System.out.println("暂无任何用户记录。");
        } else {
            System.out.printf("%-4s %-12s %-10s %-6s %-6s %-6s%n",
                    "名次", "账号", "昵称", "积分", "挑战", "正确");
            for (int i = 0; i < top.size(); i++) {
                User u = top.get(i);
                String medal = (i == 0) ? "[1]" : (i == 1) ? "[2]" : (i == 2) ? "[3]" :
                        String.format("%2d.", (i + 1));
                System.out.printf("%-4s %-12s %-10s %-6d %-6d %-6d%n",
                        medal, u.getUserId(), u.getNickname(),
                        u.getScore(), u.getTotalChallenges(), u.getTotalCorrect());
            }
        }
        System.out.println("===================================================");
    }
}
