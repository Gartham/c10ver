package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.users.User;

public class Account extends SavablePropertyObject {
	private final Property<BigInteger> balance = bigIntegerProperty("bal", BigInteger.ZERO);

	public Account(File userDirectory) {
		this(userDirectory, true);
	}

	protected Account(File userDirectory, boolean load) {
		super(new File(userDirectory, "main-account.txt"));
		if (load)
			load();
	}

	public void setBalance(BigInteger balance) {
		this.balance.set(balance);
	}

	public BigInteger getBalance() {
		return balance.get();
	}

	public boolean pay(BigInteger amount, Account recipient) {
		if (!withdraw(amount))
			return false;
		recipient.deposit(amount);
		return true;
	}

	public void deposit(BigInteger amt) {
		setBalance(getBalance().add(amt));
	}

	public void deposit(long amt) {
		deposit(BigInteger.valueOf(amt));
	}

	public boolean withdraw(BigInteger amt) {
		if (getBalance().compareTo(amt) < 0)
			return false;
		setBalance(getBalance().subtract(amt));
		return true;
	}

	public boolean withdraw(long amt) {
		return withdraw(BigInteger.valueOf(amt));
	}

}
