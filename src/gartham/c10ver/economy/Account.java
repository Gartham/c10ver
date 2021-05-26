package gartham.c10ver.economy;

import java.io.File;
import java.math.BigInteger;

import gartham.c10ver.data.autosave.SavablePropertyObject;

public class Account extends SavablePropertyObject {
	private final Property<BigInteger> balance = bigIntegerProperty("bal", BigInteger.ZERO);

	public Account(File dir) {
		this(dir, true);
	}

	protected Account(File dir, boolean load) {
		super(dir);
		if (load)
			load();
	}

	public void setBalance(BigInteger balance) {
		this.balance.set(balance);
	}

	public BigInteger getBalance() {
		return balance.get();
	}

	public boolean pay(BigInteger amount, UserAccount recipient) {
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

	/**
	 * Takes the specified amount of money out of this {@link UserAccount} and
	 * returns <code>true</code>, or returns <code>false</code>.
	 * 
	 * @param amt The amount to attempt to withdraw.
	 * @return Whether or not the withdrawal was a success. Withdrawal will be
	 *         successful only if this {@link UserAccount} has as much or more money
	 *         than the specified amount.
	 */
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
