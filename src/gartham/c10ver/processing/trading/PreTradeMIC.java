package gartham.c10ver.processing.trading;

import java.time.Instant;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PreTradeMIC implements MessageInputConsumer {
	private final Trade trade;

	public PreTradeMIC(Trade trade) {
		this.trade = trade;
	}

	private Instant recipientTS = Instant.now(), requesterTS = recipientTS;

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {

		var channel = trade.getInitialChannel();

		if (!trade.isRecipient(event.getAuthor()))
			if (Instant.now().isAfter(recipientTS.plusSeconds(30))) {
				channel.sendMessage(trade.getRecip().getEcouser().getUser().getAsTag()
						+ " didn't reply within 30 seconds to the confirmation, so the trade has been cancelled.")
						.queue();
				return true;
			}

		if (!(event.getChannel().equals(channel)
				&& (trade.isRequester(event.getAuthor()) || trade.isRecipient(event.getAuthor())))) {
			return false;
		}

		return false;
	}

}
