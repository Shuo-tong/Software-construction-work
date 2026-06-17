package arithmetic;

/**
 * 加减法共享数据基（AdditiveBase）。
 * 设计要点：
 *  1) 仅存储上三角 0 <= i <= j <= N，
 *  2) 一维数组紧凑存储，索引公式见 idx()。
 *  3) 加法直接读表；减法用恒等式 a - b = (a + b) - 2b 复用同一张表，
 *     即同一份数据同时服务加法与减法两种运算。
 */
public final class AdditiveBase {
    public static final int N = 100;
    private static final AdditiveBase INSTANCE = new AdditiveBase();

    private final int[] table;

    private AdditiveBase() {
        // 上三角项数：C(N+2, 2) = (N+1)*(N+2)/2 = 5151
        table = new int[(N + 1) * (N + 2) / 2];
        for (int i = 0; i <= N; i++) {
            for (int j = i; j <= N; j++) {
                table[idx(i, j)] = i + j;
            }
        }
    }

    public static AdditiveBase getInstance() { return INSTANCE; }

    /**
     * 上三角索引：要求 0 <= i <= j <= N。
     * 第 i 行起始偏移 = i*(N+1) - i*(i-1)/2，列偏移 = j - i。
     */
    private int idx(int i, int j) {
        return i * (N + 1) - i * (i - 1) / 2 + (j - i);
    }

    /** 查询加法结果：a + b。利用对称性把参数规约到上三角。 */
    public int add(int a, int b) {
        int i = Math.min(a, b);
        int j = Math.max(a, b);
        return table[idx(i, j)];
    }

    /**
     * 查询减法结果：a - b。
     * 复用加法表：sum(a, b) = a + b，因此 a - b = sum(a, b) - 2b。
     * 兼容 a < b 的情形（返回负值），保持与原始 left - right 行为一致。
     */
    public int sub(int a, int b) {
        if (a >= b) {
            return table[idx(b, a)] - 2 * b;
        } else {
            return -(table[idx(a, b)] - 2 * a);
        }
    }

    /** 加法合法性：操作数与和均在 [0, N] 范围内。 */
    public boolean canAdd(int a, int b) {
        return a >= 0 && b >= 0 && a <= N && b <= N && (a + b) <= N;
    }

    /** 减法合法性：a >= b，且二者均在 [0, N] 范围内。 */
    public boolean canSub(int a, int b) {
        return a >= 0 && b >= 0 && a <= N && b <= N && a >= b;
    }

    /** 表大小（供测试 / 诊断）。 */
    public int size() { return table.length; }
}
