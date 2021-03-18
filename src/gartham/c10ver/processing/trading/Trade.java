package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Trade implements MessageInputConsumer {
	private final TradeParticipant requester, recip;
	private final TradeManager manager;
	private final MessageChannel initialChannel;

	/**
	 * Whether the recipient of the trade has accepted to trade.
	 */
	private boolean accepted;

	public Trade(TradeParticipant requester, TradeParticipant recip, TradeManager manager,
			MessageChannel initialChannel) {
		this.requester = requester;
		this.recip = recip;
		this.manager = manager;
		this.initialChannel = initialChannel;
	}

	public TradeManager getManager() {
		return manager;
	}

	public MessageChannel getInitialChannel() {
		return initialChannel;
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

	public boolean isRecipient(User user) {
		return getRecip().getEcouser().getUser().equals(user);
	}

	public boolean isRequester(User user) {
		return getRequester().getEcouser().getUser().equals(user);
	}

	/**
	 * Terminates this trade object, removing it from its {@link TradeManager} and
	 * from the message processor that it is registered to.
	 */
	public void end() {
		manager.remove(this);
	}

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {
		return isAccepted() ? handleTradeQuery(event, processor, consumer)
				: handlePreTradeQuery(event, processor, consumer);
	}

	private boolean handlePreTradeQuery(MessageReceivedEvent event,
			InputProcessor<? extends MessageReceivedEvent> processor, InputConsumer<MessageReceivedEvent> consumer) {
		return false;
	}

	private boolean handleTradeQuery(MessageReceivedEvent event,
			InputProcessor<? extends MessageReceivedEvent> processor, InputConsumer<MessageReceivedEvent> consumer) {
		// TODO Auto-generated method stub
		return false;
	}

}
