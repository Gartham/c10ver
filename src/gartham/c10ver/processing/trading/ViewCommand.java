package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class ViewCommand extends MatchBasedCommand {

	private final Trade trade;

	public ViewCommand(Trade trade) {
		super("view");
		this.trade = trade;
	}

	@Override
	public void exec(CommandInvocation inv) {
		inv.event.getChannel().sendMessage("Here are the items you are willing to offer: ")
				.embed((trade.isRecipient(inv.event.getAuthor()) ? trade.getRecip() : trade.getRequester())
						.getTrade(new EmbedBuilder()).build())
				.queue();
	}

}
