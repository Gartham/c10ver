package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class FinishedCommand extends MatchBasedCommand {

	private final Trade trade;

	public FinishedCommand(Trade trade) {
		super("accept", "finished", "done", "complete");
		this.trade = trade;
	}

	@Override
	public void exec(CommandInvocation inv) {
		var person = trade.getParticipant(inv.event.getAuthor());
		if (inv.args.length == 0)
			if (!person.isFinished()) {
				person.setFinished(true);
				var other = trade.isRecipient(inv.event.getAuthor()) ? trade.getRequester() : trade.getRecip();
				if (other.isFinished()) {
					// TODO
				} else {
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " you've accepted the trade. Now the person you're trading with needs to accept. If they make any changes, you'll have to accept again before the trade can take place.\n\n By the way, here are the items you're planning on trading:")
							.embed(person.getTrade(new EmbedBuilder()).build()).queue();
				}
			} else
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
						+ " you've already accepted, you need to wait on the person you're trading with to accept.")
						.queue();
	}

}
