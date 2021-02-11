package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;

import gartham.c10ver.data.autosave.SavablePropertyObject;

public class Account extends SavablePropertyObject {
	private final Property<BigDecimal> balance = bigDecimalProperty("bal", BigDecimal.ZERO);

	public Account(File userDirectory) {
		super(new File(userDirectory, "main-account.txt"));
	}

	public void setBalance(BigDecimal balance) {
		this.balance.set(balance);
	}

	public BigDecimal getBalance() {
		return balance.get();
	}

	public boolean pay(BigDecimal amount, Account recipient) {
		if (!withdraw(amount))
			return false;
		recipient.deposit(amount);
		return true;
	}

	public void deposit(BigDecimal amt) {
		balance.set(balance.get().add(amt));
	}

	public void deposit(long amt) {
		deposit(BigDecimal.valueOf(amt));
	}

	public boolean withdraw(BigDecimal amt) {
		if (balance.get().compareTo(amt) < 0)
			return false;
		balance.set(balance.get().subtract(amt));
		return true;
	}

	public boolean withdraw(long amt) {
		return withdraw(BigDecimal.valueOf(amt));
	}

}
