package gartham.c10ver.transactions;

import java.util.function.Function;

public abstract class TransactionHandler {

	private Function<Transaction, TransactionResponse> transactionProcessor;

	public final TransactionHandler setTransactionProcessor(
			Function<Transaction, TransactionResponse> transactionProcessor) {
		this.transactionProcessor = transactionProcessor;
		return this;
	}

	protected abstract void enable();

	protected final TransactionResponse handleTransaction(Transaction transaction) {
		return transactionProcessor.apply(transaction);
	}

	protected abstract void destroy();
}
