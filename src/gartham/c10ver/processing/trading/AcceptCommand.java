package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.UserInventory;
import gartham.c10ver.economy.items.UserInventory.UserEntry;
import gartham.c10ver.economy.items.utils.ItemList;
import gartham.c10ver.economy.users.User;
import net.dv8tion.jda.api.EmbedBuilder;

public class AcceptCommand extends MatchBasedCommand {

	private final Trade trade;

	public AcceptCommand(Trade trade) {
		super("accept", "finished", "done", "complete");
		this.trade = trade;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void exec(CommandInvocation inv) {// TODO Synchronize
		var person = trade.getParticipant(inv.event.getAuthor());
		if (inv.args.length == 0)
			if (!person.isFinished()) {
				person.setFinished(true);
				var other = trade.isRecipient(inv.event.getAuthor()) ? trade.getRequester() : trade.getRecip();
				if (other.isFinished()) {

					// Do the actual tradeoff.

					ItemList invokerList = person.getItems(), otherList = other.getItems();
					UserInventory takeInv = person.getEcouser().getInventory(),
							recivInv = other.getEcouser().getInventory();
					for (var e : invokerList.getItems()) {// For each item specified to trade, pull it out of the
															// inventory.
						var entry = takeInv.get(e.getItem().getItemType());
						if (entry == null) {
							inv.event.getChannel().sendMessage(
									"The trade could not be completed as requested because of an error. (User did not have items to trade.)")
									.queue();
							return;
						}

						if (!((UserEntry<Item>) entry).has(e.getItem(), e.getCount())) {
							inv.event.getChannel().sendMessage(
									"The trade could not be completed as requested because of an error. (User did not have enough items to complete the trade.)")
									.queue();
							return;
						}

					}

					for (var e : otherList.getItems()) {
						var entry = recivInv.get(e.getItem().getItemType());
						if (entry == null) {
							inv.event.getChannel().sendMessage(
									"The trade could not be completed as requested because of an error. (User did not have items to trade.)")
									.queue();
							return;
						}
						if (!((UserEntry<Item>) entry).has(e.getItem(), e.getCount())) {
							inv.event.getChannel().sendMessage(
									"The trade could not be completed as requested because of an error. (User did not have enough items to complete the trade.)")
									.queue();
							return;
						}
					}

					for (var e : invokerList.getItems()) {
						takeInv.remove(e.getItem(), e.getCount());
						recivInv.add(e.getItem(), e.getCount());
					}
					for (var e : otherList.getItems()) {
						recivInv.remove(e.getItem(), e.getCount());
						takeInv.add(e.getItem(), e.getCount());
					}

					trade.end();
					trade.getInitialChannel()
							.sendMessage("Trade between " + trade.getRequesterUser().getAsMention() + " and "
									+ trade.getRecipientUser().getAsMention()
									+ " complete. Check your inventories! (`~inv`)")
							.queue();
				} else
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " you've accepted the trade. Now the person you're trading with needs to accept. If they make any changes, you'll have to accept again before the trade can take place.\n\n By the way, here are the items you're planning on trading:")
							.embed(person.getTrade(new EmbedBuilder()).build()).queue();
			} else
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
						+ " you've already accepted, you need to wait on the person you're trading with to accept.")
						.queue();
	}

}
