package gartham.c10ver.economy;

import java.math.BigDecimal;
import java.time.Instant;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.data.PropertyObject;

public final class Multiplier extends PropertyObject {
	private final Property<Instant> expr = instantProperty("expr");
	private final Property<BigDecimal> amt = bigDecimalProperty("amt");

	public Instant getExpiration() {
		return expr.get();
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
