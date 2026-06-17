package TEST;

import arithmetic.Exercise;
import arithmetic.Judgement;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * 测试类：用于测试 Exercise 的 CSV 文件读写功能
 */
public class ExerciseFileTester {

    /** 测试 writeCSVAddtionExercise：生成加法习题并写入 CSV 文件 */
    public static void testWriteCSVAddtionExercise() {
        Exercise exercise = new Exercise();
        // 生成 20 道加法题并写入文件
        exercise.writeCSVAddtionExercise(20);

        // 验证文件是否生成成功
        File dir = new File("Exercises");
        String prefix = "addition_exercise_20_";
        boolean found = false;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().startsWith(prefix) && f.getName().endsWith(".csv")) {
                    found = true;
                    System.out.println("文件生成成功: " + f.getName());
                    System.out.println("文件大小: " + f.length() + " 字节");
                }
            }
        }
        if (!found) {
            System.out.println("测试失败：未找到生成的习题文件！");
        }

        // 在屏幕上同时显示当前习题内容以便对照
        System.out.println("\n--- 当前习题内容（屏幕输出） ---");
        exercise.formattedDisplay_2();
    }

    /** 测试 readCSVAddtionExercise：从 CSV 文件读入加法习题并屏幕输出验证 */
    public static void testReadCSVAddtionExercise() {
        // 查找 Exercises 文件夹中最新的加法习题文件
        File dir = new File("Exercises");
        File targetFile = null;
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().startsWith("addition_exercise_") && f.getName().endsWith(".csv")) {
                        targetFile = f;  // 取最后一个匹配的文件
                    }
                }
            }
        }
        if (targetFile == null) {
            System.out.println("测试失败：Exercises 文件夹中未找到加法习题文件！");
            return;
        }

        System.out.println("读取文件: " + targetFile.getName());
        Exercise exercise = new Exercise();
        exercise.readCSVAddtionExercise(targetFile);

        // 屏幕输出读入的结果
        System.out.println("\n--- 读入的习题内容 ---");
        exercise.formattedDisplay_2();

        // 输出带答案的完整格式以便验证正确性
        System.out.println("\n--- 带答案的完整内容 ---");
        exercise.formattedDisplay_3();
    }

    // ---------- 减法测试 ----------

    /** 测试 writeCSVSubstractExercise：生成减法习题并写入 CSV 文件 */
    public static void testWriteCSVSubstractExercise() {
        Exercise exercise = new Exercise();
        exercise.writeCSVSubstractExercise(20);
        System.out.println("--- 当前减法习题内容 ---");
        exercise.formattedDisplay_2();
    }

    /** 测试 readCSVSubstractExercise：从 CSV 文件读入减法习题 */
    public static void testReadCSVSubstractExercise() {
        File dir = new File("Exercises");
        File targetFile = null;
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().startsWith("substract_exercise_") && f.getName().endsWith(".csv")) {
                        targetFile = f;
                    }
                }
            }
        }
        if (targetFile == null) {
            System.out.println("测试失败：未找到减法习题文件！");
            return;
        }
        System.out.println("读取文件: " + targetFile.getName());
        Exercise exercise = new Exercise();
        exercise.readCSVSubstractExercise(targetFile);
        System.out.println("--- 读入的减法习题 ---");
        exercise.formattedDisplay_2();
        System.out.println("--- 带答案的完整内容 ---");
        exercise.formattedDisplay_3();
    }

    // ---------- 混合运算测试 ----------

    /** 测试 writeCSVBinaryExercise：生成混合运算习题并写入 CSV 文件 */
    public static void testWriteCSVBinaryExercise() {
        Exercise exercise = new Exercise();
        exercise.writeCSVBinaryExercise(20);
        System.out.println("--- 当前混合习题内容 ---");
        exercise.formattedDisplay_2();
    }

    /** 测试 readCSVBinaryExercise：从 CSV 文件读入混合运算习题 */
    public static void testReadCSVBinaryExercise() {
        File dir = new File("Exercises");
        File targetFile = null;
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().startsWith("binary_exercise_") && f.getName().endsWith(".csv")) {
                        targetFile = f;
                    }
                }
            }
        }
        if (targetFile == null) {
            System.out.println("测试失败：未找到混合运算习题文件！");
            return;
        }
        System.out.println("读取文件: " + targetFile.getName());
        Exercise exercise = new Exercise();
        exercise.readCSVBinaryExercise(targetFile);
        System.out.println("--- 读入的混合习题 ---");
        exercise.formattedDisplay_2();
        System.out.println("--- 带答案的完整内容 ---");
        exercise.formattedDisplay_3();
    }

    // ---------- 完整读图判题流程测试 ----------

    /**
     * 测试完整的“读练习+判题”流程：
     *  1) 生成一份加法习题文件（已知答案）
     *  2) 模拟家长录入小明的练习结果（故意制造错答和未答）
     *  3) 调用 Judgement 进行批改、显示、写入文件
     */
    public static void testJudgement() {
        // 步骤1：生成加法习题
        System.out.println("--- 步骤1：生成加法习题 ---");
        Exercise exercise = new Exercise();
        int count = 10;
        exercise.writeCSVAddtionExercise(count);

        // 找到刚生成的最新习题文件（按名字最大）
        File dir = new File("Exercises");
        File latestExercise = null;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().startsWith("addition_exercise_" + count + "_") && f.getName().endsWith(".csv")) {
                    if (latestExercise == null || f.getName().compareTo(latestExercise.getName()) > 0) {
                        latestExercise = f;
                    }
                }
            }
        }
        if (latestExercise == null) {
            System.out.println("测试失败：未找到习题文件！");
            return;
        }
        System.out.println("习题文件: " + latestExercise.getName());

        // 步骤2：读回习题获取标准答案，构造模拟练习结果
        System.out.println("\n--- 步骤2：模拟小明作答（故意错答2道、未答1道） ---");
        Exercise key = new Exercise();
        key.readCSVAddtionExercise(latestExercise);

        int[] studentAnswers = new int[count];
        for (int i = 0; i < count; i++) {
            studentAnswers[i] = key.get(i).getResult();
        }
        if (count >= 2) studentAnswers[1] += 10;   // 错答
        if (count >= 5) studentAnswers[4] += 5;    // 错答
        if (count >= 8) studentAnswers[7] = -1;    // 未作答

        // 写入练习结果 CSV（含文件头）
        String exerciseBase = latestExercise.getName().replace(".csv", "");
        // 要求的练习名格式：practice_ae_xxx（ae=addition exercise）
        String practiceBase = exerciseBase.replace("addition_exercise", "practice_ae");
        File practiceFile = new File("Exercises/" + practiceBase + ".csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(practiceFile))) {
            pw.println("练习：" + exerciseBase);
            for (int i = 0; i < count; i++) {
                pw.print(studentAnswers[i]);
                if (i < count - 1) pw.print(",");
                if ((i + 1) % 5 == 0) pw.println();
            }
            pw.println();
        } catch (Exception e) {
            System.err.println("写入练习文件失败: " + e.getMessage());
            return;
        }
        System.out.println("练习文件已生成: " + practiceFile.getName());

        // 步骤3：调用 Judgement 一键批改
        System.out.println("\n--- 步骤3：调用 Judgement 批改 ---");
        Judgement judgement = new Judgement();
        judgement.judgePractice(practiceFile);
    }

    public static void main(String[] args) {
        System.out.println("===== 测试 writeCSVAddtionExercise =====");
        testWriteCSVAddtionExercise();

        System.out.println("\n===== 测试 readCSVAddtionExercise =====");
        testReadCSVAddtionExercise();

        System.out.println("\n===== 测试 writeCSVSubstractExercise =====");
        testWriteCSVSubstractExercise();

        System.out.println("\n===== 测试 readCSVSubstractExercise =====");
        testReadCSVSubstractExercise();

        System.out.println("\n===== 测试 writeCSVBinaryExercise =====");
        testWriteCSVBinaryExercise();

        System.out.println("\n===== 测试 readCSVBinaryExercise =====");
        testReadCSVBinaryExercise();

        System.out.println("\n===== 测试 完整判题流程 testJudgement =====");
        testJudgement();
    }
}
