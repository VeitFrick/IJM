package at.aau.softwaredynamics.runner.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String millisecondsToPrettyString(long ms) {
        return String.format("%02d h, %02d min, %02d sec",
                TimeUnit.MILLISECONDS.toHours(ms),
                TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms))
        );
    }
}
