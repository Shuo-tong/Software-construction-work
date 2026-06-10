package arithmetic;

public class Addition extends Binaryoperation {

    // 加减共享数据基（单例），运行期纯查询
    private static final AdditiveBase BASE = AdditiveBase.getInstance();

    public Addition() { operator = '+'; }

    @Override
    public int calculate() {
        // 通过数据基查询 a + b，等价于 left_operand + right_operand
        return BASE.add(left_operand, right_operand);
    }

    @Override
    public boolean checkingCalculation() {
        // 单一真理源：合法性判定下沉到数据基
        return BASE.canAdd(left_operand, right_operand);
    }

    public Addition generate() {
        Addition op = new Addition();
        op.generateBinaryOperation();
        return op;
    }
}
