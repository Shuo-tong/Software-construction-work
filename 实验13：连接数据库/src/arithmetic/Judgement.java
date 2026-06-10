package arithmetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Judgement 类：完成练习批改。
 * 流程：读入学生练习结果 -> 根据文件头加载对应习题答案 -> 比较统计 -> 屏幕显示 -> 写入CSV
 */
public class Judgement {

    private Integer[] results;       // 学生的练习答案
    private Integer[] answers;       // 习题的正确答案
    private String exerciseFileName; // 从练习文件头读取的习题文件名（不含扩展名）
    private String practiceFileName; // 当前练习文件名（不含扩展名）
    private int totalCount;          // 算式总数
    private int correctCount;        // 正确数
    private int wrongCount;          // 错误数
    private int score;               // 得分（百分制）

    // 从CSV文件读入练习结果，存入数组 results
    public Integer[] readCSVPractice(File aPractice) {
        // 记录当前练习文件名（去掉.csv后缀）
        String fname = aPractice.getName();
        practiceFileName = fname.endsWith(".csv") ? fname.substring(0, fname.length() - 4) : fname;

        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        // 整数正则：含负号
        Pattern numPattern = Pattern.compile("-?\\d+");
        // 文件头正则：匹配类似 "练习：addition_exercise_50_019" 的行（容忍空格）
        Pattern headerPattern = Pattern.compile("练习\\s*[:：]\\s*([\\w]+)");

        try (BufferedReader br = new BufferedReader(new FileReader(aPractice))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 优先解析文件头
                Matcher hm = headerPattern.matcher(line);
                if (hm.find()) {
                    exerciseFileName = hm.group(1);
                    continue;
                }
                // 跳过其它非数字行
                if (line.trim().startsWith("练习")) continue;
                // 提取所有整数
                Matcher matcher = numPattern.matcher(line);
                while (matcher.find()) {
                    list.add(Integer.parseInt(matcher.group()));
                }
            }
        } catch (IOException e) {
            System.err.println("读取练习文件失败: " + e.getMessage());
        }

        results = list.toArray(new Integer[0]);
        System.out.println("读入练习结果: " + results.length + " 个答案" +
                (exerciseFileName != null ? "（对应习题: " + exerciseFileName + "）" : ""));
        return results;
    }

    // 从文件头得到习题文件，加载对应的习题并取出标准答案存入 answers
    public Integer[] loadAnswers() {
        if (exerciseFileName == null) {
            System.err.println("无法获取习题文件头！请先调用 readCSVPractice 或确保练习文件包含文件头。");
            return null;
        }
        File exerciseFile = new File("Exercises/" + exerciseFileName + ".csv");
        if (!exerciseFile.exists()) {
            System.err.println("习题文件不存在: " + exerciseFile.getPath());
            return null;
        }

        Exercise anExercise = new Exercise();
        // 根据文件名前缀选择不同的读入方法
        if (exerciseFileName.startsWith("addition_exercise_")) {
            anExercise.readCSVAddtionExercise(exerciseFile);
        } else if (exerciseFileName.startsWith("substract_exercise_")) {
            anExercise.readCSVSubstractExercise(exerciseFile);
        } else if (exerciseFileName.startsWith("binary_exercise_")) {
            anExercise.readCSVBinaryExercise(exerciseFile);
        } else {
            // 默认使用混合运算读入
            anExercise.readCSVBinaryExercise(exerciseFile);
        }

        answers = new Integer[anExercise.size()];
        for (int i = 0; i < anExercise.size(); i++) {
            answers[i] = anExercise.get(i).getResult();
        }
        return answers;
    }

    // 比较 results 和 answers，统计正确/错误/得分
    public void judge() {
        if (results == null || answers == null) {
            System.err.println("批改失败：results 或 answers 为空，请先读入数据。");
            return;
        }
        totalCount = answers.length;
        correctCount = 0;
        wrongCount = 0;
        // 按对应位置比较，长度不一致时以习题数为准
        for (int i = 0; i < totalCount; i++) {
            if (i < results.length && results[i].equals(answers[i])) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }
        // 百分制得分
        score = totalCount == 0 ? 0 : (int) Math.round(correctCount * 100.0 / totalCount);
    }

    // 屏幕显示批改结果
    public void displayResult() {
        System.out.println("=========== 批改结果 ===========");
        System.out.println("答案：" + practiceFileName);
        System.out.println("算式总数：" + totalCount);
        System.out.println("正确：" + correctCount);
        System.out.println("错误：" + wrongCount);
        System.out.println("得分：" + score);
        System.out.println("================================");

        // 详细显示每题的批改对照
        System.out.println("--- 逐题对照 ---");
        for (int i = 0; i < totalCount; i++) {
            String stuAns = (i < results.length) ? results[i].toString() : "缺";
            String mark = (i < results.length && results[i].equals(answers[i])) ? "√" : "×";
            System.out.printf("第%2d题: 学生=%s, 标准=%d  %s%n",
                    (i + 1), stuAns, answers[i], mark);
        }
    }

    // 把批改结果输出到 CSV 文件
    public void writeCSVResult() {
        File dir = new File("Exercises");
        if (!dir.exists()) dir.mkdirs();
        // 输出文件名：judgement_<练习文件名>.csv
        String outName = "Exercises/judgement_" + practiceFileName + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(outName))) {
            pw.println("答案：" + practiceFileName);
            pw.println("算式总数：" + totalCount);
            pw.println("正确：" + correctCount);
            pw.println("错误：" + wrongCount);
            pw.println("得分：" + score);
            System.out.println("批改结果已写入文件: " + outName);
        } catch (IOException e) {
            System.err.println("写入批改文件失败: " + e.getMessage());
        }
    }

    // 一站式调用：读入练习 -> 加载答案 -> 批改 -> 显示 -> 写入文件
    public void judgePractice(File aPractice) {
        readCSVPractice(aPractice);
        loadAnswers();
        judge();
        displayResult();
        writeCSVResult();
    }

    public Integer[] getResults()      { return results; }
    public Integer[] getAnswers()      { return answers; }
    public int getTotalCount()         { return totalCount; }
    public int getCorrectCount()       { return correctCount; }
    public int getWrongCount()         { return wrongCount; }
    public int getScore()              { return score; }
    public String getExerciseFileName(){ return exerciseFileName; }
}
