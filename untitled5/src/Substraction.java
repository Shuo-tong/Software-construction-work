public class Substraction extends Binaryoperation {

    public Substraction() { operator = '-'; }

    @Override
    public int calculate() {
        return left_operand - right_operand;
    }

    @Override
    public boolean checkingCalculation() {
        return right_operand <= left_operand;  // 确保结果不小于 LOWER(0)
    }

    public Substraction generate() {
        Substraction op = new Substraction();
        op.generateBinaryOperation();
        return op;
    }
}
