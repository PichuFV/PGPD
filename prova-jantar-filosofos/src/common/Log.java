package common;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Log {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private Log() {}

    public static synchronized void info(String msg) {
        String ts = LocalTime.now().format(FMT);
        String th = Thread.currentThread().getName();
        System.out.println(ts + " [" + th + "] " + msg);
    }
}
