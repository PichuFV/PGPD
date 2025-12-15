package tarefa1;

import common.*;

public final class Philosopher extends Thread {
    private final int id;
    private final Fork left;
    private final Fork right;
    private final StopSignal stop;

    public Philosopher(int id, Fork left, Fork right, StopSignal stop) {
        super("F" + id);
        this.id = id;
        this.left = left;
        this.right = right;
        this.stop = stop;
    }

    @Override
    public void run() {
        try {
            while (stop.isRunning()) {
                think();
                eat();
            }
        } catch (InterruptedException e) {
            // graceful shutdown
        }
    }

    private void think() throws InterruptedException {
        Log.info("Filósofo " + id + " começou a pensar.");
        RandomSleep.between(1000, 3000);
    }

    private void eat() throws InterruptedException {
        Log.info("Filósofo " + id + " tenta pegar " + left + " (esquerdo).");
        synchronized (left) {
            // small delay to increase deadlock chance
            Thread.sleep(10);
            Log.info("Filósofo " + id + " pegou " + left + " e tenta pegar " + right + " (direito).");
            synchronized (right) {
                Log.info("Filósofo " + id + " pegou ambos os garfos e começou a comer.");
                RandomSleep.between(1000, 3000);
                Log.info("Filósofo " + id + " terminou de comer e vai soltar " + left + " e " + right + ".");
            }
        }
    }
}
