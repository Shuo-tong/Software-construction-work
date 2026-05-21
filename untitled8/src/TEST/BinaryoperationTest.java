package TEST;

import arithmetic.Addition;
import arithmetic.Substraction;
import arithmetic.Binaryoperation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryoperationTest {

    @Test
    void calculate() {
        // 序号1：70, 30, '+' → 预期 100
        Binaryoperation op1 = new Addition();
        op1.setLeftOperand(70);
        op1.setRightOperand(30);
        assertEquals(100, op1.calculate());

        // 序号2：100, 1, '-' → 预期 99
        Binaryoperation op2 = new Substraction();
        op2.setLeftOperand(100);
        op2.setRightOperand(1);
        assertEquals(99, op2.calculate());

        // 序号3：100, 0, '-' → 预期 100
        Binaryoperation op3 = new Substraction();
        op3.setLeftOperand(100);
        op3.setRightOperand(0);
        assertEquals(100, op3.calculate());
    }

    @Test
    void boundaryCalculate() {


        // 两个操作数均为最小値 0
        Binaryoperation addMin = new Addition();
        addMin.setLeftOperand(0);
        addMin.setRightOperand(0);
        assertEquals(0, addMin.calculate());

        // 左操作数取上限 100，右操作数为 0
        Binaryoperation addMaxLeft = new Addition();
        addMaxLeft.setLeftOperand(100);
        addMaxLeft.setRightOperand(0);
        assertEquals(100, addMaxLeft.calculate());

        // 左操作数为 0，右操作数取上限 100
        Binaryoperation addMaxRight = new Addition();
        addMaxRight.setLeftOperand(0);
        addMaxRight.setRightOperand(100);
        assertEquals(100, addMaxRight.calculate());

        Binaryoperation addBoundary = new Addition();
        addBoundary.setLeftOperand(50);
        addBoundary.setRightOperand(50);
        assertEquals(100, addBoundary.calculate());


        // 两个操作数均为 0，结果为最小値 0
        Binaryoperation subMin = new Substraction();
        subMin.setLeftOperand(0);
        subMin.setRightOperand(0);
        assertEquals(0, subMin.calculate());

        // 两个操作数相等，结果为 0
        Binaryoperation subEqual = new Substraction();
        subEqual.setLeftOperand(100);
        subEqual.setRightOperand(100);
        assertEquals(0, subEqual.calculate());

        // 左操作数取上限 100，右操作数为 0，结果为最大値 100
        Binaryoperation subMaxLeft = new Substraction();
        subMaxLeft.setLeftOperand(100);
        subMaxLeft.setRightOperand(0);
        assertEquals(100, subMaxLeft.calculate());
    }
}