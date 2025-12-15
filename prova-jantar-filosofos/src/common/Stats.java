package common;

import java.util.Arrays;

public final class Stats {
    private final int n;
    private final long[] meals;
    private final long[] attempts;
    private final long[] totalWaitNanos;
    private final long[] totalEatNanos;
    private final long[] forkHoldNanos; // per fork id

    public Stats(int philosophers, int forks) {
        this.n = philosophers;
        this.meals = new long[philosophers];
        this.attempts = new long[philosophers];
        this.totalWaitNanos = new long[philosophers];
        this.totalEatNanos = new long[philosophers];
        this.forkHoldNanos = new long[forks];
    }

    public synchronized void recordAttempt(int id) {
        attempts[id]++;
    }

    public synchronized void recordEatStart(int id, long waitNanos) {
        totalWaitNanos[id] += Math.max(0L, waitNanos);
    }

    public synchronized void recordEatEnd(int id, int leftForkId, int rightForkId, long eatNanos) {
        meals[id]++;
        totalEatNanos[id] += Math.max(0L, eatNanos);
        // each fork is held for the duration of eating in our implementations
        forkHoldNanos[leftForkId] += Math.max(0L, eatNanos);
        forkHoldNanos[rightForkId] += Math.max(0L, eatNanos);
    }

    public synchronized long mealsOf(int id) { return meals[id]; }

    public synchronized String summary(long runtimeNanos) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Estatísticas (runtime ~ ").append(runtimeNanos / 1_000_000_000.0).append(" s) ===\n");
        for (int i = 0; i < n; i++) {
            double avgWaitMs = meals[i] == 0 ? Double.NaN : (totalWaitNanos[i] / 1_000_000.0) / meals[i];
            double avgEatMs  = meals[i] == 0 ? Double.NaN : (totalEatNanos[i] / 1_000_000.0) / meals[i];
            sb.append("Filósofo ").append(i)
              .append(": refeições=").append(meals[i])
              .append(", tentativas=").append(attempts[i])
              .append(", espera média=").append(format(avgWaitMs)).append(" ms")
              .append(", comer médio=").append(format(avgEatMs)).append(" ms")
              .append("\n");
        }
        sb.append("\nUtilização dos garfos (tempo segurado / runtime):\n");
        for (int f = 0; f < forkHoldNanos.length; f++) {
            double util = runtimeNanos <= 0 ? Double.NaN : (double) forkHoldNanos[f] / (double) runtimeNanos;
            sb.append("G").append(f).append(": ").append(format(util * 100)).append("%\n");
        }
        sb.append("\nFairness: coeficiente de variação (CV) das refeições: ").append(format(coefVar(meals))).append("\n");
        return sb.toString();
    }

    private static String format(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "n/a";
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private static double coefVar(long[] x) {
        int n = x.length;
        if (n == 0) return Double.NaN;
        double mean = 0.0;
        for (long v : x) mean += v;
        mean /= n;
        if (mean == 0.0) return Double.NaN;

        double var = 0.0;
        for (long v : x) {
            double d = v - mean;
            var += d * d;
        }
        var /= n;
        double std = Math.sqrt(var);
        return std / mean;
    }

    @Override
    public synchronized String toString() {
        return "Stats{" +
                "meals=" + Arrays.toString(meals) +
                ", attempts=" + Arrays.toString(attempts) +
                '}';
    }
}
