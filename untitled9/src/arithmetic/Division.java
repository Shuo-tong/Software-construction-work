package arithmetic;

import java.util.Random;

public class Division extends Binaryoperation {

    static final int DIV_UPPER = 10;  // 除数与商的上限 [1, 10]

    // 乘除共享数据基（与 Multiplication 复用同一份数据）
    private static final MultiplicativeBase BASE = MultiplicativeBase.getInstance();

    public Division() { operator = '/'; }

    @Override
    public int calculate() {
        // 反向查表：在乘法表中找 q 使 b * q == a
        return BASE.div(left_operand, right_operand);
    }

    @Override
    public boolean checkingCalculation() {
        return BASE.canDiv(left_operand, right_operand);
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
