package gartham.c10ver.users;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.Account;
import gartham.c10ver.economy.Economy;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.questions.Question;
import net.dv8tion.jda.api.entities.Guild;

public class User extends SavablePropertyObject {

	private final Property<Instant> dailyCommand = instantProperty("daily", Instant.MIN),
			weeklyCommand = instantProperty("weekly", Instant.MIN),
			monthlyCommand = instantProperty("monthly", Instant.MIN);

	private final Property<ArrayList<Question>> questions = listProperty("questions",
			toObjectGateway(t -> new Question((JSONObject) t)));
	{
		questions.set(new ArrayList<>());
	}

	public List<Question> getQuestions() {
		return questions.get();
	}

	private final Account account;
	private final Inventory inventory;
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

	public Inventory getInventory() {
		return inventory;
	}

	public Account getAccount() {
		return account;
	}

	public BigDecimal calcMultiplier(Guild guild) {
		var v = guild == null ? null : guild.getMember(getUser()).getTimeBoosted();
		var x = v == null ? BigDecimal.ONE
				: BigDecimal.valueOf(5, -1).add(BigDecimal.valueOf(Duration.between(v, Instant.now()).toDays() + 1)
						.multiply(BigDecimal.valueOf(1, -2)));
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

	public BigInteger reward(BigInteger amount, BigDecimal multiplier) {
		var x = new BigDecimal(amount).multiply(multiplier).toBigInteger();
		getAccount().deposit(x);
		return x;
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
		inventory = new Inventory(userDirectory, this);
		if (load)
			load();
		if (questions.get() == null)
			questions.set(new ArrayList<>());
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
