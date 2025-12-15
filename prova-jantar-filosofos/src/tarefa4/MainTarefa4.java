package tarefa4;

import common.*;

public final class MainTarefa4 {
    public static void main(String[] args) throws Exception {
        int seconds = parseDurationSeconds(args, 120);
        Log.info("Tarefa 4 (monitor Mesa + fairness FIFO) - executando por ~" + seconds + "s");

        StopSignal stop = new StopSignal();
        Stats stats = new Stats(5, 5);
        Mesa mesa = new Mesa(5, 5);

        Philosopher[] ps = new Philosopher[5];
        for (int i = 0; i < 5; i++) {
            ps[i] = new Philosopher(i, stop, stats, mesa);
        }

        long start = System.nanoTime();
        long endAt = start + seconds * 1_000_000_000L;

        for (Philosopher p : ps) p.start();

        while (System.nanoTime() < endAt) {
            Thread.sleep(1000);
        }

        stop.stop();
        for (Philosopher p : ps) p.interrupt();
        for (Philosopher p : ps) p.join(2000);

        long runtime = System.nanoTime() - start;
        Log.info(stats.summary(runtime));
        Log.info("Fim da Tarefa 4.");
    }

    private static int parseDurationSeconds(String[] args, int def) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--duration")) return Integer.parseInt(args[i + 1]);
        }
        return def;
    }
}
