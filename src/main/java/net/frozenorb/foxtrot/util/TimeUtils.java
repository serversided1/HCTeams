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

	/**
	 * Converts a given length, in seconds, to a formatted String.
	 * 
	 * @param i
	 *            the length in seconds
	 * @return string
	 */
	public static String getConvertedTime(long i) {
		i = Math.abs(i);
		int hours = (int) Math.floor(i / 3600);
		int remainder = (int) (i % 3600), minutes = remainder / 60, seconds = remainder % 60;
		String toReturn;
		if (seconds == 0 && minutes == 0)
			return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + "0 seconds";
		if (minutes == 0) {
			if (seconds == 1)
				return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + String.format("%s seconds", seconds);
			return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + String.format("%s seconds", seconds);
		}
		if (seconds == 0) {
			if (minutes == 1)
				return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + String.format("%sm", minutes);
			return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + String.format("%sm", minutes);
		}
		if (seconds == 1) {
			if (minutes == 1)
				return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + String.format("%sm %ss", minutes, seconds);
			return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + String.format("%sm %ss", minutes, seconds);
		}
		if (minutes == 1) {
			return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + "" + String.format("%sm %ss", minutes, seconds);
		}
		toReturn = String.format("%sm %ss", minutes, seconds);
		return (hours != 0 ? (hours == 1 ? hours + "h" : hours + "h") : "") + " " + toReturn;
	}
}
