package arithmetic.user;

import arithmetic.Addition;
import arithmetic.Binaryoperation;
import arithmetic.Difficulty;
import arithmetic.Division;
import arithmetic.Multiplication;
import arithmetic.Substraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 错题本：每个 User 持有一份。
 * 利用 Binaryoperation.equals 自动去重：同一题再次做错时仅累加 retryCount。
 */
public class WrongBook {

    private final List<WrongEntry> entries = new ArrayList<>();

    /** 添加一道错题；若题目已存在则只 +1 重做次数并刷新 userAnswer */
    public void add(Binaryoperation problem, int userAnswer, Difficulty difficulty) {
        for (WrongEntry e : entries) {
            if (e.getProblem().equals(problem)) {
                e.setUserAnswer(userAnswer);
                e.incRetry();
                e.setMastered(false);  // 再次做错则取消掌握标记
                return;
            }
        }
        entries.add(new WrongEntry(problem, userAnswer, difficulty));
    }

    /** 按下标标记某题已掌握 */
    public void markMastered(int index) {
        if (index >= 0 && index < entries.size()) entries.get(index).markMastered();
    }

    /** 返回未掌握的错题数 */
    public int activeCount() {
        int n = 0;
        for (WrongEntry e : entries) if (!e.isMastered()) n++;
        return n;
    }

    public int size() { return entries.size(); }
    public List<WrongEntry> getAll() { return entries; }
    public List<WrongEntry> getActive() {
        List<WrongEntry> r = new ArrayList<>();
        for (WrongEntry e : entries) if (!e.isMastered()) r.add(e);
        return r;
    }

    /** 屏幕展示 */
    public void display() {
        if (entries.isEmpty()) {
            System.out.println("[错题本] 还没有错题，继续保持！");
            return;
        }
        System.out.println("=========== 错题本 (共 " + entries.size()
                + " 题，未掌握 " + activeCount() + ") ===========");
        for (int i = 0; i < entries.size(); i++) {
            System.out.printf("%2d. %s%n", (i + 1), entries.get(i));
        }
        System.out.println("============================================");
    }

    // ========================
    //  CSV 持久化
    // ========================
    // 行格式：op|userAns|difficulty|timestamp|retryCount|mastered
    // 例如：  32+5|37|L2|1716700000000|2|false
    private static final Pattern PROBLEM_PATTERN =
            Pattern.compile("(\\d+)\\s*([+\\-*/])\\s*(\\d+)");

    public void writeCSV(File file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (WrongEntry e : entries) {
                Binaryoperation p = e.getProblem();
                pw.println(p.toString() + "|" + e.getUserAnswer() + "|"
                        + e.getDifficulty().name() + "|" + e.getTimestamp()
                        + "|" + e.getRetryCount() + "|" + e.isMastered());
            }
        } catch (IOException ex) {
            System.err.println("写入错题本失败: " + ex.getMessage());
        }
    }

    public void readCSV(File file) {
        entries.clear();
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;
                Binaryoperation problem = parseProblem(parts[0].trim());
                if (problem == null) continue;
                int userAns = Integer.parseInt(parts[1].trim());
                Difficulty d = Difficulty.valueOf(parts[2].trim());
                long ts = Long.parseLong(parts[3].trim());
                int retry = Integer.parseInt(parts[4].trim());
                boolean mastered = Boolean.parseBoolean(parts[5].trim());
                entries.add(new WrongEntry(problem, userAns, d, ts, retry, mastered));
            }
        } catch (IOException ex) {
            System.err.println("读取错题本失败: " + ex.getMessage());
        }
    }

    /** 把 "32+5" 这种字符串还原成对应的 Binaryoperation 子类对象 */
    private Binaryoperation parseProblem(String token) {
        Matcher m = PROBLEM_PATTERN.matcher(token);
        if (!m.find()) return null;
        int left  = Integer.parseInt(m.group(1));
        char oper = m.group(2).charAt(0);
        int right = Integer.parseInt(m.group(3));
        Binaryoperation op;
        switch (oper) {
            case '+': op = new Addition();       break;
            case '-': op = new Substraction();   break;
            case '*': op = new Multiplication(); break;
            case '/': op = new Division();       break;
            default:  return null;
        }
        op.setLeftOperand(left);
        op.setRightOperand(right);
        // 调用 calculate() 取得正确答案后回写 value，以便 getResult() 返回正确值
        op.setValue(op.calculate());
        return op;
    }
}
