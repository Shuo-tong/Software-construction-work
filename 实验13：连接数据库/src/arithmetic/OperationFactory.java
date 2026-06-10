package arithmetic;

import java.util.Random;

/**
 * 按难度梯度生成各类运算题的工厂。
 * 通过 {@link Binaryoperation#setRange(int, int)} 注入难度对应的操作数取值范围。
 */
public class OperationFactory {
    private final Difficulty difficulty;
    private final Random random = new Random();

    public OperationFactory(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() { return difficulty; }

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

    /** 在加/减/乘/除四种里随机选一种生成 */
    public Binaryoperation newRandom() {
        switch (random.nextInt(4)) {
            case 0:  return newAddition();
            case 1:  return newSubstraction();
            case 2:  return newMultiplication();
            default: return newDivision();
        }
    }

    /**
     * 按难度生成一份混合题集（去重），题量由 difficulty.problemCount 决定。
     * 复用 {@link Exercise} 作为返回容器，与现有 CSV / 显示体系兼容。
     */
    public Exercise newExercise() {
        Exercise ex = new Exercise();
        int target = difficulty.problemCount;
        // 防止极端情况下题库太小导致死循环（如 L1 乘除可枚举对仅 25 个）
        int safeguard = target * 50;
        int tries = 0;
        while (ex.size() < target && tries++ < safeguard) {
            Binaryoperation op = newRandom();
            if (!ex.contains(op)) ex.add(op);
        }
        return ex;
    }
}
