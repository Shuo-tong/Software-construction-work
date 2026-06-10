package arithmetic;

import arithmetic.challenge.ChallengeRunner;

public class Main {
    public static void main(String[] args) {
        // 启动控制台交互：登录/注册 -> 选择难度 -> 挑战 -> 错题本/积分榜
        new ChallengeRunner().run();
    }
}
