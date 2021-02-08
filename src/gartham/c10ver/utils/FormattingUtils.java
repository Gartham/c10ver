package gartham.c10ver.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gartham.c10ver.utils.FormattingUtils.TimeUnit.*;

public class FormattingUtils {

	public enum TimeUnit {
		YEARS, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS;
	}

	public static String formatLargest(Duration duration, int amt) {
		if (amt < 0)
			throw new IllegalArgumentException("Illegal amt: " + amt);
		else if (amt == 0)
			return "";
		int nanoseconds = duration.getNano();
		long seconds = duration.getSeconds();

		final StringBuilder b = new StringBuilder();
		var values = getParts(duration);

		List<TimeUnit> units = new ArrayList<>(amt);

		boolean f = false;
		for (TimeUnit tu : TimeUnit.values()) {
			if (amt == 0)
				break;
			if (f || values.get(tu) != 0) {
				f = true;
				units.add(tu);
				amt--;
			}
		}

		if (units.isEmpty()) {
			units.add(SECONDS);
		}

		return format(values, units.toArray(new TimeUnit[units.size()]));

	}

	public static void main(String[] args) {
		System.out.println(formatLargest(Duration.ofSeconds(0), 3));
	}

	private static Map<TimeUnit, Long> getParts(Duration duration) {
		int nanoseconds = duration.getNano();
		long seconds = duration.getSeconds();

		Map<TimeUnit, Long> values = new HashMap<>();

		values.put(NANOSECONDS, (long) (nanoseconds % 1000));
		values.put(MICROSECONDS, (long) ((nanoseconds /= 1000) % 1000));
		values.put(MILLISECONDS, (long) (nanoseconds / 1000));
		values.put(SECONDS, (long) (int) (seconds % 60));
		values.put(MINUTES, (long) (int) ((seconds /= 60) % 60));
		values.put(HOURS, (long) (int) ((seconds /= 60) % 24));
		values.put(DAYS, (long) (int) ((seconds /= 24) % 365));
		values.put(YEARS, (long) (int) (seconds /= 365));

		return values;

	}

	private static String format(Map<TimeUnit, Long> parts, TimeUnit... units) {
		final StringBuilder b = new StringBuilder();

		for (TimeUnit tu : units) {
			b.append(parts.get(tu));
			switch (tu) {
			case DAYS:
				b.append("d ");
				break;
			case HOURS:
				b.append("h ");
				break;
			case MICROSECONDS:
				b.append("\u00b5s ");
				break;
			case MILLISECONDS:
				b.append("ms ");
				break;
			case MINUTES:
				b.append("m ");
				break;
			case NANOSECONDS:
				b.append("ns ");
				break;
			case SECONDS:
				b.append("s ");
				break;
			case YEARS:
				b.append("y ");
				break;
			}
		}

		return b.toString().trim();
	}

	public static String format(Duration duration, TimeUnit... units) {
		int nanoseconds = duration.getNano();
		long seconds = duration.getSeconds();

		Map<TimeUnit, Long> parts = getParts(duration);
		return format(parts, units);
	}

}
