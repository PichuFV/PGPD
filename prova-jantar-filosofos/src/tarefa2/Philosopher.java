package tarefa2;

import common.*;

public final class Philosopher extends Thread {
    private final int id;
    private final Fork left;
    private final Fork right;
    private final StopSignal stop;
    private final Stats stats;
    private final boolean reverseOrder; // philosopher 4: right then left

    public Philosopher(int id, Fork left, Fork right, StopSignal stop, Stats stats, boolean reverseOrder) {
        super("F" + id);
        this.id = id;
        this.left = left;
        this.right = right;
        this.stop = stop;
        this.stats = stats;
        this.reverseOrder = reverseOrder;
    }

    @Override
    public void run() {
        try {
            while (stop.isRunning()) {
                think();
                tryEat();
            }
        } catch (InterruptedException e) {
            // shutdown
        }
    }

    private void think() throws InterruptedException {
        Log.info("Filósofo " + id + " começou a pensar.");
        RandomSleep.between(1000, 3000);
    }

    private void tryEat() throws InterruptedException {
        stats.recordAttempt(id);
        long attemptAt = System.nanoTime();

        Fork first = reverseOrder ? right : left;
        Fork second = reverseOrder ? left : right;

        Log.info("Filósofo " + id + " tenta pegar " + first + " (1º).");
        synchronized (first) {
            Log.info("Filósofo " + id + " pegou " + first + " e tenta pegar " + second + " (2º).");
            synchronized (second) {
                long startEat = System.nanoTime();
                stats.recordEatStart(id, startEat - attemptAt);

                Log.info("Filósofo " + id + " pegou ambos os garfos e começou a comer.");
                RandomSleep.between(1000, 3000);
                long endEat = System.nanoTime();

                // map ids for utilization; left/right fork ids matter only by id number
                stats.recordEatEnd(id, left.id(), right.id(), endEat - startEat);
                Log.info("Filósofo " + id + " terminou de comer e soltou os garfos.");
            }
        }
    }
}
