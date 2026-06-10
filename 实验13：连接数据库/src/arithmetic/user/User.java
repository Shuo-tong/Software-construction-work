package arithmetic.user;

/**
 * 用户实体：账号、口令、昵称、积分、错题本及简单成就统计。
 * 不做密码哈希（小学生场景，明文足够），后续可平滑升级。
 */
public class User {
    private final String userId;
    private String password;
    private String nickname;
    private int score;
    private int totalChallenges;
    private int totalCorrect;
    private final WrongBook wrongBook;

    public User(String userId, String password, String nickname) {
        this.userId   = userId;
        this.password = password;
        this.nickname = (nickname == null || nickname.isEmpty()) ? userId : nickname;
        this.score    = 0;
        this.totalChallenges = 0;
        this.totalCorrect    = 0;
        this.wrongBook = new WrongBook();
    }

    /** 完整构造（供 CSV 反序列化使用） */
    public User(String userId, String password, String nickname,
                int score, int totalChallenges, int totalCorrect) {
        this(userId, password, nickname);
        this.score = score;
        this.totalChallenges = totalChallenges;
        this.totalCorrect = totalCorrect;
    }

    public String getUserId()           { return userId; }
    public String getPassword()         { return password; }
    public String getNickname()         { return nickname; }
    public int getScore()               { return score; }
    public int getTotalChallenges()     { return totalChallenges; }
    public int getTotalCorrect()        { return totalCorrect; }
    public WrongBook getWrongBook()     { return wrongBook; }

    public void setPassword(String pw)  { this.password = pw; }
    public void setNickname(String n)   { this.nickname = n; }

    /** 校验密码 */
    public boolean verifyPassword(String pw) {
        return password != null && password.equals(pw);
    }

    /** 累加积分（不允许扣分） */
    public void addScore(int delta) {
        if (delta > 0) this.score += delta;
    }

    /** 一次挑战结束后调用，更新成就统计 */
    public void recordChallenge(int correctInThisRound) {
        this.totalChallenges++;
        this.totalCorrect += Math.max(0, correctInThisRound);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s 积分=%d 挑战=%d 正确=%d 错题=%d",
                userId, nickname, score, totalChallenges, totalCorrect, wrongBook.size());
    }
}
