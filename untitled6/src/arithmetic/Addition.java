package arithmetic;

public class Addition extends Binaryoperation {

    public Addition() { operator = '+'; }

    @Override
    public int calculate() {
        return left_operand + right_operand;
    }

    @Override
    public boolean checkingCalculation() {
        return (left_operand + right_operand) <= UPPER;  // 确保和不超过上限
    }

    public Addition generate() {
        Addition op = new Addition();
        op.generateBinaryOperation();
        return op;
    }
}
