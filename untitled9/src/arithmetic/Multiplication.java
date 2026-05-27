package arithmetic;

import java.util.Random;

public class Multiplication extends Binaryoperation {

    static final int MULT_UPPER = 10;  // 乘法操作数上限 [1, 10]

    // 乘除共享数据基（单例）
    private static final MultiplicativeBase BASE = MultiplicativeBase.getInstance();

    public Multiplication() { operator = '*'; }

    @Override
    public int calculate() {
        // 通过数据基查询 a * b
        return BASE.mul(left_operand, right_operand);
    }

    @Override
    public boolean checkingCalculation() {
        return BASE.canMul(left_operand, right_operand);
    }

    // 覆写生成方法：直接在 [1, MULT_UPPER] 范围内取操作数，而不是从 [0, UPPER] 内筛选
    @Override
    protected void generateBinaryOperation() {
        Random random = new Random();
        left_operand  = random.nextInt(MULT_UPPER) + 1;
        right_operand = random.nextInt(MULT_UPPER) + 1;
        value = calculate();
    }

    public Multiplication generate() {
        Multiplication op = new Multiplication();
        op.generateBinaryOperation();
        return op;
    }
}
