package tarefa3;

import common.*;

import java.util.concurrent.Semaphore;

public final class Philosopher extends Thread {
    private final int id;
    private final Fork left;
    private final Fork right;
    private final StopSignal stop;
    private final Stats stats;
    private final Semaphore limit; // at most 4 philosophers trying at once

    public Philosopher(int id, Fork left, Fork right, StopSignal stop, Stats stats, Semaphore limit) {
        super("F" + id);
        this.id = id;
        this.left = left;
        this.right = right;
        this.stop = stop;
        this.stats = stats;
        this.limit = limit;
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

        Log.info("Filósofo " + id + " pede permissão no semáforo (máx 4 tentando).");
        limit.acquire();
        try {
            Log.info("Filósofo " + id + " autorizado; tenta pegar " + left + " e " + right + ".");
            synchronized (left) {
                synchronized (right) {
                    long startEat = System.nanoTime();
                    stats.recordEatStart(id, startEat - attemptAt);

                    Log.info("Filósofo " + id + " pegou ambos os garfos e começou a comer.");
                    RandomSleep.between(1000, 3000);
                    long endEat = System.nanoTime();

                    stats.recordEatEnd(id, left.id(), right.id(), endEat - startEat);
                    Log.info("Filósofo " + id + " terminou de comer e soltou os garfos.");
                }
            }
        } finally {
            limit.release();
            Log.info("Filósofo " + id + " liberou permissão do semáforo.");
        }
    }
}
