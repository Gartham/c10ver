package gartham.c10ver.processing.trading;

public class Trade {
	private final TradeParticipant requester, recip;
	private final TradeManager manager;
	/**
	 * Whether the recipient of the trade has accepted to trade.
	 */
	private boolean accepted;

	Trade(TradeManager manager, TradeParticipant requester, TradeParticipant recip) {
		this.manager = manager;
		this.requester = requester;
		this.recip = recip;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public TradeParticipant getRequester() {
		return requester;
	}

	public TradeParticipant getRecip() {
		return recip;
	}

	/**
	 * Removes this {@link Trade} object from its {@link TradeManager}.
	 */
	public void end() {
		manager.remove(this);
	}

}
