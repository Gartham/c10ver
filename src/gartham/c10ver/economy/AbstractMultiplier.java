package gartham.c10ver.economy;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AbstractMultiplier {
	private final BigDecimal amt;
	private final Duration duration;

	public AbstractMultiplier(BigDecimal amt, Duration duration) {
		this.amt = amt;
		this.duration = duration;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public Duration getDuration() {
		return duration;
	}

	public static AbstractMultiplier ofSec(long secondsTillExpired, BigDecimal value) {
		return new AbstractMultiplier(value, Duration.of(secondsTillExpired, ChronoUnit.SECONDS));
	}

	public static AbstractMultiplier ofMin(long minutesTillExpired, BigDecimal value) {
		return new AbstractMultiplier(value, Duration.of(minutesTillExpired, MINUTES));
	}

	public static AbstractMultiplier ofHr(long hrsTillExpired, BigDecimal value) {
		return new AbstractMultiplier(value, Duration.of(hrsTillExpired, HOURS));
	}

	public static AbstractMultiplier ofDay(long daysTillExpired, BigDecimal value) {
		return new AbstractMultiplier(value, Duration.of(daysTillExpired, DAYS));
	}

	public static AbstractMultiplier ofWk(long weeksTillExpired, BigDecimal value) {
		return new AbstractMultiplier(value, Duration.of(weeksTillExpired, WEEKS));
	}

	public static AbstractMultiplier ofMth(long monthsTillExpired, BigDecimal value) {
		return new AbstractMultiplier(value, Duration.of(monthsTillExpired, MONTHS));
	}

	public static AbstractMultiplier ofYr(long yrsTillExpired, BigDecimal value) {
		return new AbstractMultiplier(value, Duration.of(yrsTillExpired, YEARS));
	}

	public Multiplier reify() {
		return new Multiplier(Instant.now().plus(duration), amt);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amt == null) ? 0 : amt.hashCode());
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractMultiplier other = (AbstractMultiplier) obj;
		if (amt == null) {
			if (other.amt != null)
				return false;
		} else if (!amt.equals(other.amt))
			return false;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		return true;
	}

}
