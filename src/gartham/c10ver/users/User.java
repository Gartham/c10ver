package gartham.c10ver.users;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.Account;
import gartham.c10ver.economy.items.Inventory;

public class User extends SavablePropertyObject {

	private final Property<Instant> dailyCommand = instantProperty("daily", Instant.MIN),
			weeklyCommand = instantProperty("weekly", Instant.MIN),
			monthlyCommand = instantProperty("monthly", Instant.MIN);

	private final Account account;
	private final Inventory inventory;

	public String getUserID() {
		return getSaveLocation().getParentFile().getName();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Account getAccount() {
		return account;
	}

	public User(File userDirectory) {
		this(userDirectory, true);
	}

	protected User(File userDirectory, boolean load) {
		super(new File(userDirectory, "user-data.txt"));
		account = new Account(userDirectory);
		inventory = new Inventory(userDirectory);
		if (load)
			load();
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
