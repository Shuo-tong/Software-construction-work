package TEST;

import arithmetic.challenge.ChallengeRunner;
import arithmetic.user.UserManager;

import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChallengeRunner CLI 菜单交互测试。
 * 通过重定向 System.in / System.out 模拟用户输入并捕获程序输出。
 *
 * 测试覆盖：
 *  - 有效输入：注册、登录、选择难度、正常作答、查看错题本、积分榜、登出、退出
 *  - 无效输入：非法菜单选项、错误密码、非数字作答、空输入、特殊字符等
 */
class ChallengeRunnerTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private ByteArrayOutputStream capturedOut;

    /** 每个测试前：清理 Users 目录确保隔离，重定向输出流 */
    @BeforeEach
    void setUp() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
        cleanupUsersDir();
    }

    /** 每个测试后：还原标准流 */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // =========================================================
    //  辅助方法
    // =========================================================

    /** 将多行输入模拟为 System.in */
    private void simulateInput(String... lines) {
        String joined = String.join("\n", lines) + "\n";
        System.setIn(new ByteArrayInputStream(joined.getBytes()));
    }

    /** 获取捕获的控制台输出文本 */
    private String getOutput() {
        return capturedOut.toString();
    }

    /** 清理 Users 目录 */
    private void cleanupUsersDir() {
        File dir = new File("Users");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
            dir.delete();
        }
    }

    /**
     * 运行 ChallengeRunner（需要在 simulateInput 之后调用）。
     * 由于 ChallengeRunner 内部的 Scanner 绑定的是构造时的 System.in，
     * 因此需要先设置 System.in 再构造 ChallengeRunner。
     */
    private void runCLI() {
        new ChallengeRunner().run();
    }

    // =========================================================
    //  有效输入测试
    // =========================================================

    /** 测试：用户选择退出（未登录状态输入 0 直接退出） */
    @Test
    void testExitWithoutLogin() {
        simulateInput("0");
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("再见"), "退出时应显示'再见'");
    }

    /** 测试：注册新用户成功 */
    @Test
    void testRegisterSuccess() {
        simulateInput(
                "2",          // 选择注册
                "testuser",   // 用户名
                "pass123",    // 密码
                "小测",       // 昵称
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("注册成功"), "注册成功后应有提示");
    }

    /** 测试：注册后登录成功 */
    @Test
    void testRegisterThenLogin() {
        simulateInput(
                "2",          // 注册
                "alice",      // 用户名
                "abc",        // 密码
                "小爱",       // 昵称
                "1",          // 登录
                "alice",      // 用户名
                "abc",        // 密码
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("注册成功"), "注册应成功");
        assertTrue(output.contains("欢迎回来"), "登录应成功并显示欢迎");
    }

    /** 测试：登录后查看积分榜 */
    @Test
    void testViewLeaderboard() {
        simulateInput(
                "2",          // 注册
                "user1",
                "pw1",
                "昵称1",
                "1",          // 登录
                "user1",
                "pw1",
                "3",          // 查看积分榜
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("积分榜"), "应显示积分榜");
    }

    /** 测试：登录后查看错题本（无错题） */
    @Test
    void testViewEmptyWrongBook() {
        simulateInput(
                "2",          // 注册
                "user2",
                "pw2",
                "",           // 昵称留空（使用默认）
                "1",          // 登录
                "user2",
                "pw2",
                "2",          // 查看错题本
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("还没有错题") || output.contains("错题本"),
                "无错题时应提示没有错题或显示错题本标题");
    }

    /** 测试：登录后查看个人信息 */
    @Test
    void testViewPersonalInfo() {
        simulateInput(
                "2",
                "infouser",
                "pw",
                "信息人",
                "1",
                "infouser",
                "pw",
                "4",          // 个人信息
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("infouser"), "个人信息中应包含用户ID");
    }

    /** 测试：登录后登出 */
    @Test
    void testLogout() {
        simulateInput(
                "2",
                "loguser",
                "pw",
                "登出人",
                "1",
                "loguser",
                "pw",
                "9",          // 登出
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("已登出"), "登出时应有提示");
    }

    /** 测试：完整挑战流程（L1 难度，5 题，全部输入有效数字） */
    @Test
    void testChallengeWithValidAnswers() {
        // L1 难度有 5 道题，每道我们都回答 "0"（大概率答错，但输入合法）
        simulateInput(
                "2",
                "player1",
                "pw",
                "玩家1",
                "1",
                "player1",
                "pw",
                "1",          // 开始挑战
                "1",          // 选择 L1 难度
                "0",          // 第1题答案
                "0",          // 第2题答案
                "0",          // 第3题答案
                "0",          // 第4题答案
                "0",          // 第5题答案
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("挑战结果"), "挑战结束后应显示结果");
        assertTrue(output.contains("题数"), "应显示题数统计");
        assertTrue(output.contains("本次获得积分"), "应显示积分信息");
    }

    /** 测试：L2 难度挑战，10 道题全部输入有效数字 */
    @Test
    void testChallengeL2() {
        // L2 有 10 道题
        simulateInput(
                "2",
                "p2",
                "pw",
                "玩家2",
                "1",
                "p2",
                "pw",
                "1",          // 开始挑战
                "2",          // L2 难度
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",  // 10道题的答案
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("挑战结果"), "L2 挑战应正常完成");
    }

    // =========================================================
    //  无效输入测试
    // =========================================================

    /** 测试：未登录菜单输入无效选项 */
    @Test
    void testInvalidAuthMenuChoice() {
        simulateInput(
                "5",          // 无效选项
                "abc",        // 无效选项
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("无效输入"), "无效菜单选项应提示无效输入");
    }

    /** 测试：登录时用户不存在 */
    @Test
    void testLoginNonexistentUser() {
        simulateInput(
                "1",          // 登录
                "ghost",      // 不存在的用户
                "anypass",
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("用户不存在"), "登录不存在的用户应提示失败");
    }

    /** 测试：登录时密码错误 */
    @Test
    void testLoginWrongPassword() {
        simulateInput(
                "2",          // 先注册
                "secuser",
                "rightpw",
                "安全人",
                "1",          // 登录
                "secuser",
                "wrongpw",    // 错误密码
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("密码错误"), "密码错误时应有提示");
    }

    /** 测试：重复注册同一用户名 */
    @Test
    void testDuplicateRegister() {
        simulateInput(
                "2",          // 注册第一次
                "dupuser",
                "pw1",
                "昵称",
                "2",          // 注册第二次（同名）
                "dupuser",
                "pw2",
                "昵称2",
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("已存在"), "重复注册应提示用户名已存在");
    }

    /** 测试：已登录菜单输入无效选项 */
    @Test
    void testInvalidMainMenuChoice() {
        simulateInput(
                "2",
                "menuuser",
                "pw",
                "菜单人",
                "1",
                "menuuser",
                "pw",
                "7",          // 无效的主菜单选项
                "abc",        // 无效的主菜单选项
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("无效输入"), "无效主菜单选项应提示无效输入");
    }

    /** 测试：挑战时选择无效难度 */
    @Test
    void testInvalidDifficultySelection() {
        simulateInput(
                "2",
                "diffuser",
                "pw",
                "难度人",
                "1",
                "diffuser",
                "pw",
                "1",          // 开始挑战
                "5",          // 无效难度
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("无效难度") || output.contains("已取消"),
                "无效难度选项应提示无效并取消挑战");
    }

    /** 测试：挑战作答时输入非数字字符（字母） */
    @Test
    void testNonNumericAnswerLetters() {
        simulateInput(
                "2",
                "badans1",
                "pw",
                "错答1",
                "1",
                "badans1",
                "pw",
                "1",          // 开始挑战
                "1",          // L1 难度，5 题
                "abc",        // 非法输入：字母
                "10",         // 正常数字
                "xyz",        // 非法输入：字母
                "5",          // 正常数字
                "!@#",        // 非法输入：特殊字符
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        // 非数字输入应被当作"未作答"处理
        assertTrue(output.contains("已记为未作答"), "非数字输入应提示已记为未作答");
        assertTrue(output.contains("挑战结果"), "即使部分题未作答，挑战也应正常结束");
    }

    /** 测试：挑战作答时输入空字符串 */
    @Test
    void testEmptyAnswer() {
        simulateInput(
                "2",
                "emptyans",
                "pw",
                "空答人",
                "1",
                "emptyans",
                "pw",
                "1",          // 开始挑战
                "1",          // L1 难度，5 题
                "",           // 空输入
                "",           // 空输入
                "10",         // 正常
                "",           // 空输入
                "5",          // 正常
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("已记为未作答"), "空输入应被视为未作答");
        assertTrue(output.contains("挑战结果"), "挑战应正常完成");
    }

    /** 测试：挑战作答时输入特殊字符 */
    @Test
    void testSpecialCharacterAnswer() {
        simulateInput(
                "2",
                "specans",
                "pw",
                "特字人",
                "1",
                "specans",
                "pw",
                "1",          // 开始挑战
                "1",          // L1
                "!@#$%",      // 特殊字符
                "3.14",       // 小数（非整数）
                "1 2",        // 包含空格的多数字
                "-",          // 单独的负号
                "++",         // 运算符
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("已记为未作答"), "特殊字符应被视为未作答");
        assertTrue(output.contains("挑战结果"), "挑战应正常完成");
    }

    /** 测试：挑战作答时输入负数（合法整数，但口算不可能为负） */
    @Test
    void testNegativeNumberAnswer() {
        simulateInput(
                "2",
                "negans",
                "pw",
                "负数人",
                "1",
                "negans",
                "pw",
                "1",          // 开始挑战
                "1",          // L1
                "-1",         // 负数（合法整数，但答案肯定错）
                "-100",       // 负数
                "0",          // 0
                "999",        // 超大数
                "-999",       // 超大负数
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        // 负数是合法整数，不会触发"未作答"，但会答错
        assertTrue(output.contains("挑战结果"), "负数输入应被当作有效答案参与判分");
        assertTrue(output.contains("错误"), "负数答案应被判为错误");
    }

    /** 测试：挑战作答时输入超大数字（不会溢出 int 范围） */
    @Test
    void testLargeNumberAnswer() {
        simulateInput(
                "2",
                "bignum",
                "pw",
                "大数人",
                "1",
                "bignum",
                "pw",
                "1",          // 开始挑战
                "1",          // L1
                "99999",      // 超大数
                "100000",
                "12345",
                "0",
                "1",
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("挑战结果"), "大数输入应正常完成挑战");
    }

    /** 测试：连续多次无效菜单输入后正常退出 */
    @Test
    void testMultipleInvalidThenExit() {
        simulateInput(
                "99",
                "hello",
                "-1",
                "",
                "!",
                "0"           // 最终退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("再见"), "经过多次无效输入后应仍能正常退出");
        // 统计"无效输入"出现的次数
        int count = 0;
        int idx = 0;
        while ((idx = output.indexOf("无效输入", idx)) != -1) {
            count++;
            idx++;
        }
        assertTrue(count >= 3, "多次无效输入应多次提示（至少3次），实际: " + count);
    }

    /** 测试：挑战难度选择时输入字母 */
    @Test
    void testDifficultyWithLetters() {
        simulateInput(
                "2",
                "difflet",
                "pw",
                "字母难度",
                "1",
                "difflet",
                "pw",
                "1",          // 开始挑战
                "abc",        // 无效难度输入
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        assertTrue(output.contains("无效难度") || output.contains("已取消"),
                "字母作为难度选择应被拒绝");
    }

    /** 测试：注册时用户名为空 */
    @Test
    void testRegisterEmptyUserId() {
        simulateInput(
                "2",          // 注册
                "",           // 空用户名
                "pw",
                "空名人",
                "0"           // 退出
        );
        runCLI();
        String output = getOutput();
        // UserManager.register() 对空 userId 返回 false
        assertFalse(output.contains("注册成功"), "空用户名不应注册成功");
    }
}
