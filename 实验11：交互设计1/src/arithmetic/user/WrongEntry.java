package arithmetic.user;

import arithmetic.Binaryoperation;
import arithmetic.Difficulty;

/**
 * 错题条目：记录一道做错的题目以及相关元数据。
 */
public class WrongEntry {
    /** 题目本身（含正确答案，复用 Binaryoperation 已有 equals/toString） */
    private final Binaryoperation problem;
    /** 用户当时填写的答案 */
    private int userAnswer;
    /** 错题来自哪个难度 */
    private final Difficulty difficulty;
    /** 错题首次记录时间（毫秒时间戳） */
    private final long timestamp;
    /** 重做次数（同一题再次做错时累加） */
    private int retryCount;
    /** 是否已掌握（用户标记） */
    private boolean mastered;

    public WrongEntry(Binaryoperation problem, int userAnswer, Difficulty difficulty) {
        this.problem = problem;
        this.userAnswer = userAnswer;
        this.difficulty = difficulty;
        this.timestamp = System.currentTimeMillis();
        this.retryCount = 0;
        this.mastered = false;
    }

    /** 完整构造（供 CSV 反序列化使用） */
    public WrongEntry(Binaryoperation problem, int userAnswer, Difficulty difficulty,
                      long timestamp, int retryCount, boolean mastered) {
        this.problem = problem;
        this.userAnswer = userAnswer;
        this.difficulty = difficulty;
        this.timestamp = timestamp;
        this.retryCount = retryCount;
        this.mastered = mastered;
    }

    public Binaryoperation getProblem()   { return problem; }
    public int getUserAnswer()             { return userAnswer; }
    public Difficulty getDifficulty()      { return difficulty; }
    public long getTimestamp()             { return timestamp; }
    public int getRetryCount()             { return retryCount; }
    public boolean isMastered()            { return mastered; }

    public void setUserAnswer(int ans)     { this.userAnswer = ans; }
    public void incRetry()                 { this.retryCount++; }
    public void markMastered()             { this.mastered = true; }
    public void setMastered(boolean m)     { this.mastered = m; }

    /** 单行展示：题目=学生答案 [标准答案=X] 难度=L? 重做=N (✓掌握) */
    @Override
    public String toString() {
        return String.format("%s=%-3d [正确=%d] 难度=%s 重做=%d%s",
                problem.toString(), userAnswer, problem.getResult(),
                difficulty.name(), retryCount,
                mastered ? " (已掌握)" : "");
    }
}
