package gartham.c10ver.economy;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.data.PropertyObject;
import static java.time.temporal.ChronoUnit.*;

public final class Multiplier extends PropertyObject {
	private final Property<Instant> expr = instantProperty("expr");
	private final Property<BigDecimal> amt = bigDecimalProperty("amt");

	public static Multiplier ofSec(long secondsTillExpired, BigDecimal value) {
		return new Multiplier(Instant.now().plusSeconds(secondsTillExpired), value);
	}

	public static Multiplier ofMin(long minutesTillExpired, BigDecimal value) {
		return new Multiplier(Instant.now().plus(minutesTillExpired, MINUTES), value);
	}

	public static Multiplier ofHr(long hrsTillExpired, BigDecimal value) {
		return new Multiplier(Instant.now().plus(hrsTillExpired, HOURS), value);
	}

	public static Multiplier ofDay(long daysTillExpired, BigDecimal value) {
		return new Multiplier(Instant.now().plus(daysTillExpired, DAYS), value);
	}

	public static Multiplier ofWk(long weeksTillExpired, BigDecimal value) {
		return new Multiplier(Instant.now().plus(weeksTillExpired, WEEKS), value);
	}

	public static Multiplier ofMth(long monthsTillExpired, BigDecimal value) {
		return new Multiplier(Instant.now().plus(monthsTillExpired, MONTHS), value);
	}

	public static Multiplier ofYr(long yrsTillExpired, BigDecimal value) {
		return new Multiplier(Instant.now().plus(yrsTillExpired, YEARS), value);
	}

	public Instant getExpiration() {
		return expr.get();
	}

	public Duration getTimeRemaining() {
		return Duration.between(Instant.now(), expr.get());
	}

	public BigDecimal getAmount() {
		return amt.get();
	}

	public Multiplier(Instant exprTime, BigDecimal amt) {
		expr.set(exprTime);
		this.amt.set(amt);
	}

	public Multiplier(JSONObject obj) {
		load(obj);
	}

	public Multiplier(JSONValue obj) {
		this((JSONObject) obj);
	}
}
