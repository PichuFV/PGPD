package tarefa3;

import common.*;

import java.util.concurrent.Semaphore;

public final class MainTarefa3 {
    public static void main(String[] args) throws Exception {
        int seconds = parseDurationSeconds(args, 120);
        Log.info("Tarefa 3 (semáforo global) - executando por ~" + seconds + "s");

        StopSignal stop = new StopSignal();
        Fork[] forks = new Fork[5];
        for (int i = 0; i < 5; i++) forks[i] = new Fork(i);

        Stats stats = new Stats(5, 5);
        // fairness=true ajuda a reduzir variação, mas não garante por si só sem o monitor da Tarefa 4.
        Semaphore limit = new Semaphore(4, true);

        Philosopher[] ps = new Philosopher[5];
        for (int i = 0; i < 5; i++) {
            Fork left = forks[i];
            Fork right = forks[(i + 1) % 5];
            ps[i] = new Philosopher(i, left, right, stop, stats, limit);
        }

        long start = System.nanoTime();
        long endAt = start + seconds * 1_000_000_000L;

        for (Philosopher p : ps) p.start();

        while (System.nanoTime() < endAt) {
            if (DeadlockDetector.logIfDeadlocked()) {
                Log.info("Isso não deveria ocorrer na Tarefa 3. Encerrando.");
                break;
            }
            Thread.sleep(1000);
        }

        stop.stop();
        for (Philosopher p : ps) p.interrupt();
        for (Philosopher p : ps) p.join(2000);

        long runtime = System.nanoTime() - start;
        Log.info(stats.summary(runtime));
        Log.info("Fim da Tarefa 3.");
    }

    private static int parseDurationSeconds(String[] args, int def) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--duration")) return Integer.parseInt(args[i + 1]);
        }
        return def;
    }
}
