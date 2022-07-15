package gartham.c10ver.economy.users;

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
import gartham.c10ver.economy.Economy;
import gartham.c10ver.economy.Mailbox;
import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.MultiplierManager;
import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.economy.Server;
import gartham.c10ver.economy.items.UserInventory;
import gartham.c10ver.economy.questions.Question;
import net.dv8tion.jda.api.entities.Guild;

public class EconomyUser extends SavablePropertyObject {

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
	private final UserSettings settings;
	private final String userID;
	private final Mailbox mailbox;
	public net.dv8tion.jda.api.entities.User getUser() {
		try {
			return economy.getClover().getBot().retrieveUserById(userID).complete();
		} catch (Exception e) {
			System.out.println(userID);
			throw e;
		}
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

	public UserAccount getAccount() {
		return account;
	}

	public UserSettings getSettings() {
		return settings;
	}

	public Mailbox getMailbox() {
		return mailbox;
	}

	/**
	 * Claims all the loot in this user's {@link Mailbox}, returning the
	 * {@link Receipt} or <code>null</code> if the {@link Mailbox} was empty.
	 * 
	 * @return The {@link Receipt} from the
	 */
	public Receipt claimMailbox() {
		if (mailbox.isEmpty())
			return null;
		else {
			var rewardsop = mailbox.claim();// Take the loot out of the mailbox.
			mailbox.save();
			var receipt = reward(rewardsop);// Give it to the user.
			return receipt;// Return the receipt.
			// I don't like creating variables like this. :)
			// This entire method can be a one-liner using a conditional (:? operator)
			// expression.
		}
	}

	public Receipt claimMailboxAndSave() {
		var rec = claimMailbox();
		mailbox.save();
		return rec;
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

	public Receipt reward(RewardsOperation op) {
		if (op.hasCloves()) {
			getAccount().deposit(op.getRewardedCloves());
			getAccount().addTotalEarnings(op.getRewardedCloves());
			if (op.isShouldSave())
				getAccount().save();
		}
		if (op.hasItems()) {
			op.getItems().putInto(inventory);
			if (op.isShouldSave())
				inventory.saveAll();
		}
		if (op.hasMults()) {
			for (var m : op.getMults().entrySet())
				for (int i = 0; i < m.getValue(); i++)
					addMultiplier(m.getKey().reify());
			if (op.isShouldSave())
				save();
		}
		return new Receipt(op);
	}

	/**
	 * Cleans multipliers and saves user data encapsulated directly in the
	 * {@link EconomyUser} class.
	 */
	@Override
	public void save() {
		MultiplierManager.cleanMults(multipliers.get());
		super.save();
	}

	public class Receipt {
		private final RewardsOperation rewards;
		private final BigInteger totalCloves = getAccount().getBalance();

		public Receipt(RewardsOperation rewards) {
			this.rewards = rewards;
		}

		public RewardsOperation getRewards() {
			return rewards;
		}

		public BigInteger getTotalCloves() {
			return totalCloves;
		}
	}

	public EconomyUser(File userDirectory, Economy economy) {
		this(userDirectory, true, economy);
	}

	protected EconomyUser(File userDirectory, boolean load, Economy economy) {
		super(new File(userDirectory, "user-data.txt"));
		this.economy = economy;
		userID = userDirectory.getName();
		account = new UserAccount(userDirectory, this);
		inventory = new UserInventory(userDirectory);
		settings = new UserSettings(userDirectory, this);
		mailbox = new Mailbox(new File(userDirectory, "mailbox"), this);
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
