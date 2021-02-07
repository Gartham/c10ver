package gartham.c10ver.economy;

import java.math.BigDecimal;

public class Account {
	private BigDecimal balance = new BigDecimal(0);

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void pay(BigDecimal amount, Account recipient) {
		// TODO Code.

	}

	public void pay(BigDecimal amount) {
		balance = balance.add(amount);
	}

	public void pay(long amount) {
		pay(BigDecimal.valueOf(amount));
	}

}
