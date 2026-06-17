package arithmetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    //  CSV 文件读写方法
    // ========================

    /** 自动查找下一个可用的文件编号（扫描目录中已有文件） */
    private int getNextFileNumber(File dir, String prefix) {
        int maxNum = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                if (name.startsWith(prefix) && name.endsWith(".csv")) {
                    // 提取编号部分：prefix 后、.csv 前的三位数字
                    String numStr = name.substring(prefix.length(), name.length() - 4);
                    try {
                        int num = Integer.parseInt(numStr);
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return maxNum + 1;
    }

    /** 生成 count 道加法题并以 CSV 格式写入文件，存储到 Execises 文件夹，编号自动递增 */
    public void writeCSVAddtionExercise(int count) {
        generateAdditionExercise(count);
        // 确保 Execises 目录存在
        File dir = new File("Exercises");
        if (!dir.exists()) dir.mkdirs();
        // 自动获取下一个文件编号
        String prefix = String.format("addition_exercise_%d_", count);
        int fileNum = getNextFileNumber(dir, prefix);
        String fileName = String.format("Exercises/%s%03d.csv", prefix, fileNum);
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            int columnsPerRow = 5;
            for (int i = 0; i < this.size(); i++) {
                if (i > 0 && i % columnsPerRow == 0) pw.println();
                if (i % columnsPerRow != 0) pw.print(",");
                pw.print(this.get(i).toString());
            }
            pw.println();
            System.out.println("习题已写入文件: " + fileName);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }

    /**
     * 从 CSV 文件中读入加法习题，还原为 Exercise 对象。
     * 使用正则表达式解析算式，容忍多余空格等格式差异。
     */
    public void readCSVAddtionExercise(File csvFile) {
        this.clear();
        // 正则：匹配形如 "51+11" 的加法算式，允许数字和加号之间有空格
        Pattern pattern = Pattern.compile("(\\d+)\\s*\\+\\s*(\\d+)");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 按逗号分割每个算式
                String[] tokens = line.split(",");
                for (String token : tokens) {
                    token = token.trim();
                    if (token.isEmpty()) continue;
                    Matcher matcher = pattern.matcher(token);
                    if (matcher.find()) {
                        int left = Integer.parseInt(matcher.group(1));
                        int right = Integer.parseInt(matcher.group(2));
                        Addition op = new Addition();
                        op.setLeftOperand(left);
                        op.setRightOperand(right);
                        op.value = op.calculate();
                        this.add(op);
                    }
                }
            }
            System.out.println("从文件读入 " + this.size() + " 道加法题: " + csvFile.getName());
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }

    /** 批量生成 number 个加法习题集，每个包含 count 道题，分别存入单独的 CSV 文件 */
    public void writeCSVAddtionExercises(int number, int count) {
        for (int i = 0; i < number; i++) {
            writeCSVAddtionExercise(count);
        }
        System.out.println("批量生成完成：共 " + number + " 个加法习题文件，每个 " + count + " 道题");
    }

    // ---------- 减法 CSV 读写 ----------

    /** 生成 count 道减法题并以 CSV 格式写入文件，编号自动递增 */
    public void writeCSVSubstractExercise(int count) {
        generateSubstractExercise(count);
        File dir = new File("Exercises");
        if (!dir.exists()) dir.mkdirs();
        String prefix = String.format("substract_exercise_%d_", count);
        int fileNum = getNextFileNumber(dir, prefix);
        String fileName = String.format("Exercises/%s%03d.csv", prefix, fileNum);
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            int columnsPerRow = 5;
            for (int i = 0; i < this.size(); i++) {
                if (i > 0 && i % columnsPerRow == 0) pw.println();
                if (i % columnsPerRow != 0) pw.print(",");
                pw.print(this.get(i).toString());
            }
            pw.println();
            System.out.println("习题已写入文件: " + fileName);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }

    /** 从 CSV 文件中读入减法习题，还原为 Exercise 对象 */
    public void readCSVSubstractExercise(File csvFile) {
        this.clear();
        Pattern pattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                for (String token : tokens) {
                    token = token.trim();
                    if (token.isEmpty()) continue;
                    Matcher matcher = pattern.matcher(token);
                    if (matcher.find()) {
                        int left = Integer.parseInt(matcher.group(1));
                        int right = Integer.parseInt(matcher.group(2));
                        Substraction op = new Substraction();
                        op.setLeftOperand(left);
                        op.setRightOperand(right);
                        op.value = op.calculate();
                        this.add(op);
                    }
                }
            }
            System.out.println("从文件读入 " + this.size() + " 道减法题: " + csvFile.getName());
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }

    /** 批量生成 number 个减法习题集 */
    public void writeCSVSubstractExercises(int number, int count) {
        for (int i = 0; i < number; i++) {
            writeCSVSubstractExercise(count);
        }
        System.out.println("批量生成完成：共 " + number + " 个减法习题文件，每个 " + count + " 道题");
    }

    // ---------- 混合运算 CSV 读写 ----------

    /** 生成 count 道混合运算题并以 CSV 格式写入文件，编号自动递增 */
    public void writeCSVBinaryExercise(int count) {
        generateBinaryExercise(count);
        File dir = new File("Exercises");
        if (!dir.exists()) dir.mkdirs();
        String prefix = String.format("binary_exercise_%d_", count);
        int fileNum = getNextFileNumber(dir, prefix);
        String fileName = String.format("Exercises/%s%03d.csv", prefix, fileNum);
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            int columnsPerRow = 5;
            for (int i = 0; i < this.size(); i++) {
                if (i > 0 && i % columnsPerRow == 0) pw.println();
                if (i % columnsPerRow != 0) pw.print(",");
                pw.print(this.get(i).toString());
            }
            pw.println();
            System.out.println("习题已写入文件: " + fileName);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }

    /**
     * 从 CSV 文件中读入混合运算习题，根据运算符自动创建对应类型的对象。
     * 支持 +、-、*、/ 四种运算符。
     */
    public void readCSVBinaryExercise(File csvFile) {
        this.clear();
        Pattern pattern = Pattern.compile("(\\d+)\\s*([+\\-*/])\\s*(\\d+)");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                for (String token : tokens) {
                    token = token.trim();
                    if (token.isEmpty()) continue;
                    Matcher matcher = pattern.matcher(token);
                    if (matcher.find()) {
                        int left = Integer.parseInt(matcher.group(1));
                        char oper = matcher.group(2).charAt(0);
                        int right = Integer.parseInt(matcher.group(3));
                        Binaryoperation op;
                        switch (oper) {
                            case '+': op = new Addition();       break;
                            case '-': op = new Substraction();   break;
                            case '*': op = new Multiplication(); break;
                            case '/': op = new Division();       break;
                            default: continue;
                        }
                        op.setLeftOperand(left);
                        op.setRightOperand(right);
                        op.value = op.calculate();
                        this.add(op);
                    }
                }
            }
            System.out.println("从文件读入 " + this.size() + " 道混合题: " + csvFile.getName());
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }

    /** 批量生成 number 个混合运算习题集 */
    public void writeCSVBinaryExercises(int number, int count) {
        for (int i = 0; i < number; i++) {
            writeCSVBinaryExercise(count);
        }
        System.out.println("批量生成完成：共 " + number + " 个混合运算习题文件，每个 " + count + " 道题");
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
