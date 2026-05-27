package arithmetic;

/**
 * 减法专用数据基（SubtractiveBase）。
 * 设计要点：
 *  1) 存储下三角 0 <= j <= i <= N，存储差值 i - j（非负）。
 *  2) 一维数组紧凑存储，索引公式见 idx()。
 *  3) 减法直接查表（a>=b 时），否则取负。
 */
public final class SubtractiveBase {
    public static final int N = 100;
    private static final SubtractiveBase INSTANCE = new SubtractiveBase();

    private final int[] table;

    private SubtractiveBase() {
        // 下三角项数：C(N+2, 2) = (N+1)*(N+2)/2 = 5151
        table = new int[(N + 1) * (N + 2) / 2];
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= i; j++) {
                table[idx(i, j)] = i - j;
            }
        }
    }

    public static SubtractiveBase getInstance() { return INSTANCE; }

    /**
     * 下三角索引：要求 0 <= j <= i <= N。
     * 第 i 行起始偏移 = i*(i+1)/2，列偏移 = j。
     */
    private int idx(int i, int j) {
        return i * (i + 1) / 2 + j;
    }

    /** 查询减法结果：a - b。若 a < b 则返回负值。 */
    public int sub(int a, int b) {
        if (a >= b) {
            return table[idx(a, b)];
        } else {
            return -table[idx(b, a)];
        }
    }

    /** 减法合法性：a >= b，且二者均在 [0, N] 范围内。 */
    public boolean canSub(int a, int b) {
        return a >= 0 && b >= 0 && a <= N && b <= N && a >= b;
    }

    /** 表大小（供测试 / 诊断）。 */
    public int size() { return table.length; }
}