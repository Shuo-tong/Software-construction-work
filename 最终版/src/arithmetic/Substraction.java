package arithmetic;

public class Substraction extends Binaryoperation {

    // 加减共享数据基（与 Addition 复用同一份数据）
    private static final AdditiveBase BASE = AdditiveBase.getInstance();

    public Substraction() { operator = '-'; }

    @Override
    public int calculate() {
        // 复用加法表：a - b = sum(a, b) - 2b
        return BASE.sub(left_operand, right_operand);
    }

    @Override
    public boolean checkingCalculation() {
        return BASE.canSub(left_operand, right_operand);
    }

    public Substraction generate() {
        Substraction op = new Substraction();
        op.generateBinaryOperation();
        return op;
    }
}
