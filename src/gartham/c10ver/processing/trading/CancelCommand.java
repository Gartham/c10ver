package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;

public class CancelCommand extends MatchBasedCommand {

	private final Trade trade;

	public CancelCommand(Trade trade) {
		super("cancel");
		this.trade = trade;
	}

	@Override
	public void exec(CommandInvocation inv) {
		if (inv.args.length == 0) {
			trade.end();
			inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
					+ " ended the trade between you and "
					+ (trade.isRequester(inv.event.getAuthor()) ? trade.getRecipientUser() : trade.getRequesterUser())
							.getAsMention()
					+ '.').queue();
		}
	}

}
