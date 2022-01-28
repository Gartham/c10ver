package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.AbstractMultiplier;
import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.ItemCategory;
import gartham.c10ver.economy.items.utility.Consumable;
import gartham.c10ver.economy.users.EconomyUser;

public class Foodstuff extends Item implements Consumable {

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
	protected static final long sec(long seconds) {
		return seconds * 1000;
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
	protected static final long min(long minutes) {
		return sec(minutes) * 60;
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
	protected static final long hr(long hours) {
		return min(hours) * 60;
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
	protected static final long day(long days) {
		return hr(days) * 24;
	}

	private static final String MULTIPLIER_PK = "mult", TTL_PK = "ttl";

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
	private Property<BigDecimal> multiplierProperty() {
		return getProperty(MULTIPLIER_PK);
	}

	{
		bigDecimalProperty(MULTIPLIER_PK).setTransient(true).setAttribute(false);
		longProperty(TTL_PK).setTransient(true).setAttribute(false);
	}

	/**
	 * This is how long the multiplier effect of this food will last, in
	 * milliseconds.
	 */
	private Property<Long> ttlProperty() {
		return getProperty(TTL_PK);
	}

	public BigDecimal getMultiplier() {
		return multiplierProperty().get();
	}

	protected void setMultiplier(BigDecimal multiplierValue) {
		multiplierProperty().set(multiplierValue);
	}

	protected void setTTL(long ttl) {
		ttlProperty().set(ttl);
	}

	public long getTTL() {
		return ttlProperty().get();
	}

	public Foodstuff(String type, JSONObject properties) {
		super(type, ItemCategory.FOOD, properties);
		load(multiplierProperty(), properties);
	}

	public Foodstuff(String type) {
		super(type, ItemCategory.FOOD);
	}

	@Override
	public final void consume(EconomyUser user) {
		user.addMultiplier(new Multiplier(Instant.now().plusMillis(getTTL()), getMultiplier()));
	}

	public final AbstractMultiplier mult() {
		return new AbstractMultiplier(getMultiplier(), Duration.ofMillis(getTTL()));
	}

}
