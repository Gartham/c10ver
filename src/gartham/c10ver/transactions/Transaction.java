package gartham.c10ver.transactions;

import java.math.BigDecimal;

public class Transaction {

	public enum Item {
		GLOBAL_MULT, SERVER_MULT
	}

	private final Item item;
	private final BigDecimal amount;

	public Transaction(Item item, BigDecimal amount) {
		this.item = item;
		this.amount = amount;
	}

}
