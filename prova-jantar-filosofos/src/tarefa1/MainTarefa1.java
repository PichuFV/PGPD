package tarefa1;

import common.*;

public final class MainTarefa1 {
    public static void main(String[] args) throws Exception {
        int seconds = parseDurationSeconds(args, 30);
        Log.info("Tarefa 1 (deadlock) - executando por ~" + seconds + "s");
        StopSignal stop = new StopSignal();

        Fork[] forks = new Fork[5];
        for (int i = 0; i < 5; i++) forks[i] = new Fork(i);

        Philosopher[] ps = new Philosopher[5];
        for (int i = 0; i < 5; i++) {
            Fork left = forks[i];
            Fork right = forks[(i + 1) % 5];
            ps[i] = new Philosopher(i, left, right, stop);
        }

        for (Philosopher p : ps) p.start();

        long start = System.nanoTime();
        long endAt = start + seconds * 1_000_000_000L;

        while (System.nanoTime() < endAt) {
            if (DeadlockDetector.logIfDeadlocked()) {
                Log.info("Encerrando execução (deadlock evidenciado).");
                break;
            }
            Thread.sleep(1000);
        }

        stop.stop();
        for (Philosopher p : ps) p.interrupt();
        for (Philosopher p : ps) p.join(2000);

        Log.info("Fim da Tarefa 1.");
    }

    private static int parseDurationSeconds(String[] args, int def) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--duration")) return Integer.parseInt(args[i + 1]);
        }
        return def;
    }
}
