package arithmetic;

/**
 * 难度梯度枚举：L1 / L2 / L3。
 * 不设置时间限制，仅约束操作数范围、题量、积分系数与全对奖励。
 */
public enum Difficulty {
    /** 入门：加减 [0,20]，乘除 [1,5]，5 题 */
    L1(0, 20, 1, 5,  5, 1,  5),
    /** 进阶：加减 [0,50]，乘除 [1,8]，10 题 */
    L2(0, 50, 1, 8, 10, 2, 10),
    /** 满级：加减 [0,100]，乘除 [1,10]，20 题 */
    L3(0, 100, 1, 10, 20, 3, 15);

    /** 加减法操作数下限 */
    public final int addLow;
    /** 加减法操作数上限（也是和的上限、被减数的上限） */
    public final int addHigh;
    /** 乘除法操作数下限（同时也是除法商的下限） */
    public final int mulLow;
    /** 乘除法操作数上限（同时也是除法商的上限） */
    public final int mulHigh;
    /** 题量 */
    public final int problemCount;
    /** 单题分值 */
    public final int scorePerQuestion;
    /** 全对额外奖励 */
    public final int bonusAllRight;

    Difficulty(int addLow, int addHigh, int mulLow, int mulHigh,
               int problemCount, int scorePerQuestion, int bonusAllRight) {
        this.addLow = addLow;
        this.addHigh = addHigh;
        this.mulLow = mulLow;
        this.mulHigh = mulHigh;
        this.problemCount = problemCount;
        this.scorePerQuestion = scorePerQuestion;
        this.bonusAllRight = bonusAllRight;
    }

    /** 计算挑战最终得分：单题分 * 正确数 + 全对奖励（仅当全对触发） */
    public int computeScore(int correct, int total) {
        int base = correct * scorePerQuestion;
        return (correct == total && total > 0) ? base + bonusAllRight : base;
    }

    // =========================================================
    //  模式枚举：控制题目类型
    // =========================================================

    /** 题目模式：决定生成哪种类型的运算题 */
    public enum Mode {
        /** 加/减/乘/除随机混合（默认） */
        MIXED("混合运算", 1, "加减乘除随机混合"),
        /** 仅生成加法题 */
        ADD_ONLY("仅加法", 1, "全是加法"),
        /** 仅生成减法题 */
        SUB_ONLY("仅减法", 1, "全是减法"),
        /** 仅生成乘法和除法 */
        MUL_DIV("仅乘除", 1, "乘除各半");
        /** 长混合运算，题量翻倍 */
//        LONG_MIXED("长混合运算", 2, "题量翻倍，加减乘除混合");

        /** 显示名称 */
        public final String displayName;
        /** 题量倍数（LONG_MIXED 为 2，其余为 1） */
        public final int problemMultiplier;
        /** 简要说明 */
        public final String description;

        Mode(String displayName, int problemMultiplier, String description) {
            this.displayName = displayName;
            this.problemMultiplier = problemMultiplier;
            this.description = description;
        }
    }
}
