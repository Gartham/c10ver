package gartham.c10ver.processing.trading;

import java.time.Instant;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.SimpleCommandProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TradeMIC implements MessageInputConsumer {

	private final static SimpleCommandProcessor processor = new SimpleCommandProcessor();

	private final Trade trade;
	private Instant lastUse = Instant.now();

	public TradeMIC(Trade trade) {
		this.trade = trade;
	}

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {
		var clover = trade.getManager().getClover();
		var txt = event.getMessage().getContentRaw();
		if (event.getChannel().equals(trade.getInitialChannel()) && (event.getAuthor().equals(trade.getRecipientUser())
				|| event.getAuthor().equals(trade.getRequesterUser()))) {
			TradeMIC.processor.run(clover.getCommandParser().parse(null, txt, event));
			lastUse = Instant.now();
			return true;
		}

		if (Instant.now().isAfter(lastUse.plusSeconds(120)))
			trade.end();
		lastUse = Instant.now();
		return false;
	}

}
