package arithmetic.challenge;

import arithmetic.Binaryoperation;
import arithmetic.Difficulty;
import arithmetic.Exercise;
import arithmetic.OperationFactory;
import arithmetic.user.User;
import arithmetic.user.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 单次挑战会话。
 * 流程：构造 -> generate() -> 多次 submitAnswer(idx, ans) -> finish()
 */
public class Challenge {
    private final User user;
    private final Difficulty difficulty;
    private final OperationFactory factory;
    private final Exercise problems;          // 复用 Exercise 容器
    private final Integer[] userAnswers;      // 用户作答（null = 未答）
    private long startTime;
    private long endTime;
    private boolean finished;

    public Challenge(User user, Difficulty difficulty) {
        if (user == null) throw new IllegalArgumentException("挑战需要登录用户");
        if (difficulty == null) throw new IllegalArgumentException("挑战需要指定难度");
        this.user = user;
        this.difficulty = difficulty;
        this.factory = new OperationFactory(difficulty);
        this.problems = new Exercise();
        this.userAnswers = new Integer[difficulty.problemCount];
    }

    /** 出题（在 OperationFactory 内已做去重） */
    public void generate() {
        problems.clear();
        Exercise generated = factory.newExercise();
        problems.addAll(generated);
        startTime = System.currentTimeMillis();
        finished = false;
    }

    public Exercise getProblems()       { return problems; }
    public Difficulty getDifficulty()   { return difficulty; }
    public User getUser()               { return user; }
    public int getProblemCount()        { return problems.size(); }

    /** 提交某题答案；index 从 0 起 */
    public void submitAnswer(int index, int answer) {
        if (finished) throw new IllegalStateException("挑战已结束");
        if (index < 0 || index >= userAnswers.length) return;
        userAnswers[index] = answer;
    }

    /** 已作答题数 */
    public int answeredCount() {
        int n = 0;
        for (Integer a : userAnswers) if (a != null) n++;
        return n;
    }

    /**
     * 结算：判分、积分入账、错题入库、保存到 CSV。
     */
    public ChallengeResult finish() {
        if (finished) throw new IllegalStateException("不能重复结算");
        endTime = System.currentTimeMillis();
        finished = true;

        int total = problems.size();
        int correct = 0;
        List<Binaryoperation> wrongList = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            Binaryoperation p = problems.get(i);
            Integer ua = userAnswers[i];
            boolean ok = (ua != null) && (ua == p.getResult());
            if (ok) {
                correct++;
            } else {
                int answerForBook = (ua != null) ? ua : Integer.MIN_VALUE;
                user.getWrongBook().add(p, answerForBook, difficulty);
                wrongList.add(p);
            }
        }
        int wrong = total - correct;
        int score = difficulty.computeScore(correct, total);

        // 更新用户成就并入账
        user.recordChallenge(correct);
        user.addScore(score);

        // 持久化
        UserManager.getInstance().saveCurrentUser();

        return new ChallengeResult(user, difficulty, total, correct, wrong,
                score, endTime - startTime, wrongList);
    }
}
