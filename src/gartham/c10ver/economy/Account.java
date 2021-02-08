package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.data.DataUtils;
import gartham.c10ver.data.autosave.AutosaveValue;
import gartham.c10ver.data.autosave.Saveable;

public class Account implements Saveable {
	private AutosaveValue<BigDecimal> balance;

	private final File mainAccount;

	public Account(File userDirectory) {
		mainAccount = new File(userDirectory, "main-account.txt");
		var mainAcc = DataUtils.loadObj(mainAccount);
		balance = new AutosaveValue<>(mainAcc == null ? BigDecimal.ZERO : new BigDecimal(mainAcc.getString("bal")),
				this);
	}

	public void setBalance(BigDecimal balance) {
		this.balance.setValue(balance);
	}

	public BigDecimal getBalance() {
		return balance.getValue();
	}

	public boolean pay(BigDecimal amount, Account recipient) {
		if (!withdraw(amount))
			return false;
		recipient.deposit(amount);
		return true;
	}

	public void deposit(BigDecimal amt) {
		balance.setValue(balance.getValue().add(amt));
	}

	public void deposit(long amt) {
		deposit(BigDecimal.valueOf(amt));
	}

	public boolean withdraw(BigDecimal amt) {
		if (balance.getValue().compareTo(amt) < 0)
			return false;
		balance.setValue(balance.getValue().subtract(amt));
		return true;
	}

	public boolean withdraw(long amt) {
		return withdraw(BigDecimal.valueOf(amt));
	}

	public JSONValue toJSON() {
		return new JSONObject().put("bal", balance.toString());
	}

	@Override
	public void save() {
		DataUtils.save(toJSON(), mainAccount);
	}

}
