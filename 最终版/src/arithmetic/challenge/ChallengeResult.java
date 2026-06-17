package arithmetic.challenge;

import arithmetic.Binaryoperation;
import arithmetic.Difficulty;
import arithmetic.user.User;

import java.util.List;

/**
 * 挑战结果（值对象）。
 * 由 {@link Challenge#finish()} 返回，并由 ChallengeRunner 展示。
 */
public class ChallengeResult {
    private final User user;
    private final Difficulty difficulty;
    private final Difficulty.Mode mode;
    private final int total;
    private final int correct;
    private final int wrong;
    private final int scoreEarned;
    private final long timeUsedMs;
    private final List<Binaryoperation> wrongProblems;

    public ChallengeResult(User user, Difficulty difficulty, Difficulty.Mode mode,
                           int total, int correct, int wrong,
                           int scoreEarned, long timeUsedMs,
                           List<Binaryoperation> wrongProblems) {
        this.user = user;
        this.difficulty = difficulty;
        this.mode = mode;
        this.total = total;
        this.correct = correct;
        this.wrong = wrong;
        this.scoreEarned = scoreEarned;
        this.timeUsedMs = timeUsedMs;
        this.wrongProblems = wrongProblems;
    }

    public User getUser()                          { return user; }
    public Difficulty getDifficulty()              { return difficulty; }
    public Difficulty.Mode getMode()               { return mode; }
    public int getTotal()                          { return total; }
    public int getCorrect()                        { return correct; }
    public int getWrong()                          { return wrong; }
    public int getScoreEarned()                    { return scoreEarned; }
    public long getTimeUsedMs()                    { return timeUsedMs; }
    public List<Binaryoperation> getWrongProblems(){ return wrongProblems; }

    /** 挑战结束屏幕展示 */
    public void display() {
        System.out.println("============ 挑战结果 ============");
        System.out.println("玩家：" + user.getNickname() + " (" + user.getUserId() + ")");
        System.out.println("难度：" + difficulty.name() + "  |  模式：" + mode.displayName);
        System.out.println("题数：" + total + "  正确：" + correct + "  错误：" + wrong);
        System.out.println("耗时：" + (timeUsedMs / 1000.0) + " 秒");
        System.out.println("本次获得积分：" + scoreEarned);
        System.out.println("当前总积分：" + user.getScore());
        if (!wrongProblems.isEmpty()) {
            System.out.println("--- 错题清单（已计入错题本） ---");
            for (Binaryoperation p : wrongProblems) {
                System.out.println("  " + p.fullString());
            }
        } else if (correct == total && total > 0) {
            System.out.println("[全对] 触发奖励 +" + difficulty.bonusAllRight + " 分！");
        }
        System.out.println("==================================");
    }
}
