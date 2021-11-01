package gartham.c10ver.economy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.users.EconomyUser;

/**
 * <h1>RewardsOperation</h1>
 * <p>
 * Represents a single operation of rewarding a user.
 * </p>
 * <h3>Overview</h3>
 * <p>
 * This class stores all the data relevant to the task of rewarding a user once
 * for something. There are (currently) three types of "loot" that a user can
 * earn:
 * <ol>
 * <li>Cloves</li>
 * <li>Items</li>
 * <li>Multipliers</li>
 * </ol>
 * Multipliers affect the number of cloves that a user actually <i>receives</i>
 * when a user <i>earns</i> a certain amount, and since multipliers can come
 * from a variety of places, the situation is complicated. Because of this, the
 * {@link RewardsOperation} abstraction has been made.
 * </p>
 * <h3>Earnings vs Rewards</h3>
 * <p>
 * The number of cloves a user <b>earns</b> is the number of cloves they get,
 * disregarding multipliers. The number of cloves <b>rewarded</b> to a user is
 * the number of cloves earned multiplied by the {@link #getTotalMultiplier()
 * total multiplier} that is applied to the earnings.
 * </p>
 * <h3>Multipliers</h3> Currently, there are three types of multipliers:
 * <ul>
 * <li>Personal Multipliers</li>
 * <li>Server Multipliers</li>
 * <li>Nitro Boosting</li>
 * </ul>
 * <p>
 * Personal multipliers are, themselves, rewards that a user can earn from
 * interacting with C10ver. C10ver keeps track of these on a per-user basis, and
 * any user's personal multipliers, as well as their total personal multiplier,
 * can be obtained from the user's {@link EconomyUser economy user object}
 * through the methods {@link EconomyUser#getMultipliers()} and
 * {@link EconomyUser#getPersonalTotalMultiplier()}, respectively.
 * </p>
 * <p>
 * Server multipliers are multipliers that affect a whole server (meaning, a
 * single server multiplier affects every user in a server, not just a single
 * user). Server multipliers are also automatically kept track of by C10ver.
 * </p>
 * <p>
 * Nitro Boosting a server also provides an additional bonus to a user's
 * multipliers.
 * </p>
 * 
 * @author Gartham
 *
 */
public class RewardsOperation {

	/**
	 * The items to give the user.
	 */
	private final Inventory items = new Inventory();
	/**
	 * The cloves to give the user.
	 */
	private BigInteger cloves;
	/**
	 * The multipliers to give the user.
	 */
	private final Map<AbstractMultiplier, Integer> mults = new HashMap<>();
	/**
	 * Whether to use the {@link #mults} earned as a result of this
	 * {@link RewardsOperation} on the {@link #cloves} earned in this operation.
	 * This is <code>true</code> by default. If this value is <code>true</code>, the
	 * multipliers granted to the user when rewards are bestowed are stacked
	 * (additively) with the personal multipliers for the user.
	 */
	private boolean applyEarnedMultipliers = true;
	/**
	 * Whether to save the user's information (only the information that has been
	 * modified) upon reward reception.
	 */
	private boolean shouldSave = true;

	/**
	 * Represents the total personal multiplier applied to this
	 * {@link RewardsOperation}. This is distinct from {@link #otherMultipliers}
	 * because this value is added to (before being multiplied with
	 * {@link #otherMultipliers}) if {@link #applyEarnedMultipliers} is
	 * <code>true</code>.
	 * 
	 * Multipliers stack additively with other multipliers of the same type (e.g.
	 * two personal multipliers are added to get a total personal multiplier) but
	 * multiplicatively with each of other types (a total personal multiplier is
	 * multiplied by a server multiplier to give a final reward amount).
	 */
	private BigDecimal personalMultiplier;
	/**
	 * All non-personal multipliers that will take place during this
	 * {@link RewardsOperation} combined into a final multiplier.
	 * <code>(sum(serverMultipliers) + 1) * (nitroMultiplier + 1)</code>. The
	 * <code>+1</code>s are because users should have a multiplier of <code>1</code>
	 * in any category if they don't have any active multipliers there (otherwise
	 * their final rewards will be zero :( ).
	 */
	private BigDecimal otherMultipliers;

