package common;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/** Best-effort deadlock detector for intrinsic locks (synchronized). */
public final class DeadlockDetector {
    private DeadlockDetector() {}

    public static boolean logIfDeadlocked() {
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] ids = mx.findDeadlockedThreads();
        if (ids == null || ids.length == 0) return false;

        Log.info("⚠ DEADLOCK detectado! Threads envolvidas:");
        ThreadInfo[] infos = mx.getThreadInfo(ids, true, true);
        for (ThreadInfo ti : infos) {
            if (ti == null) continue;
            Log.info(" - " + ti.getThreadName() + " esperando por " + ti.getLockName()
                    + " (detém: " + ti.getLockOwnerName() + ")");
        }
        return true;
    }
}
