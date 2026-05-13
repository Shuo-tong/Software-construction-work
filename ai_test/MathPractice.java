public class MathPractice {
    // 生成加法题目：返回 题目字符串，如 "5+3"
    public String generateAddition(int max) {
        int a = (int) (Math.random() * max);
        int b = (int) (Math.random() * max);
        return a + "+" + b;
    }

    // 计算正确答案
    public int calculateAnswer(String question) {
        if (question.contains("+")) {
            String[] nums = question.split("\\+");
            return Integer.parseInt(nums[0]) + Integer.parseInt(nums[1]);
        }
        return 0;
    }

    // 校验答案是否正确
    public boolean checkAnswer(String question, int userAnswer) {
        return calculateAnswer(question) == userAnswer;
    }
}