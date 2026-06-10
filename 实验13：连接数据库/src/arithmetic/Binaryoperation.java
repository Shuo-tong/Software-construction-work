package arithmetic;

import java.util.Random;

public abstract class Binaryoperation {
    static final int UPPER = 100;
    static final int LOWER = 0;
    protected int left_operand = 0, right_operand = 0;
    protected char operator = '+';
    protected int value = 0;

    // 操作数随机生成范围（实例字段，可由难度梯度注入）
    protected int rangeLow  = LOWER;
    protected int rangeHigh = UPPER;

    // 抽象方法：计算运算结果
    public abstract int calculate();

    // 抽象方法：检验当前操作数是否构成合法运算
    public abstract boolean checkingCalculation();

    /** 设置操作数随机生成范围（供 OperationFactory / 难度系统注入） */
    public void setRange(int low, int high) {
        this.rangeLow  = low;
        this.rangeHigh = high;
    }
    public int getRangeLow()  { return rangeLow; }
    public int getRangeHigh() { return rangeHigh; }

    // 模板方法：随机生成操作数，直到通过检验，再计算结果
    protected void generateBinaryOperation() {
        Random random = new Random();
        int span = rangeHigh - rangeLow + 1;
        do {
            left_operand  = random.nextInt(span) + rangeLow;
            right_operand = random.nextInt(span) + rangeLow;
        } while (!checkingCalculation());
        value = calculate();
    }

    // 实例变量访问器
    public int getLeftOperand() { return left_operand; }
    public int getRightOperand() { return right_operand; }
    public char getOperator() { return operator; }
    public int getResult() { return value; }

    // 实例变量设置器（供测试使用）
    public void setLeftOperand(int left)   { left_operand  = left; }
    public void setRightOperand(int right) { right_operand = right; }
    /** 设置运算结果（供 CSV 反序列化 / 错题本还原使用） */
    public void setValue(int v)            { value = v; }

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
