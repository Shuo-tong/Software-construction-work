import java.util.Random;

public class Division extends Binaryoperation {

    static final int DIV_UPPER = 10;  // 除数与商的上限 [1, 10]

    public Division() { operator = '/'; }

    @Override
    public int calculate() {
        return left_operand / right_operand;
    }

    @Override
    public boolean checkingCalculation() {
        return right_operand != 0 && left_operand % right_operand == 0;  // 确保整除
    }

    // 覆写生成方法：先取除数和商，再直接计算被除数，保证整除
    @Override
    protected void generateBinaryOperation() {
        Random random = new Random();
        right_operand = random.nextInt(DIV_UPPER) + 1;       // 除数 [1, 10]
        int quotient  = random.nextInt(DIV_UPPER) + 1;       // 商   [1, 10]
        left_operand  = right_operand * quotient;            // 被除数
        value = calculate();
    }

    public Division generate() {
        Division op = new Division();
        op.generateBinaryOperation();
        return op;
    }
}
