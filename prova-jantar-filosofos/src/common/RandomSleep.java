package common;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomSleep {
    private RandomSleep() {}

    /** Sleep for random milliseconds in [minMs, maxMs]. */
    public static void between(int minMs, int maxMs) throws InterruptedException {
        int ms = ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
        Thread.sleep(ms);
    }
}
