package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Trade implements MessageInputConsumer {
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
	 * Terminates this trade object, removing it from its {@link TradeManager} and
	 * from the message processor that it is registered to.
	 */
	public void end() {
		manager.remove(this);
	}

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {
		// TODO Auto-generated method stub
		return false;
	}

}
