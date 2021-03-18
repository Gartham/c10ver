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
		trade.getInitialChannel().sendMessage(trade.getRecipientUser().getAsMention() + ", "
				+ trade.getRequesterUser().getAsMention()
				+ " has requested to trade with you. Type **accept** to start the trade or type **reject** to reject the request. "
				+ trade.getRequesterMember().getEffectiveName() + " can also cancel the request with **cancel**.")
				.queue();
	}

	private Instant recipientTS = Instant.now();

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {

		var channel = trade.getInitialChannel();

		if (!trade.isRecipient(event.getAuthor()))
			if (Instant.now().isAfter(recipientTS.plusSeconds(30))) {
				channel.sendMessage(trade.getRecip().getEcouser().getUser().getAsTag()
						+ " didn't reply within 30 seconds to the confirmation, so the trade has been cancelled.")
						.queue();
				trade.end();
				return false;
			}

		if (!(event.getChannel().equals(channel)
				&& (trade.isRequester(event.getAuthor()) || trade.isRecipient(event.getAuthor()))))
			return false;

		recipientTS = Instant.now();

		var c = event.getMessage().getContentRaw();
		if (trade.isRecipient(event.getAuthor())) {
			if (c.equalsIgnoreCase("accept")) {
				event.getChannel()
						.sendMessage(trade.getRecipientUser().getAsMention() + " you have now started a trade with "
								+ trade.getRequesterUser().getAsMention()
								+ ".\n\nYou are both in trade mode. You can type `+item-name amount`")
						.queue();
				trade.accept();
				return true;
			} else if (c.equalsIgnoreCase("reject")) {
				event.getChannel().sendMessage(trade.getRequesterUser().getAsMention() + ' '
						+ event.getMember().getEffectiveName() + " did not want to trade with you.").queue();
				trade.end();
				return true;
			}
		} else if (trade.isRequester(event.getAuthor())) {
			if (c.equalsIgnoreCase("cancel")) {
				event.getChannel().sendMessage(trade.getRequesterUser().getAsMention()
						+ " has cancelled the trade request to " + trade.getRecipientMember());
				trade.end();
				return true;
			}
		}
		return false;
	}

}
