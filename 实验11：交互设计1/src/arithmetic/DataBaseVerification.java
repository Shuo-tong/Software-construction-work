package arithmetic;

/**
 * 数据基一致性验证：全量遍历所有合法操作数对，
 * 对比"数据基查询结果"与"朴素直接计算"，确保两者完全一致。
 * 同时输出空间占用统计。
 */
public class DataBaseVerification {

    public static void main(String[] args) {
        verifyAdditive();
        verifyMultiplicative();
        printSpaceStats();
        System.out.println("\n[OK] 数据基与朴素计算结果完全一致。");
    }

    private static void verifyAdditive() {
        AdditiveBase base = AdditiveBase.getInstance();
        int N = AdditiveBase.N;
        int checkedAdd = 0, checkedSub = 0;

        // 加法：所有 a, b ∈ [0, N] 且 a+b ≤ N
        for (int a = 0; a <= N; a++) {
            for (int b = 0; b <= N - a; b++) {
                int expected = a + b;
                int got = base.add(a, b);
                if (got != expected) {
                    throw new AssertionError("ADD 不一致: " + a + "+" + b + "=" + got + " (期望 " + expected + ")");
                }
                checkedAdd++;
            }
        }

        // 减法：所有 a ≥ b 且二者 ∈ [0, N]
        for (int a = 0; a <= N; a++) {
            for (int b = 0; b <= a; b++) {
                int expected = a - b;
                int got = base.sub(a, b);
                if (got != expected) {
                    throw new AssertionError("SUB 不一致: " + a + "-" + b + "=" + got + " (期望 " + expected + ")");
                }
                checkedSub++;
            }
        }

        System.out.printf("AdditiveBase  : 加法验证 %d 组，减法验证 %d 组，全部通过%n",
                checkedAdd, checkedSub);
    }

    private static void verifyMultiplicative() {
        MultiplicativeBase base = MultiplicativeBase.getInstance();
        int N = MultiplicativeBase.N;
        int checkedMul = 0, checkedDiv = 0;

        // 乘法：所有 a, b ∈ [1, N]
        for (int a = 1; a <= N; a++) {
            for (int b = 1; b <= N; b++) {
                int expected = a * b;
                int got = base.mul(a, b);
                if (got != expected) {
                    throw new AssertionError("MUL 不一致: " + a + "*" + b + "=" + got + " (期望 " + expected + ")");
                }
                checkedMul++;
            }
        }

        // 除法：所有 a 是 b 的倍数，且商 ∈ [1, N]
        for (int b = 1; b <= N; b++) {
            for (int q = 1; q <= N; q++) {
                int a = b * q;  // 构造能整除的被除数
                int got = base.div(a, b);
                if (got != q) {
                    throw new AssertionError("DIV 不一致: " + a + "/" + b + "=" + got + " (期望 " + q + ")");
                }
                checkedDiv++;
            }
        }

        System.out.printf("MultiplicativeBase: 乘法验证 %d 组，除法验证 %d 组，全部通过%n",
                checkedMul, checkedDiv);
    }

    private static void printSpaceStats() {
        int addSize = AdditiveBase.getInstance().size();
        int mulSize = MultiplicativeBase.getInstance().size();
        int naiveAdd = 101 * 101, naiveMul = 11 * 11;
        System.out.println("\n--- 空间统计 ---");
        System.out.printf("AdditiveBase     : 上三角 %d 项 (朴素全表 %d 项, 节省 %.1f%%)%n",
                addSize, naiveAdd, (1 - addSize * 1.0 / naiveAdd) * 100);
        System.out.printf("MultiplicativeBase: 上三角 %d 项 (朴素全表 %d 项, 节省 %.1f%%)%n",
                mulSize, naiveMul, (1 - mulSize * 1.0 / naiveMul) * 100);
        int sharedTotal = addSize + mulSize;
        int naiveTotal = naiveAdd * 2 + naiveMul * 2;  // 加减乘除四张全表
        System.out.printf("加减/乘除共享后总计 %d 项 vs 四张朴素全表 %d 项, 节省 %.1f%%%n",
                sharedTotal, naiveTotal, (1 - sharedTotal * 1.0 / naiveTotal) * 100);
    }
}
