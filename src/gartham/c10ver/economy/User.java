package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.util.StringGateway;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.accolades.AccoladeList;
import gartham.c10ver.economy.items.UserInventory;
import gartham.c10ver.economy.questions.Question;
import net.dv8tion.jda.api.entities.Guild;

public class User extends SavablePropertyObject {

	private final Property<Instant> dailyCommand = instantProperty("daily", Instant.MIN),
			weeklyCommand = instantProperty("weekly", Instant.MIN),
			monthlyCommand = instantProperty("monthly", Instant.MIN);
	private final Property<BigInteger> messageCount = bigIntegerProperty("message-count", BigInteger.ZERO),
			prestige = bigIntegerProperty("prestige", BigInteger.ZERO),
			voteCount = bigIntegerProperty("vote-count", BigInteger.ZERO);
	private final Property<ArrayList<Multiplier>> multipliers = listProperty("multipliers",
			toObjectGateway(Multiplier::new));
	private final Property<ArrayList<String>> joinedGuilds = listProperty("joined-guilds",
			toStringGateway(StringGateway.string()));

	public ArrayList<String> getJoinedGuilds() {
		return joinedGuilds.get();
	}

	public void setJoinedGuilds(ArrayList<String> joinedGuilds) {
		this.joinedGuilds.set(joinedGuilds);
	}

	public ArrayList<Multiplier> getMultipliers() {
		return MultiplierManager.getMultipliers(multipliers.get());
	}

	public BigDecimal getPersonalTotalMultiplier() {
		return MultiplierManager.getTotalValue(multipliers.get());
	}

	public void addMultiplier(Multiplier m) {
		MultiplierManager.addMultiplier(m, multipliers.get());
	}

	public BigInteger getMessageCount() {
		return messageCount.get();
	}

	public BigInteger getPrestige() {
		return prestige.get();
	}

	public void setMessageCount(BigInteger count) {
		messageCount.set(count);
	}

	public void setPrestige(BigInteger count) {
		prestige.set(count);
	}

	public void setVoteCount(BigInteger count) {
		voteCount.set(count);
	}

	public void incrementVoteCount() {
		setVoteCount(getVoteCount().add(BigInteger.ONE));
	}

	public void incrementMessageCount() {
		setMessageCount(getMessageCount().add(BigInteger.ONE));
	}

	public void incrementPrestige() {
		setPrestige(getPrestige().add(BigInteger.ONE));
	}

	private final Property<ArrayList<Question>> questions = listProperty("questions",
			toObjectGateway(t -> new Question((JSONObject) t)));
	{
		questions.set(new ArrayList<>());
	}

	public List<Question> getQuestions() {
		return questions.get();
	}

	private final UserAccount account;
	private final UserInventory inventory;
	private final Economy economy;
	private final AccoladeList accolades;
	private final String userID;

	public net.dv8tion.jda.api.entities.User getUser() {
		return economy.getClover().getBot().getUserById(userID);
	}

	public Economy getEconomy() {
		return economy;
	}

	public String getUserID() {
		return getSaveLocation().getParentFile().getName();
	}

	public UserInventory getInventory() {
		return inventory;
	}

	public AccoladeList getAccolades() {
		return accolades;
	}

	public UserAccount getAccount() {
		return account;
	}

	/**
	 * Calculates the multiplier applied to a reward that this user earned in the
	 * provided guild.
	 * 
	 * @param guild The guild that the reward was earned in.
	 * @return The total multiplier
	 *         (<code>{@link #getPersonalTotalMultiplier()} * nitro_multiplier * {@link Server#getTotalServerMultiplier()}</code>).
	 */
	public BigDecimal calcMultiplier(Guild guild) {
		var v = guild == null ? null : guild.getMember(getUser()).getTimeBoosted();
		var x = v == null ? BigDecimal.ONE
				: BigDecimal.valueOf(13, 1)
						.add(BigDecimal.valueOf(Duration.between(v.toInstant(), Instant.now()).toDays() + 1)
								.multiply(BigDecimal.valueOf(1, 2)));
		x = x.add(MultiplierManager.getTotalMultiplier(multipliers.get()));
		if (guild != null)
			x = x.multiply(getEconomy().getServer(guild.getId()).getTotalServerMultiplier());
		return x;
	}

	/**
	 * <p>
	 * Rewards the user the specified amount. The actual amount deposited into the
	 * user's account is a product of the specified amount and this user's
	 * multipliers and possible "effects" which can increase (or decrease) the
	 * amount of money earned. This method handles a <code>null</code> value for the
	 * {@link Guild} argument as if the command was not invoked in a server.
	 * </p>
	 * <p>
	 * The total rewards earned multiplied byt
	 * 
	 * @param amount The amount to reward the user.
	 * @param guild  The {@link Guild} that the reward is to be given in. Nitro
	 *               boosts will multiply the reward of this user.
	 * @return The amount rewarded to the user.
	 */
	public BigInteger reward(BigInteger amount, Guild guild) {
		return reward(amount, calcMultiplier(guild));
	}

	@Override
	public void save() {
		MultiplierManager.cleanMults(multipliers.get());
		super.save();
	}

