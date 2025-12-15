package common;

public final class StopSignal {
    private volatile boolean running = true;

    public boolean isRunning() { return running; }
    public void stop() { running = false; }
}
