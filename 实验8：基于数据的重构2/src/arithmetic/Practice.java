package arithmetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Practice {

    /**
     * 从 CSV 文件中读入练习结果，返回整数列表。
     * 使用正则表达式提取所有整数（含负数 -1），滤掉其它符号。
     *
     */
    public ArrayList<Integer> readCSVPractice(File aFile) {
        ArrayList<Integer> results = new ArrayList<>();
        // 正则：匹配整数（含负号，如 -1），滤掉其它所有符号
        Pattern pattern = Pattern.compile("-?\\d+");
        try (BufferedReader br = new BufferedReader(new FileReader(aFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 跳过文件头（以"练习"开头的行）
                if (line.trim().startsWith("练习")) continue;
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    results.add(Integer.parseInt(matcher.group()));
                }
            }
            System.out.println("从文件读入 " + results.size() + " 个练习答案: " + aFile.getName());
        } catch (IOException e) {
            System.err.println("读取练习文件失败: " + e.getMessage());
        }
        return results;
    }
}
