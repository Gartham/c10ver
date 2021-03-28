package gartham.c10ver.processing.trading;

import java.util.HashMap;
import java.util.Map;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.User;
import net.dv8tion.jda.api.entities.TextChannel;

public class TradeManager {
	private final Map<String, Trade> trades = new HashMap<>();
	private final Clover clover;

	public TradeManager(Clover clover) {
		this.clover = clover;
	}

	public Clover getClover() {
		return clover;
	}

	public Trade getTrade(User user) {
		return trades.get(user.getUserID());
	}

	public Trade getTrade(String user) {
		return trades.get(user);
	}

	public boolean participating(User user) {
		return getTrade(user) != null;
	}

	public boolean participating(String user) {
		return getTrade(user) != null;
	}

	/**
	 * Attempts to open a new {@link Trade} between the two users, returning
	 * <code>null</code> if a {@link Trade} already exists involving any of the two
	 * users.
	 * 
	 * @param requester The person requesting the trade.
	 * @param recipient The person receiving the trade request.
	 * @return The newly created {@link Trade} object, or <code>null</code>.
	 */
	public Trade open(User requester, User recipient, TextChannel channel) {
		if (participating(recipient) || participating(requester))
			return null;
		var t = new Trade(new TradeParticipant(requester), new TradeParticipant(recipient), this, channel);
		trades.put(requester.getUserID(), t);
		trades.put(recipient.getUserID(), t);
		return t;
	}

	void remove(Trade trade) {
		trades.remove(trade.getRequester().getEcouser().getUserID());
		trades.remove(trade.getRecip().getEcouser().getUserID());
	}

}
