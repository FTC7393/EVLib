package ftc.evlib.util;

import android.util.Log;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/26/16
 */
public class StepTimer {
    private final String tag;
    private long timer;

    public StepTimer(String tag) {
        this.tag = tag;
        start();
    }

    public void start() {
        timer = System.nanoTime();
    }

    //timing method
    public void log(String message) {
        Log.i(tag, "TIME: " + message + ": " + String.valueOf(get()) + " ms");
    }

    public double get() {
        return (System.nanoTime() - timer) / 1000000.0;
    }
}
