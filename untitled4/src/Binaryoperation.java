import java.util.Random;

public abstract class Binaryoperation {
    static final int UPPER = 100;
    static final int LOWER = 0;
    protected int left_operand = 0, right_operand = 0;
    protected char operator = '+';
    protected int value = 0;

    // 抽象方法：计算运算结果
    public abstract int calculate();

    // 抽象方法：检验当前操作数是否构成合法运算
    public abstract boolean checkingCalculation();

    // 模板方法：随机生成操作数，直到通过检验，再计算结果
    protected void generateBinaryOperation() {
        Random random = new Random();
        do {
            left_operand = random.nextInt(UPPER + 1);
            right_operand = random.nextInt(UPPER + 1);
        } while (!checkingCalculation());
        value = calculate();
    }

    // 实例变量访问器
    public int getLeftOperand() { return left_operand; }
    public int getRightOperand() { return right_operand; }
    public char getOperator() { return operator; }
    public int getResult() { return value; }

    // 覆写 Object.equals()，使 ArrayList.contains() 能正确判断重复
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Binaryoperation)) return false;
        Binaryoperation other = (Binaryoperation) obj;
        return left_operand == other.getLeftOperand()
            && right_operand == other.getRightOperand()
            && operator == other.getOperator();
    }

    public String toString()  { return left_operand + "" + operator + right_operand; }  // 示例："32+5"
    public String asString()  { return toString() + "="; }                              // 示例："32+5="
    public String fullString(){ return asString() + value; }                            // 示例："32+5=37"
}