	/**
	 * <p>
	 * Determines whether any modifications to a user's data should be saved as a
	 * result of applying this {@link RewardsOperation}. Defaults to
	 * <code>true</code>.
	 * </p>
	 * <p>
	 * Usually, after rewarding a user (for example, if they open a crate), you will
	 * want to save that data immediately (since the bot can crash at any time), so
	 * after executing a {@link RewardsOperation} on a user (after <i>rewarding</i>
	 * a user), any of the user's data that has been modified by the rewarding gets
	 * saved to the filesystem. Only files which have been modified get saved, so if
	 * a user gets rewarded some items, but no cloves or multipliers, then only the
	 * user's {@link EconomyUser#getInventory() inventory} will get saved to the
	 * filesystem. Everything else will remain untouched!
	 * </p>
	 * <p>
	 * In some cases, it may be desired to not save a user's data automatically
	 * after the user is rewarded. A good example of this is when a user receives
	 * some cloves for sending a message. Message sending happens extremely often,
	 * so if each user's data was saved every time it sent a message, the bot would
	 * constantly be overwriting files with (mostly) the same data (only updating
	 * the cloves a user has). This can quickly be problematic for situations where
	 * a community is active, so a developer may choose to <i>defer</i> saving. The
	 * default behavior in C10ver is to save data every 16th message a user sends
	 * (unless the user receives an item in which case saving happens immediately).
	 * This allows 1/16th of the number of filesystem save operations to take place,
	 * while still saving a user's data relatively often (if a user loses the past
	 * ~8 or so messages worth of cloves, it's usually not a problem, or even
	 * noticeable). In most cases however, it is easier to simply allow the
	 * {@link RewardsOperation} API automatically save whatever files it updates
	 * when it's executed on a user.
	 * </p>
	 * 
	 * @return <code>true</code> if automatic saving after the
	 *         {@link RewardsOperation} is executed is enabled, <code>false</code>
	 *         otherwise.
	 */
	public boolean isShouldSave() {
		return shouldSave;
	}

	/**
	 * Sets {@link #isShouldSave()}.
	 * 
	 * @param save <code>true</code> if automatic saving after the
	 *             {@link RewardsOperation} is executed is enabled,
	 *             <code>false</code> otherwise.
	 */
	public void setShouldSave(boolean save) {
		this.shouldSave = save;
	}

	/**
	 * Gets the <i>raw</i> number of cloves to be applied in this
	 * {@link RewardsOperation}.
	 * 
	 * @return The raw number of cloves to be applied once this
	 *         {@link RewardsOperation} executes.
	 * @see #getCloves()
	 */
	public BigInteger getCloves() {
		return cloves;
	}

	/**
	 * Sets the <i>raw</i> number of cloves to be applied in this
	 * {@link RewardsOperation}. The exact amount of cloves rewarded to the user is
	 * calculated by {@link #getRewardedCloves()}.
	 * 
	 * @param cloves The number of cloves to be given in this reward.
	 */
	public void setCloves(BigInteger cloves) {
		this.cloves = cloves;
	}

	/**
	 * Returns the variable that determines whether the multipliers rewarded to this
	 * user through this {@link RewardsOperation} should also multiply any cloves
	 * given to this user through this {@link RewardsOperation}.
	 * 
	 * @return <code>true</code> if rewarded multipliers affect earned cloves,
	 *         <code>false</code> otherwise.
	 */
	public boolean isApplyEarnedMultipliers() {
		return applyEarnedMultipliers;
	}

	/**
	 * Sets whether multipliers given to the user when this {@link RewardsOperation}
	 * is executed will also multiply the cloves given to this user through this
	 * {@link RewardsOperation}. This is <code>true</code> by default.
	 * 
	 * @param applyEarnedMultipliers <code>true</code> if mults earned in this
	 *                               rewards op should affect the cloves rewarded.
	 */
	public void setApplyEarnedMultipliers(boolean applyEarnedMultipliers) {
		this.applyEarnedMultipliers = applyEarnedMultipliers;
	}

	/**
	 * Modifiable {@link Inventory} object containing all the items that will be
	 * given to the user when this {@link RewardsOperation} is executed.
	 * 
	 * @return The modifiable container for all the items to give to the user.
	 */
	public Inventory getItems() {
		return items;
	}

	/**
	 * <p>
	 * Gets the modifiable frequency {@link Map} of multipliers that will be granted
	 * to the user once this {@link RewardsOperation} is executed on a user.
	 * </p>
	 * <p>
	 * The format of this map is:
	 * 
	 * <pre>
	 * 	<code>
	 * 		{ mult:count, mult:count, ... }
	 * 	</code>
	 * </pre>
	 * 
	 * what this means is that if a user has two entries in this map:
	 * 
	 * <pre>
	 * 	<code>
	 * 		{ 0.5:4, 1:5 }
	 * 	</code>
	 * </pre>
	 * 
	 * then the total personal multiplier they get rewarded is
	 * <code>0.5 * 4 + 1 * 5 = 2 + 5 = 7</code>. This would make their final
	 * {@link #getPersonalMultiplier()} become <code>8</code> once it is applied.
	 * 
	 * @return The modifiable {@link Map} of {@link AbstractMultiplier}s to give the
	 *         user.
	 */
	public Map<AbstractMultiplier, Integer> getMults() {
		return mults;
	}

	/**
	 * Simply gets the personal multiplier value stored. See
	 * {@link #setPersonalMultiplier(BigDecimal)} for details.
	 * 
	 * @return {@link #personalMultiplier}.
	 * @see #getTotalMultiplier()
	 * @see #setPersonalMultiplier(BigDecimal)
	 * @see #setOtherMultipliers(BigDecimal)
	 * @see #getOtherMultipliers()
	 */
	public BigDecimal getPersonalMultiplier() {
		return personalMultiplier;
	}

