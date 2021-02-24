package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.items.Item;

public abstract class Foodstuff extends Item {

	/**
	 * <p>
	 * A convenience function to generate a {@link BigInteger} representing the
	 * number of seconds provided, in millis.
	 * </p>
	 * <p>
	 * If you give this function <code>1</code>, it will give you back a
	 * {@link BigInteger} of the value <code>1000</code>, because there are 1000
	 * milliseconds in a single second.
	 * </p>
	 * 
	 * @param seconds The number of seconds this {@link BigInteger} should
	 *                represent.
	 * @return A {@link BigInteger} representing the requested number of seconds, in
	 *         milliseconds.
	 */
	protected static final BigInteger sec(long seconds) {
		return BigInteger.valueOf(seconds).multiply(BigInteger.valueOf(1000));
	}

	/**
	 * <p>
	 * A convenience function to generate a {@link BigInteger} representing the
	 * number of minutes provided, in milliseconds.
	 * </p>
	 * 
	 * @param minutes The number of minutes this {@link BigInteger} should
	 *                represent.
	 * @return A {@link BigInteger} representing the requested number of minutes, in
	 *         milliseconds.
	 * @see #sec(long)
	 */
	protected static final BigInteger min(long minutes) {
		return sec(minutes).multiply(BigInteger.valueOf(60));
	}

	/**
	 * <p>
	 * A convenience function to generate a {@link BigInteger} representing the
	 * number of hours provided, in milliseconds.
	 * </p>
	 * 
	 * @param hours The number of hours that the resulting {@link BigInteger} should
	 *              represent.
	 * @return A {@link BigInteger} representing the requested number of hours, in
	 *         milliseconds.
	 * @see #sec(long)
	 */
	protected static final BigInteger hr(long hours) {
		return min(hours).multiply(BigInteger.valueOf(60));
	}

	/**
	 * <p>
	 * A convenience function to generate a {@link BigInteger} representing the
	 * number of days provided, in milliseconds.
	 * </p>
	 * 
	 * @param days The number of days that the resulting {@link BigInteger} will
	 *             represent.
	 * @return A {@link BigInteger} representing the requested number of days, in
	 *         milliseconds.
	 * @see #sec(long)
	 */
	protected static final BigInteger day(long days) {
		return hr(days).multiply(BigInteger.valueOf(24));
	}

	/**
	 * <p>
	 * This is the value that this food item will give to a user's multiplier when
	 * that person consumes this item.
	 * </p>
	 * <p>
	 * If this value is <font color=red><code>0.5</code></font> and someone with a
	 * multiplier of <font color=red><code>1.0</code></font> consumes this item,
	 * that person will end up with a multiplier of
	 * <font color=red><code>1.5</code></font> for however long this item lasts. The
	 * multipliers are added.
	 * </p>
	 */
	private final Property<BigDecimal> multiplier = bigDecimalProperty("mult");
	/**
	 * This is how long the multiplier effect of this food will last, in
	 * milliseconds.
	 */
	private final Property<BigInteger> ttl = bigIntegerProperty("ttl");

	public BigDecimal getMultiplier() {
		return multiplier.get();
	}

	protected void setMultiplier(BigDecimal multiplierValue) {
		this.multiplier.set(multiplierValue);
	}

	protected void setTTL(BigInteger ttl) {
		this.ttl.set(ttl);
	}

	public BigInteger getTTL() {
		return ttl.get();
	}

	public Foodstuff(String type, JSONObject properties) {
		super(type, properties);
		load(multiplier, properties);
	}

	public Foodstuff(String type) {
		super(type);
	}

}
