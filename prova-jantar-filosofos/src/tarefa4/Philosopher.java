package tarefa4;

import common.*;

public final class Philosopher extends Thread {
    private final int id;
    private final StopSignal stop;
    private final Stats stats;
    private final Mesa mesa;

    public Philosopher(int id, StopSignal stop, Stats stats, Mesa mesa) {
        super("F" + id);
        this.id = id;
        this.stop = stop;
        this.stats = stats;
        this.mesa = mesa;
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

        Log.info("Filósofo " + id + " pede à Mesa para comer.");
        mesa.requestToEat(id);

        long startEat = System.nanoTime();
        stats.recordEatStart(id, startEat - attemptAt);

        Log.info("Filósofo " + id + " começou a comer.");
        RandomSleep.between(1000, 3000);
        long endEat = System.nanoTime();

        // Utilização: garfos são os IDs left=id e right=(id+1)%5
        stats.recordEatEnd(id, id, (id + 1) % 5, endEat - startEat);

        mesa.doneEating(id);
        Log.info("Filósofo " + id + " terminou de comer.");
    }
}
