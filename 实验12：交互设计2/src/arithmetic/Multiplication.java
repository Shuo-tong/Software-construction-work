package arithmetic;

import java.util.Random;

public class Multiplication extends Binaryoperation {

    static final int MULT_UPPER = 10;  // 乘法操作数上限 [1, 10]

    // 乘除共享数据基（单例）
    private static final MultiplicativeBase BASE = MultiplicativeBase.getInstance();

    public Multiplication() {
        operator = '*';
        // 默认范围 [1, MULT_UPPER]
        rangeLow  = 1;
        rangeHigh = MULT_UPPER;
    }

    @Override
    public int calculate() {
        // 通过数据基查询 a * b
        return BASE.mul(left_operand, right_operand);
    }

    @Override
    public boolean checkingCalculation() {
        return BASE.canMul(left_operand, right_operand);
    }

    // 覆写生成方法：直接在 [rangeLow, rangeHigh] 范围内取操作数
    @Override
    protected void generateBinaryOperation() {
        Random random = new Random();
        int span = rangeHigh - rangeLow + 1;
        left_operand  = random.nextInt(span) + rangeLow;
        right_operand = random.nextInt(span) + rangeLow;
        value = calculate();
    }

    public Multiplication generate() {
        Multiplication op = new Multiplication();
        op.generateBinaryOperation();
        return op;
    }
}
