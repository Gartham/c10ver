package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class FinishedCommand extends MatchBasedCommand {

	private final Trade trade;

	public FinishedCommand(Trade trade) {
		super("finished", "done", "complete");
		this.trade = trade;
	}

	@Override
	public void exec(CommandInvocation inv) {
		var person = trade.getParticipant(inv.event.getAuthor());
		if (inv.args.length == 0)
			if (!person.isFinished()) {
				person.setFinished(true);
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
						+ " marked your side of the trade as complete. Here are the items you're planning on trading:")
						.embed(person.getTrade(new EmbedBuilder()).build()).queue();
			} else
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
						+ " you've already marked your side of the trade as " + inv.cmdName + '.').queue();
	}

}
