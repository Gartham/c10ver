package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.consumers.MessageInputConsumer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Trade {
	private final TradeParticipant requester, recip;
	private final TradeManager manager;
	private final TextChannel initialChannel;

	private final MessageInputConsumer preTradeMIC = new PreTradeMIC(this), tradeMIC = new TradeMIC(this);

	/**
	 * Whether the recipient of the trade has accepted to trade.
	 */
	private boolean accepted;

	public Trade(TradeParticipant requester, TradeParticipant recip, TradeManager manager, TextChannel initialChannel) {
		this.requester = requester;
		this.recip = recip;
		this.manager = manager;
		this.initialChannel = initialChannel;
		manager.getClover().getEventHandler().getMessageProcessor().registerInputConsumer(preTradeMIC);
	}

	public TradeManager getManager() {
		return manager;
	}

	public TextChannel getInitialChannel() {
		return initialChannel;
	}

	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * Called when the {@link #recip recipient} accepts the trade.
	 */
	public void accept() {
		accepted = true;
		var processor = manager.getClover().getEventHandler().getMessageProcessor();
		processor.removeInputConsumer(preTradeMIC);
		processor.registerInputConsumer(tradeMIC);
	}

	public TradeParticipant getRequester() {
		return requester;
	}

	public TradeParticipant getRecip() {
		return recip;
	}

	public boolean isRecipient(User user) {
		return getRecip().getEcouser().getUser().equals(user);
	}

	public boolean isRequester(User user) {
		return getRequester().getEcouser().getUser().equals(user);
	}

	public User getRecipientUser() {
		return getRecip().getEcouser().getUser();
	}

	public User getRequesterUser() {
		return getRequester().getEcouser().getUser();
	}

	public Member getRecipientMember() {
		return getInitialChannel().getGuild().getMember(getRecipientUser());
	}

	public Member getRequesterMember() {
		return getInitialChannel().getGuild().getMember(getRequesterUser());
	}

	/**
	 * Terminates this trade object, removing it from its {@link TradeManager} and
	 * from the message processor that it is registered to.
	 */
	public void end() {
		manager.remove(this);
		manager.getClover().getEventHandler().getMessageProcessor()
				.removeInputConsumer(isAccepted() ? tradeMIC : preTradeMIC);
	}

}
