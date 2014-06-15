package net.frozenorb.foxtrot.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		StringBuilder sb = new StringBuilder(64);

		if (hours != 0) {
			sb.append(hours);
			sb.append(" hours");
		}

		if (minutes != 0) {
			sb.append((hours != 0 ? ", " : "") + minutes);
			sb.append(" minutes");
		}

		if (seconds != 0) {
			sb.append((minutes != 0 ? ", " : "") + seconds);
			sb.append(" seconds");
		}

		return (sb.toString());
	}
}
