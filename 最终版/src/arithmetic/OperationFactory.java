package arithmetic;

import java.util.Random;

/**
 * 按难度梯度 + 模式生成各类运算题的工厂。
 * 通过 {@link Binaryoperation#setRange(int, int)} 注入难度对应的操作数取值范围。
 */
public class OperationFactory {
    private final Difficulty difficulty;
    private final Difficulty.Mode mode;
    private final Random random = new Random();

    public OperationFactory(Difficulty difficulty) {
        this(difficulty, Difficulty.Mode.MIXED);
    }

    public OperationFactory(Difficulty difficulty, Difficulty.Mode mode) {
        this.difficulty = difficulty;
        this.mode = mode;
    }

    public Difficulty getDifficulty() { return difficulty; }
    public Difficulty.Mode getMode()  { return mode; }

    /** 生成一道加法题（范围由难度决定） */
    public Addition newAddition() {
        Addition op = new Addition();
        op.setRange(difficulty.addLow, difficulty.addHigh);
        op.generateBinaryOperation();
        return op;
    }

    /** 生成一道减法题 */
    public Substraction newSubstraction() {
        Substraction op = new Substraction();
        op.setRange(difficulty.addLow, difficulty.addHigh);
        op.generateBinaryOperation();
        return op;
    }

    /** 生成一道乘法题 */
    public Multiplication newMultiplication() {
        Multiplication op = new Multiplication();
        op.setRange(difficulty.mulLow, difficulty.mulHigh);
        op.generateBinaryOperation();
        return op;
    }

    /** 生成一道除法题 */
    public Division newDivision() {
        Division op = new Division();
        op.setRange(difficulty.mulLow, difficulty.mulHigh);
        op.generateBinaryOperation();
        return op;
    }

    /**
     * 根据当前模式生成一道随机题。
     *  - MIXED / LONG_MIXED：加减乘除等概率
     *  - ADD_ONLY：仅加法
     *  - SUB_ONLY：仅减法
     *  - MUL_DIV：乘除等概率
     */
    public Binaryoperation newRandom() {
        switch (mode) {
            case ADD_ONLY:
                return newAddition();
            case SUB_ONLY:
                return newSubstraction();
            case MUL_DIV:
                return random.nextBoolean() ? newMultiplication() : newDivision();
            case MIXED:
//            case LONG_MIXED:
            default:
                return randomFourOps();
        }
    }

    /** 加减乘除等概率 */
    private Binaryoperation randomFourOps() {
        switch (random.nextInt(4)) {
            case 0:  return newAddition();
            case 1:  return newSubstraction();
            case 2:  return newMultiplication();
            default: return newDivision();
        }
    }

    /**
     * 按难度和模式生成一份混合题集（去重），
     * 题量 = difficulty.problemCount × mode.problemMultiplier。
     * 复用 {@link Exercise} 作为返回容器。
     */
    public Exercise newExercise() {
        Exercise ex = new Exercise();
        int target = difficulty.problemCount * mode.problemMultiplier;
        // 防止极端情况下题库太小导致死循环
        int safeguard = target * 50;
        int tries = 0;
        while (ex.size() < target && tries++ < safeguard) {
            Binaryoperation op = newRandom();
            if (!ex.contains(op)) ex.add(op);
        }
        return ex;
    }
}
