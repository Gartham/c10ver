package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.economy.User;
import gartham.c10ver.economy.items.UserInventory;
import gartham.c10ver.economy.items.utils.ItemList;
import gartham.c10ver.economy.items.utils.ItemList.Entry;
import net.dv8tion.jda.api.EmbedBuilder;

public class AcceptCommand extends MatchBasedCommand {

	private final Trade trade;

	public AcceptCommand(Trade trade) {
		super("accept", "finished", "done", "complete");
		this.trade = trade;
	}

	@Override
	public void exec(CommandInvocation inv) {// TODO Synchronize
		var person = trade.getParticipant(inv.event.getAuthor());
		if (inv.args.length == 0)
			if (!person.isFinished()) {
				person.setFinished(true);
				var other = trade.isRecipient(inv.event.getAuthor()) ? trade.getRequester() : trade.getRecip();
				if (other.isFinished()) {

					// Do the actual tradeoff.

					ItemList invokerList = person.getItems();
					UserInventory i = person.getEcouser().getInventory();
					for (var e : invokerList.getItems()) {// For each item specified to trade, pull it out of the
															// inventory.
						var entry = i.get(e.getItem().getItemType());
						if (entry == null) {
							inv.event.getChannel()
									.sendMessage("The trade could not be completed as requested because of ");// TODO
						}
					}
				} else
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " you've accepted the trade. Now the person you're trading with needs to accept. If they make any changes, you'll have to accept again before the trade can take place.\n\n By the way, here are the items you're planning on trading:")
							.embed(person.getTrade(new EmbedBuilder()).build()).queue();
			} else
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
						+ " you've already accepted, you need to wait on the person you're trading with to accept.")
						.queue();
	}

	private static void transfer(ItemList items, User from, User to) {
//		Inventory snapshot = from.getInventory().clone
	}

}
