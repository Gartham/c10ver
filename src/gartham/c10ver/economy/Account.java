package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.data.DataUtils;

public class Account {
	private BigDecimal balance;

	private final File mainAccount;

	public Account(File f) {
		mainAccount = new File(f, "main-account.txt");
		var mainAcc = DataUtils.loadObj(mainAccount);
		balance = mainAcc == null ? BigDecimal.ZERO : new BigDecimal(mainAcc.getString("bal"));
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void pay(BigDecimal amount, Account recipient) {
		// TODO Code.

	}

	public void pay(BigDecimal amount) throws RuntimeException {
		balance = balance.add(amount);
		DataUtils.save(toJSON(), mainAccount);
	}

	public void pay(long amount) {
		pay(BigDecimal.valueOf(amount));
	}

	public JSONValue toJSON() {
		return new JSONObject().put("bal", balance.toString());
	}

}
