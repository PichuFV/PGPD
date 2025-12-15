package tarefa4;

import common.Log;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Monitor centralizado que controla garfos + fairness (fila FIFO).
 * Ideia: o filósofo só pode pegar garfos quando:
 *   (1) estiver na cabeça da fila, e
 *   (2) ambos os garfos estiverem livres.
 *
 * Isso elimina deadlock (não há aquisição circular descentralizada) e evita starvation (FIFO).
 */
public final class Mesa {
    private final boolean[] freeFork; // true = livre
    private final Deque<Integer> queue = new ArrayDeque<>();
    private final boolean[] enqueued;

    public Mesa(int forks, int philosophers) {
        this.freeFork = new boolean[forks];
        Arrays.fill(this.freeFork, true);
        this.enqueued = new boolean[philosophers];
    }

    public synchronized void requestToEat(int id) throws InterruptedException {
        if (!enqueued[id]) {
            queue.addLast(id);
            enqueued[id] = true;
            Log.info("Mesa: filósofo " + id + " entrou na fila (pos=" + queue.size() + ").");
        }

        int left = id;
        int right = (id + 1) % freeFork.length;

        while (true) {
            boolean isHead = !queue.isEmpty() && queue.peekFirst() == id;
            boolean forksFree = freeFork[left] && freeFork[right];

            if (isHead && forksFree) {
                // allocate forks atomically under monitor
                freeFork[left] = false;
                freeFork[right] = false;
                queue.removeFirst();
                enqueued[id] = false;
                Log.info("Mesa: filósofo " + id + " recebeu garfos G" + left + " e G" + right + ".");
                notifyAll();
                return;
            }

            wait();
        }
    }

    public synchronized void doneEating(int id) {
        int left = id;
        int right = (id + 1) % freeFork.length;

        freeFork[left] = true;
        freeFork[right] = true;
        Log.info("Mesa: filósofo " + id + " devolveu garfos G" + left + " e G" + right + ".");
        notifyAll();
    }
}
