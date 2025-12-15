package tarefa2;

import common.*;

public final class MainTarefa2 {
    public static void main(String[] args) throws Exception {
        int seconds = parseDurationSeconds(args, 120);
        Log.info("Tarefa 2 (ordem diferente p/ prevenir deadlock) - executando por ~" + seconds + "s");

        StopSignal stop = new StopSignal();
        Fork[] forks = new Fork[5];
        for (int i = 0; i < 5; i++) forks[i] = new Fork(i);

        Stats stats = new Stats(5, 5);

        Philosopher[] ps = new Philosopher[5];
        for (int i = 0; i < 5; i++) {
            Fork left = forks[i];
            Fork right = forks[(i + 1) % 5];
            boolean reverse = (i == 4); // conforme enunciado (exemplo)
            ps[i] = new Philosopher(i, left, right, stop, stats, reverse);
        }

        long start = System.nanoTime();
        long endAt = start + seconds * 1_000_000_000L;

        for (Philosopher p : ps) p.start();

        while (System.nanoTime() < endAt) {
            if (DeadlockDetector.logIfDeadlocked()) {
                Log.info("Isso NÃO deveria acontecer na Tarefa 2. Encerrando.");
                break;
            }
            Thread.sleep(1000);
        }

        stop.stop();
        for (Philosopher p : ps) p.interrupt();
        for (Philosopher p : ps) p.join(2000);

        long runtime = System.nanoTime() - start;
        Log.info(stats.summary(runtime));
        Log.info("Fim da Tarefa 2.");
    }

    private static int parseDurationSeconds(String[] args, int def) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--duration")) return Integer.parseInt(args[i + 1]);
        }
        return def;
    }
}
