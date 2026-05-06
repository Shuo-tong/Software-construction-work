import java.util.Random;

public class Multiplication extends Binaryoperation {

    static final int MULT_UPPER = 10;  // 乘法操作数上限 [1, 10]

    public Multiplication() { operator = '*'; }

    @Override
    public int calculate() {
        return left_operand * right_operand;
    }

    @Override
    public boolean checkingCalculation() {
        return left_operand >= 1 && left_operand <= MULT_UPPER
            && right_operand >= 1 && right_operand <= MULT_UPPER;
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
