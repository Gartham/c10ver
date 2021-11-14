package gartham.c10ver.economy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.users.EconomyUser;
import net.dv8tion.jda.api.entities.Guild;

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

	public boolean hasItems() {
		return !items.isEmpty();
	}

	public boolean hasCloves() {
		return cloves.signum() != 0;
	}

	public boolean hasMults() {
		return !mults.isEmpty();
	}

	/**
	 * The items to give the user.
	 */
	private final Inventory items = new Inventory();
	/**
	 * The cloves to give the user.
	 */
	private BigInteger cloves = BigInteger.ZERO;
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
	private BigDecimal personalMultiplier = BigDecimal.ONE;
	/**
	 * All non-personal multipliers that will take place during this
	 * {@link RewardsOperation} combined into a final multiplier.
	 * <code>(sum(serverMultipliers) + 1) * (nitroMultiplier + 1)</code>. The
	 * <code>+1</code>s are because users should have a multiplier of <code>1</code>
	 * in any category if they don't have any active multipliers there (otherwise
	 * their final rewards will be zero :( ).
	 */
	private BigDecimal otherMultipliers = BigDecimal.ONE;
	/**
	 * Used to calculate the {@link #otherMultipliers} if it is not specified.
	 */
	private Guild guild;

	/**
	 * Gets the {@link Guild} that this reward was earned by a user in. This
	 * {@link Guild} is used to calculate what server to give, and it can be
	 * <code>null</code>. If {@link #getOtherMultipliers() otherMultipliers} is set,
	 * this value is ignored. Otherwise, this value is used to calculate
	 * {@link #getOtherMultipliers() otherMultipliers}, at the time of the
	 * {@link RewardsOperation} being executed, through the following calculations:
	 * 
	 * <pre>
	 * 	<code>
	 * 		otherMultipliers = totalServerMultiplier * nitroMultiplier
	 * 		nitroMultiplier = 1 + daysBoosted * 0.02
	 * 	</code>
	 * </pre>
	 * 
	 * @return The guild for this {@link RewardsOperation}.
	 */
	public Guild getGuild() {
		return guild;
	}

	public RewardsOperation setGuild(Guild guild) {
		this.guild = guild;
		return this;
	}

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
	public RewardsOperation setShouldSave(boolean save) {
		this.shouldSave = save;
		return this;
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
	public RewardsOperation setCloves(BigInteger cloves) {
		this.cloves = cloves;
		return this;
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
	public RewardsOperation setApplyEarnedMultipliers(boolean applyEarnedMultipliers) {
		this.applyEarnedMultipliers = applyEarnedMultipliers;
		return this;
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
	 * 
	 * @param personalMultiplier The total personal multiplier to apply to the
	 *                           operation.
	 * @see #getTotalMultiplier()
	 * @see #getPersonalMultiplier()
	 * @see #setOtherMultipliers(BigDecimal)
	 * @see #getOtherMultipliers()
	 */
	public RewardsOperation setPersonalMultiplier(BigDecimal personalMultiplier) {
		this.personalMultiplier = personalMultiplier;
		return this;
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
	public RewardsOperation setOtherMultipliers(BigDecimal otherMultipliers) {
		this.otherMultipliers = otherMultipliers;
		return this;
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

	public static RewardsOperation build(EconomyUser user, Guild guild, BigInteger cloves) {
		var rew = new RewardsOperation();
		rew.setCloves(cloves);
		rew.autoSetMultipliers(user, guild);
		return rew;
	}

	public static RewardsOperation build(EconomyUser user, Guild guild, ItemBunch<?>... items) {
		var rew = new RewardsOperation();
		rew.getItems().add(items);
		rew.autoSetMultipliers(user, guild);
		return rew;
	}

	/**
	 * <p>
	 * Sets the {@link #personalMultiplier} and {@link #otherMultipliers} of this
	 * {@link RewardsOperation} given the {@link EconomyUser} and {@link Guild} (if
	 * any), in which the rewards were earned. The {@link Guild} can be
	 * <code>null</code>.
	 * </p>
	 * <p>
	 * This method calculates the appropriate multipliers for the
	 * {@link RewardsOperation} based off of the provided user and guild.
	 * <ul>
	 * <li>The {@link #otherMultipliers} is set to the user's nitro-multiplier
	 * (based on the time boosted of the specified user in the specified guild) and
	 * the specified guild's {@link Server#getTotalServerMultiplier() total server
	 * multiplier}.</li>
	 * <li>The {@link #personalMultiplier} is based off of the user's
	 * {@link EconomyUser#getPersonalTotalMultiplier() personal total
	 * multiplier}.</li>
	 * </ul>
	 * Everything is calculated and set at the time this method is called.
	 * </p>
	 * <p>
	 * If the provided {@link Guild} is <code>null</code>, then the
	 * {@link #otherMultipliers} will not be modified by the call to this method.
	 * </p>
	 * 
	 * @param user  The {@link EconomyUser} which earned the rewards.
	 * @param guild The {@link Guild} in which the rewards were earned.
	 * @return This {@link RewardsOperation} object.
	 */
	public RewardsOperation autoSetMultipliers(EconomyUser user, Guild guild) {

		setPersonalMultiplier(user.getPersonalTotalMultiplier());

		Economy economy = user.getEconomy();
		if (guild != null) {
			if (economy.hasServer(guild.getId())) {
				var s = economy.getServer(guild.getId());
				setOtherMultipliers(s.getTotalServerMultiplier());
			}

			try {
				var memb = guild.retrieveMember(user.getUser()).complete();
				if (memb != null) {
					var tb = memb.getTimeBoosted();
					if (tb != null) {
						var nitromult = BigDecimal.valueOf(13, 1)
								.add(BigDecimal.valueOf(Duration.between(tb.toInstant(), Instant.now()).toDays() + 1)
										.multiply(BigDecimal.valueOf(1, 2)));
						setOtherMultipliers(getOtherMultipliers().multiply(nitromult));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this;
	}

	public static RewardsOperation build(EconomyUser user, Guild guild, BigInteger cloves, ItemBunch<?>... items) {
		var rew = build(user, guild, cloves);
		rew.getItems().add(items);
		return rew;
	}

	public static RewardsOperation build(EconomyUser user, BigInteger cloves, ItemBunch<?>... items) {
		var rew = build(user, items);
		rew.setCloves(cloves);
		rew.setPersonalMultiplier(user.getPersonalTotalMultiplier());
		return rew;
	}

	public static RewardsOperation build(EconomyUser user, ItemBunch<?>... items) {
		var rew = new RewardsOperation();
		rew.getItems().add(items);
		rew.setPersonalMultiplier(user.getPersonalTotalMultiplier());
		return rew;
	}

	/**
	 * <p>
	 * Builds a {@link RewardsOperation} with the specified User's personal
	 * multiplier and the specified characteristics. The {@link Guild} is
	 * <code>null</code>able. The {@link EconomyUser} argument is used to calculate
	 * the personal multiplier to apply to the {@link RewardsOperation}. The
	 * {@link EconomyUser#getPersonalTotalMultiplier() personal multiplier} of the
	 * provided {@link EconomyUser} is retrieved and used at the time of the method
	 * being invoked.
	 * </p>
	 * <p>
	 * The returned {@link RewardsOperation} has {@link #shouldSave} set to
	 * <code>true</code>.
	 * </p>
	 * 
	 * @param user   The user whose personal multiplier will be set to
	 *               {@link #personalMultiplier} (used to calculate
	 *               {@link #personalMultiplier}. See
	 *               {@link #autoSetMultipliers(EconomyUser, Guild)}).
	 * @param guild  The {@link Guild} in which the operation occurred (used to
	 *               calculate {@link #otherMultipliers}; see
	 *               {@link #autoSetMultipliers(EconomyUser, Guild)}).
	 * @param cloves The cloves earned in the operation.
	 * @param mults  The multipliers earned in the operation. (The resulting
	 *               {@link RewardsOperation} has {@link #applyEarnedMultipliers}
	 *               set to <code>true</code>, which is the field's default value.)
	 * @param items  The items earned in the operation.
	 * @return The built {@link RewardsOperation}.
	 */
	public static RewardsOperation build(EconomyUser user, Guild guild, BigInteger cloves,
			Map<AbstractMultiplier, Integer> mults, ItemBunch<?>... items) {
		var rew = build(user, guild, cloves, items);
		rew.getMults().putAll(mults);
		return rew;
	}

	public static RewardsOperation build(EconomyUser user, Guild guild, BigInteger cloves,
			Map<AbstractMultiplier, Integer> mults, Iterable<ItemBunch<?>> items) {
		var rew = build(user, guild, cloves);
		rew.getItems().add(items);
		rew.getMults().putAll(mults);
		return rew;
	}

	/**
	 * Adds the specified number of cloves to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param cloves The cloves to add to this {@link RewardsOperation}.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(BigInteger cloves) {
		setCloves(getCloves().add(cloves));
		return this;
	}

	/**
	 * Adds the specified {@link AbstractMultiplier} to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param mult The {@link AbstractMultiplier} to add to this
	 *             {@link RewardsOperation}.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(AbstractMultiplier mult) {
		return with(mult, 1);
	}

	/**
	 * Adds the specified {@link AbstractMultiplier} and amount to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param mult   The {@link AbstractMultiplier} to add.
	 * @param amount The number of the multiplier that will be added. (E.g.,
	 *               <code>3</code> for this value will cause 3 of the specified
	 *               multiplier to be added to this {@link RewardsOperation}.)
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(AbstractMultiplier mult, int amount) {
		mults.put(mult, mults.containsKey(mult) ? mults.get(mult) + amount : amount);
		return this;
	}

	/**
	 * Adds the specified {@link AbstractMultiplier}s to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param mults The multipliers to add.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(Map<AbstractMultiplier, Integer> mults) {
		for (Entry<AbstractMultiplier, Integer> e : mults.entrySet())
			this.mults.put(e.getKey(),
					this.mults.containsKey(e.getKey()) ? e.getValue() + this.mults.get(e.getKey()) : e.getValue());
		return this;
	}

	/**
	 * Adds one of the specified {@link Item} to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param item The {@link Item} to add.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(Item item) {
		items.add(item);
		return this;
	}

	/**
	 * Adds the specified {@link ItemBunch}es to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param items The {@link Item}s, and their respective amounts.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(Iterable<ItemBunch<?>> items) {
		this.items.add(items);
		return this;
	}

	/**
	 * Adds the specified item to <b style="color:firebrick"><code>this</code></b>
	 * {@link RewardsOperation} and returns <code>this</code>
	 * {@link RewardsOperation}. This method does <b>NOT</b> return a new
	 * {@link RewardsOperation}.
	 * 
	 * @param item The item and amount of it to add.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(ItemBunch<?> item) {
		items.add(item);
		return this;
	}

	/**
	 * Adds the specified {@link ItemBunch}es to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param items The items and their respective amounts to add.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(ItemBunch<?>... items) {
		this.items.add(items);
		return this;
	}

	/**
	 * <p>
	 * Synonymous to {@link #with(ItemBunch...)}, but for {@link Iterator}s.
	 * </p>
	 * <p>
	 * Adds the specified {@link ItemBunch}es to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * </p>
	 * 
	 * @param items The items and their respective amounts to add.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(Iterator<ItemBunch<?>> items) {
		this.items.add(items);
		return this;
	}

	/**
	 * Copies the {@link Item}s from the specified {@link Inventory} into
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * 
	 * @param inv The {@link Inventory} to copy {@link Item}s from.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(Inventory inv) {
		inv.putInto(this.items);
		return this;
	}

	/**
	 * <p>
	 * Adds the
	 * <ol>
	 * <li>Cloves</li>
	 * <li>Items</li>
	 * <li>Multipliers</li>
	 * </ol>
	 * of the specified {@link RewardsOperation} to
	 * <b style="color:firebrick"><code>this</code></b> {@link RewardsOperation} and
	 * returns <code>this</code> {@link RewardsOperation}. This method does
	 * <b>NOT</b> return a new {@link RewardsOperation}.
	 * </p>
	 * <p>
	 * Additionally, this method <span style="color:firebrick">does not</span>
	 * further modify this {@link RewardsOperation}. If the properties
	 * ({@link #shouldSave} and {@link #personalMultiplier} e.g.) should be copied
	 * over from the specified {@link RewardsOperation} to this one in addition to
	 * the rewards being added, {@link #with(RewardsOperation, boolean)} should be
	 * called with <code>true</code> as the second argument.
	 * </p>
	 * 
	 * @param other The other {@link RewardsOperation} to copy the rewards from.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(RewardsOperation other) {
		return with(other.getCloves()).with(other.getMults()).with(other.getItems());
	}

	/**
	 * <p>
	 * Adds the cloves, items, and multipliers of the specified
	 * {@link RewardsOperation} to this one, just as {@link #with(RewardsOperation)}
	 * does, and then copies the properties of the specified
	 * {@link RewardsOperation} if <code>copyProperties</code> is <code>true</code>.
	 * If called with <code>copyProperties</code> being <code>false</code>, this
	 * method would be exactly equivalent to {@link #with(RewardsOperation)}.
	 * </p>
	 * <p>
	 * The following fields are copied from the specified {@link RewardsOperation}
	 * and then <b>added</b> to this {@link RewardsOperation}:
	 * <ol>
	 * <li>{@link #cloves}</li>
	 * <li>{@link #items}</li>
	 * <li>{@link #mults}</li>
	 * </ol>
	 * </p>
	 * <p>
	 * <b style="color:firebrick;">If</b> <code>copyProperties</code> is
	 * <code>true</code>, then <i>all</i> of the following properties are copied (if
	 * not, none of them are):
	 * <ol>
	 * <li>{@link #applyEarnedMultipliers}</li>
	 * <li>{@link #otherMultipliers}</li>
	 * <li>{@link #personalMultiplier}</li>
	 * <li>{@link #shouldSave}</li>
	 * <li>{@link #guild}</li>
	 * </ol>
	 * </p>
	 * 
	 * @param other          The other {@link RewardsOperation} to add (and copy
	 *                       properties) from.
	 * @param copyProperties Whether to also copy the properties of the
	 *                       {@link RewardsOperation}, or to just add all of the
	 *                       rewards.
	 * @return <code>this</code>.
	 */
	public RewardsOperation with(RewardsOperation other, boolean copyProperties) {
		with(other);
		if (copyProperties)
			setApplyEarnedMultipliers(other.isApplyEarnedMultipliers()).setOtherMultipliers(other.getOtherMultipliers())
					.setPersonalMultiplier(other.getPersonalMultiplier()).setShouldSave(other.isShouldSave())
					.setGuild(other.getGuild());
		return this;
	}

}
