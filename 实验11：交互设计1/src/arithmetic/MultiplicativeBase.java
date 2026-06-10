package arithmetic;

/**
 * 乘除法共享数据基（MultiplicativeBase）。
 *
 * 设计要点：
 *  1) 操作数范围 [1, N]（N=10），仅存储上三角 1 <= i <= j <= N，共 N*(N+1)/2 = 55 项。
 *  2) 乘法直接读表；除法用"反向查表"思路：在第 b 行查值为 a 的列号即商 q，
 *     即同一份数据同时服务乘法与除法两种运算。
 */
public final class MultiplicativeBase {
    public static final int N = 10;
    private static final MultiplicativeBase INSTANCE = new MultiplicativeBase();

    private final int[] table;

    private MultiplicativeBase() {
        // 上三角项数：N*(N+1)/2 = 55
        table = new int[N * (N + 1) / 2];
        for (int i = 1; i <= N; i++) {
            for (int j = i; j <= N; j++) {
                table[idx(i, j)] = i * j;
            }
        }
    }

    /** 获取单例。 */
    public static MultiplicativeBase getInstance() { return INSTANCE; }

    /**
     * 上三角索引：要求 1 <= i <= j <= N。
     * 第 i 行起始偏移 = (i-1)*(N+1) - (i-1)*i/2，列偏移 = j - i。
     */
    private int idx(int i, int j) {
        return (i - 1) * (N + 1) - (i - 1) * i / 2 + (j - i);
    }

    /** 查询乘法结果：a * b。利用对称性把参数规约到上三角。 */
    public int mul(int a, int b) {
        int i = Math.min(a, b);
        int j = Math.max(a, b);
        return table[idx(i, j)];
    }

    /**
     * 查询除法结果：a / b（要求 b != 0 且 a % b == 0）。
     * 反向查表：在表中寻找 q 使 b * q == a，体现"乘除共享同一张表"。
     * 若反查失败则降级直接整除，保证健壮性。
     */
    public int div(int a, int b) {
        if (b == 0) return 0;
        for (int q = 1; q <= N; q++) {
            int i = Math.min(b, q);
            int j = Math.max(b, q);
            if (i >= 1 && j <= N && table[idx(i, j)] == a) {
                return q;
            }
        }
        return a / b;  // 兜底
    }

    /** 乘法合法性：操作数均在 [1, N] 范围内。 */
    public boolean canMul(int a, int b) {
        return a >= 1 && a <= N && b >= 1 && b <= N;
    }

    /** 除法合法性：除数 b ∈ [1, N]，被除数能整除，且商 ∈ [1, N]。 */
    public boolean canDiv(int a, int b) {
        if (b < 1 || b > N) return false;
        if (a < 1 || a > N * N) return false;
        if (a % b != 0) return false;
        int q = a / b;
        return q >= 1 && q <= N;
    }

    /** 表大小（供测试 / 诊断）。 */
    public int size() { return table.length; }
}