	/**
	 * <p>
	 * Sets the total personal multiplier to be applied to this
	 * {@link RewardsOperation}. This is simply the sum of the active personal
	 * multipliers the user has when they get the reward (+1). Multipliers are
	 * always <code>1</code> by default, because if someone has no active
	 * multipliers, they receive 1x as many rewards. Setting this value to
	 * <code>0</code> will give the user <code>0</code> cloves when the reward
	 * operation is executed.
	 * </p>
	 * <p>
	 * If this value is <code>null</code> it is calculated at the time the operation
	 * is executed.
	 * </p>
	 * 
	 * @param personalMultiplier The total personal multiplier to apply to the
	 *                           operation.
	 * @see #getTotalMultiplier()
	 * @see #getPersonalMultiplier()
	 * @see #setOtherMultipliers(BigDecimal)
	 * @see #getOtherMultipliers()
	 */
	public void setPersonalMultiplier(BigDecimal personalMultiplier) {
		this.personalMultiplier = personalMultiplier;
	}

	/**
	 * Gets the net result of all the multipliers that are not personal (or earned
	 * as a result of this {@link RewardsOperation}). See
	 * {@link #setOtherMultipliers(BigDecimal)} for details.
	 * 
	 * @return The current value of {@link #otherMultipliers}.
	 * @see #getTotalMultiplier()
	 * @see #getPersonalMultiplier()
	 * @see #setPersonalMultiplier(BigDecimal)
	 * @see #setOtherMultipliers(BigDecimal)
	 */
	public BigDecimal getOtherMultipliers() {
		return otherMultipliers;
	}

	/**
	 * The net result of all the multipliers that are not personal (or earned inside
	 * this {@link RewardsOperation}). Right now, this is typically a multiplier
	 * based on whether the user has boosted the server multiplied by any server
	 * multipliers that are in effect in the server that the rewards were earned in.
	 * <p>
	 * If this value is <code>null</code> it is calculated at the time the operation
	 * is executed.
	 * </p>
	 * 
	 * @param otherMultipliers The non-personal active multipliers to apply to the
	 *                         transaction. This value is multiplied with
	 *                         {@link #setPersonalMultiplier(BigDecimal)} to obtain
	 *                         the total multiplier. For details, see
	 *                         {@link #getTotalMultiplier()}.
	 * @see #getTotalMultiplier()
	 * @see #getPersonalMultiplier()
	 * @see #setPersonalMultiplier(BigDecimal)
	 * @see #getOtherMultipliers()
	 */
	public void setOtherMultipliers(BigDecimal otherMultipliers) {
		this.otherMultipliers = otherMultipliers;
	}

	/**
	 * Gets the total, final multiplier that will be used in the transaction. This
	 * is the result multiplied with the number of cloves earned to give the number
	 * of cloves received.
	 * 
	 * <pre>
	 * 	(Received Cloves = Earned Cloves * Total Effective Multiplier)
	 * </pre>
	 * 
	 * @return The final multiplier, with {@link #applyEarnedMultipliers} factored
	 *         into the calculation if it's <code>true</code>.
	 * @see #getPersonalMultiplier()
	 * @see #setPersonalMultiplier(BigDecimal)
	 * @see #setOtherMultipliers(BigDecimal)
	 * @see #getOtherMultipliers()
	 */
	public BigDecimal getTotalMultiplier() {
		var m = personalMultiplier;
		if (applyEarnedMultipliers)
			for (var e : mults.entrySet())
				m = m.add(e.getKey().getAmt().multiply(BigDecimal.valueOf(e.getValue())));
		return m.multiply(otherMultipliers);
	}

	/**
	 * Calculates and returns the exact number of cloves that will be rewarded to
	 * the user once this operation is executed. This method simply performs:
	 * 
	 * <pre>
	 * 	<code>
	 * 		{@link #getTotalMultiplier()} * {@link #getCloves()}
	 * 	</code>
	 * </pre>
	 * 
	 * and returns the result. <b>Any remaining fractional cloves are discarded.</b>
	 * This method does not perform rounding when applying the multiplier to cloves.
	 * This means that if you earn <code>1</code> clove with a multiplier of, for
	 * example, <code>1.35</code>, you will get exactly <code>1</code> clove as a
	 * result!
	 * 
	 * @return The raw number of cloves being rewarded multiplied by the final total
	 *         multiplier. Specifically:
	 * 
	 *         <pre>
	 *         <code>new BigDecimal(getCloves()).multiply(getTotalMultiplier()).toBigInteger();</code>
	 *         </pre>
	 */
	public BigInteger getRewardedCloves() {
		return new BigDecimal(getCloves()).multiply(getTotalMultiplier()).toBigInteger();
	}

}
