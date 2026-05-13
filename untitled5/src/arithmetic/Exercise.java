package arithmetic;

import java.util.ArrayList;
import java.util.Random;

/**
 * Exercise 直接继承 ArrayList<Binaryoperation>，本身就是一个运算题列表。
 * 提供生成方法和多种内置显示格式，用户直接调用，无需自定义。
 */
public class Exercise extends ArrayList<Binaryoperation> {

    /** 生成 operationCount 道加法题 */
    public void generateAdditionExercise(int operationCount) {
        this.clear();
        Addition creator = new Addition();
        for (int i = 0; i < operationCount; i++) {
            Binaryoperation op;
            do { op = creator.generate(); } while (this.contains(op));
            this.add(op);
        }
    }

    /** 生成 operationCount 道减法题 */
    public void generateSubstractExercise(int operationCount) {
        this.clear();
        Substraction creator = new Substraction();
        for (int i = 0; i < operationCount; i++) {
            Binaryoperation op;
            do { op = creator.generate(); } while (this.contains(op));
            this.add(op);
        }
    }

    /** 生成 operationCount 道加减法混合题 */
    public void generateBinaryExercise(int operationCount) {
        this.clear();
        Random random = new Random();
        Addition addC = new Addition(); Substraction subC = new Substraction();
        Multiplication mulC = new Multiplication(); Division divC = new Division();
        for (int i = 0; i < operationCount; i++) {
            Binaryoperation op;
            do {
                switch (random.nextInt(4)) {
                    case 0:  op = addC.generate();  break;
                    case 1:  op = subC.generate();  break;
                    case 2:  op = mulC.generate();  break;
                    default: op = divC.generate();  break;
                }
            } while (this.contains(op));
            this.add(op);
        }
    }

    /** 生成 operationCount 道乘法题 */
    public void generateMultiplicationExercise(int operationCount) {
        this.clear();
        Multiplication creator = new Multiplication();
        for (int i = 0; i < operationCount; i++) {
            Binaryoperation op;
            do { op = creator.generate(); } while (this.contains(op));
            this.add(op);
        }
    }

    /** 生成 operationCount 道除法题 */
    public void generateDivisionExercise(int operationCount) {
        this.clear();
        Division creator = new Division();
        for (int i = 0; i < operationCount; i++) {
            Binaryoperation op;
            do { op = creator.generate(); } while (this.contains(op));
            this.add(op);
        }
    }

    // ========================
    //  显示方法（内置，用户直接调用）
    // ========================

    /** 通用格式化显示：columnsPerRow 列/行，只显示算式 */
    public void formateAndDisplay(int columnsPerRow) {
        for (int i = 0; i < this.size(); i++) {
            System.out.printf("%-12s", this.get(i).asString());
            if ((i + 1) % columnsPerRow == 0) System.out.println();
        }
        if (this.size() % columnsPerRow != 0) System.out.println();
    }

    /** 格式1：编号列表，每行一题（适合考试作答） */
    public void formattedDisplay_1() {
        for (int i = 0; i < this.size(); i++) {
            System.out.printf("%2d. %s%n", (i + 1), this.get(i).asString());
        }
    }

    /** 格式2：5列紧凑表格，仅算式（适合练习卡片） */
    public void formattedDisplay_2() {
        formateAndDisplay(5);
    }

    /** 格式3：4列含完整答案（适合自查/答案键） */
    public void formattedDisplay_3() {
        for (int i = 0; i < this.size(); i++) {
            System.out.printf("%-16s", this.get(i).fullString());
            if ((i + 1) % 4 == 0) System.out.println();
        }
        if (this.size() % 4 != 0) System.out.println();
    }

    /** 格式0：正式试卷（带页眉/姓名栏，适合打印发放） */
    public void formattedDisplay() {
        String border = "============================================================";
        System.out.println(border);
        System.out.println("                 口算练习卷");
        System.out.println(border);
        for (int i = 0; i < this.size(); i++) {
            System.out.printf("%2d. %-14s", (i + 1), this.get(i).asString());
            if ((i + 1) % 4 == 0) System.out.println();
        }
        if (this.size() % 4 != 0) System.out.println();
        System.out.println(border);
    }
}
