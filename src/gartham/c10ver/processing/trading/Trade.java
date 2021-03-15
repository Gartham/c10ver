package gartham.c10ver.processing.trading;

public class Trade {
	private final TradeParticipant requester, recip;
	/**
	 * Whether the recipient of the trade has accepted to trade.
	 */
	private boolean accepted;

	public Trade(TradeParticipant requester, TradeParticipant recip) {
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

}
