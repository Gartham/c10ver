package gartham.c10ver.transactions;

import java.util.function.Consumer;

public abstract class TransactionHandler {

	private Consumer<Transaction> transactionProcessor;

	public final TransactionHandler setTransactionProcessor(Consumer<Transaction> processor) {
		transactionProcessor = processor;
		return this;
	}

	public abstract void enable();

	protected final void handleTransaction(Transaction transaction) {
		transactionProcessor.accept(transaction);
	}

	public abstract void destroy();
}
