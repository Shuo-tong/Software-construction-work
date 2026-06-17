package TEST;

import arithmetic.Addition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdditionTest {

    @Test
    void calculate() {
        Addition op = new Addition();
        op.setLeftOperand(30);
        op.setRightOperand(10);
        assertEquals(40, op.calculate());
    }

    @Test
    void checkingCalculation() {
        Addition op = new Addition();
        // 和未超过上限，应返回 true
        op.setLeftOperand(50);
        op.setRightOperand(50);
        assertTrue(op.checkingCalculation());
        // 和超过上限，应返回 false
        op.setLeftOperand(60);
        op.setRightOperand(50);
        assertFalse(op.checkingCalculation());
    }

    @Test
    void generate() {
        Addition generated = new Addition().generate();
        assertNotNull(generated);
        // 结果应等于两操作数之和
        assertEquals(generated.getLeftOperand() + generated.getRightOperand(), generated.getResult());
        // 结果不应超过上限 100
        assertTrue(generated.getResult() <= 100);
    }
}
