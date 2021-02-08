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

	public Account(File f) {
		mainAccount = new File(f, "main-account.txt");
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

	public void pay(BigDecimal amount, Account recipient) {
		// TODO Code.

	}

	public void pay(BigDecimal amount) throws RuntimeException {
		balance.setValue(balance.getValue().add(amount));
	}

	public void pay(long amount) {
		pay(BigDecimal.valueOf(amount));
	}

	public JSONValue toJSON() {
		return new JSONObject().put("bal", balance.toString());
	}

	@Override
	public void save() {
		DataUtils.save(toJSON(), mainAccount);
	}

}