	/**
	 * Adds the specified amount of cloves, multiplied by the pre-calculated
	 * multiplier, to this user's account.
	 * 
	 * @param amount     The amount earned.
	 * @param multiplier The total multiplier (already calculated) to multiply the
	 *                   amount by.
	 * @return The total number of cloves rewarded (amount * multiplier).
	 */
	public BigInteger reward(BigInteger amount, BigDecimal multiplier) {
		var x = new BigDecimal(amount).multiply(multiplier).toBigInteger();
		getAccount().deposit(x);
		getAccount().addTotalEarnings(x);
		return x;
	}

	/**
	 * Adds the specified amount of rewards, multiplied by the pre-calculated
	 * multiplier, to this user's account, and then saves this user's data.
	 * 
	 * @param amount     The amount of cloves earned.
	 * @param multiplier The total multiplier (already calculated) to multiply the
	 *                   amount by.
	 * @return The total number of cloves rewarded (amount * multiplier).
	 */
	public BigInteger rewardAndSave(BigInteger amount, BigDecimal multiplier) {
		var x = reward(amount, multiplier);
		save();
		account.save();
		return x;
	}

	public Receipt reward(Rewards rewards, Guild guild) {
		var mult = calcMultiplier(guild);
		BigInteger clovesGiven = BigInteger.ZERO;
		if (rewards.hasMultipliers())
			for (var m : rewards.getMultipliers().entrySet())
				for (int i = 0; i < m.getValue(); i++)
					addMultiplier(m.getKey().reify());
		if (rewards.hasCloves())
			clovesGiven = reward(rewards.getCloves(), mult);
		if (rewards.hasItems())
			getInventory().add(rewards.getItemsAsList());

		return new Receipt(rewards, mult, clovesGiven);
	}

	public Receipt rewardAndSave(Rewards rewards, Guild guild) {
		var r = reward(rewards, guild);
		if (rewards.hasCloves())
			getAccount().save();
		if (rewards.hasItems())
			for (var i : rewards.getItemsAsList())
				try {
					getInventory().get(i.getItem()).save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
		if (rewards.hasMultipliers())
			save();
		return r;
	}

	public class Receipt {
		private final Rewards rewards;
		private final BigDecimal appliedMultiplier;
		private final BigInteger resultingCloves, totalCloves = getAccount().getBalance();

		public Receipt(Rewards rewards, BigDecimal appliedMultiplier, BigInteger resultingCloves) {
			this.rewards = rewards;
			this.appliedMultiplier = appliedMultiplier;
			this.resultingCloves = resultingCloves;
		}

		public Rewards getRewards() {
			return rewards;
		}

		public BigDecimal getAppliedMultiplier() {
			return appliedMultiplier;
		}

		public BigInteger getResultingCloves() {
			return resultingCloves;
		}

		public BigInteger getTotalCloves() {
			return totalCloves;
		}

	}

	public BigInteger rewardAndSave(long amount, BigDecimal multiplier) {
		return rewardAndSave(BigInteger.valueOf(amount), multiplier);
	}

	public BigInteger reward(long amount, BigDecimal multiplier) {
		return reward(BigInteger.valueOf(amount), multiplier);
	}

	public BigInteger reward(long amount, Guild guild) {
		return reward(BigInteger.valueOf(amount), guild);
	}

	public User(File userDirectory, Economy economy) {
		this(userDirectory, true, economy);
	}

	protected User(File userDirectory, boolean load, Economy economy) {
		super(new File(userDirectory, "user-data.txt"));
		this.economy = economy;
		userID = userDirectory.getName();
		account = new UserAccount(userDirectory, this);
		inventory = new UserInventory(userDirectory);
		accolades = new AccoladeList(new File(userDirectory, "accolades.txt"));
		if (load)
			load();
		if (getMessageCount() == null)
			setMessageCount(BigInteger.ZERO);
		if (questions.get() == null)
			questions.set(new ArrayList<>());
		if (multipliers.get() == null)
			multipliers.set(new ArrayList<>());
		if (joinedGuilds.get() == null)
			joinedGuilds.set(new ArrayList<>());
	}

	public Instant getLastDailyInvocation() {
		return dailyCommand.get();
	}

	public Instant getLastWeeklyInvocation() {
		return weeklyCommand.get();
	}

	public Instant getLastMonthlyInvocation() {
		return monthlyCommand.get();
	}

	public void dailyInvoked() {
		dailyCommand.set(Instant.now());
	}

	public void weeklyInvoked() {
		weeklyCommand.set(Instant.now());
	}

	public void monthlyInvoked() {
		monthlyCommand.set(Instant.now());
	}

	public Duration timeSinceLastDaily() {
		return Duration.between(getLastDailyInvocation(), Instant.now());
	}

	public Duration timeSinceLastWeekly() {
		return Duration.between(getLastWeeklyInvocation(), Instant.now());
	}

	public Duration timeSinceLastMonthly() {
		return Duration.between(getLastMonthlyInvocation(), Instant.now());
	}

	public BigInteger getVoteCount() {
		return voteCount.get();
	}

}
