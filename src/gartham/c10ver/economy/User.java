package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.util.StringGateway;

import gartham.c10ver.data.PropertyObject.Property;
import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.items.UserInventory;
import gartham.c10ver.economy.questions.Question;
import net.dv8tion.jda.api.entities.Guild;

public class User extends SavablePropertyObject {

	private final Property<Instant> dailyCommand = instantProperty("daily", Instant.MIN),
			weeklyCommand = instantProperty("weekly"), monthlyCommand = instantProperty("monthly");
	private final Property<BigInteger> messageCount = bigIntegerProperty("message-count", BigInteger.ZERO),
			totalEarnings = bigIntegerProperty("total-earnings", BigInteger.ZERO);
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

	private static boolean expired(Multiplier m) {
		return Instant.now().isAfter(m.getExpiration());
	}

	private BigDecimal checkMultipliers() {
		if (multipliers.get().isEmpty())
			return BigDecimal.ZERO;
		BigDecimal res = BigDecimal.ZERO;
		Instant now = Instant.now();
		for (Iterator<Multiplier> iterator = multipliers.get().iterator(); iterator.hasNext();) {
			Multiplier m = iterator.next();
			if (now.isAfter(m.getExpiration()))
				iterator.remove();
			else
				res = res.add(m.getAmount());
		}

		return res;
	}

	public ArrayList<Multiplier> getMultipliers() {
		checkMultipliers();
		return multipliers.get();
	}

	public BigDecimal getPersonalTotalMultiplier() {
		return BigDecimal.ONE.add(checkMultipliers());
	}

	public void addMultiplier(Multiplier m) {
		if (!expired(m))
			multipliers.get().add(m);
	}

	public BigInteger getMessageCount() {
		return messageCount.get();
	}

	public void setMessageCount(BigInteger count) {
		messageCount.set(count);
	}

	public void incrementMessageCount() {
		setMessageCount(getMessageCount().add(BigInteger.ONE));
	}

	private final Property<ArrayList<Question>> questions = listProperty("questions",
			toObjectGateway(t -> new Question((JSONObject) t)));
	{
		questions.set(new ArrayList<>());
	}

	public List<Question> getQuestions() {
		return questions.get();
	}

	private final Account account;
	private final UserInventory inventory;
	private final Economy economy;
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

	public Account getAccount() {
		return account;
	}

	public BigDecimal calcMultiplier(Guild guild) {
		var v = guild == null ? null : guild.getMember(getUser()).getTimeBoosted();
		var x = v == null ? BigDecimal.ONE
				: BigDecimal.valueOf(13, 1).add(BigDecimal.valueOf(Duration.between(v, Instant.now()).toDays() + 1)
						.multiply(BigDecimal.valueOf(1, 2)));
		x = x.add(checkMultipliers());
		return x;
	}

	/**
	 * Rewards the user the specified amount. The actual amount deposited into the
	 * user's account is a product of the specified amount and this user's
	 * multipliers and possible "effects" which can increase (or decrease) the
	 * amount of money earned. This method handles a <code>null</code> value for the
	 * {@link Guild} argument as if the command was not invoked in a server.
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
		checkMultipliers();
		super.save();
	}

	public BigInteger reward(BigInteger amount, BigDecimal multiplier) {
		var x = new BigDecimal(amount).multiply(multiplier).toBigInteger();
		getAccount().deposit(x);
		totalEarnings.set(totalEarnings.get().add(x));
		return x;
	}

	public BigInteger rewardAndSave(BigInteger amount, BigDecimal multiplier) {
		var x = reward(amount, multiplier);
		save();
		account.save();
		return x;
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
		account = new Account(userDirectory, this);
		inventory = new UserInventory(userDirectory);
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
		if (weeklyCommand.get() == null) {
			if (monthlyCommand.get() == null)
				monthlyCommand.set(Instant.now());
			weeklyCommand.set(Instant.now());
			save();
		} else if (monthlyCommand.get() == null) {
			monthlyCommand.set(Instant.now());
			save();
		}
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

}
