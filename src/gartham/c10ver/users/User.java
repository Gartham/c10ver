package gartham.c10ver.users;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.autosave.AutosaveValue;
import gartham.c10ver.data.autosave.Saveable;
import gartham.c10ver.economy.Account;
import gartham.c10ver.utils.DataUtils;

public class User implements Saveable {

	private final AutosaveValue<Instant> dailyCommand, weeklyCommand, monthlyCommand;
	private final Account account;

	public Account getAccount() {
		return account;
	}

	private final File userData;

	public User(File userDirectory) {
		userData = new File(userDirectory, "user-data.txt");
		account = new Account(userDirectory);

		var userDat = DataUtils.loadObj(userData);
		if (userDat == null) {
			dailyCommand = new AutosaveValue<>(Instant.MIN, this);
			weeklyCommand = new AutosaveValue<>(Instant.MIN, this);
			monthlyCommand = new AutosaveValue<>(Instant.MIN, this);
		} else {
			dailyCommand = new AutosaveValue<>(Instant.parse(userDat.getString("daily")), this);
			weeklyCommand = new AutosaveValue<>(Instant.parse(userDat.getString("weekly")), this);
			monthlyCommand = new AutosaveValue<>(Instant.parse(userDat.getString("monthly")), this);
		}

	}

	public Instant getLastDailyInvocation() {
		return dailyCommand.getValue();
	}

	public Instant getLastWeeklyInvocation() {
		return weeklyCommand.getValue();
	}

	public Instant getLastMonthlyInvocation() {
		return monthlyCommand.getValue();
	}

	public void dailyInvoked() {
		dailyCommand.setValue(Instant.now());
	}

	public void weeklyInvoked() {
		weeklyCommand.setValue(Instant.now());
	}

	public void monthlyInvoked() {
		monthlyCommand.setValue(Instant.now());
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

	public JSONObject toJSON() {
		var dtf = DateTimeFormatter.ISO_INSTANT;
		return new JSONObject().put("daily", dtf.format(dailyCommand.getValue()))
				.put("weekly", dtf.format(weeklyCommand.getValue()))
				.put("monthly", dtf.format(monthlyCommand.getValue()));
	}

	@Override
	public void save() {
		DataUtils.save(toJSON(), userData);
	}

}
