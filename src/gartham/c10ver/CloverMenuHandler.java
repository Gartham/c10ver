package gartham.c10ver;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import gartham.c10ver.response.menus.Menu;
import gartham.c10ver.response.menus.SimplePage;
import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CloverMenuHandler implements MessageInputConsumer {
	private final Clover clover;

	public CloverMenuHandler(Clover clover) {
		this.clover = clover;
	}

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {
		BLOCK: {
			for (var v : event.getMessage().getMentionedUsers())
				if (v.getId().equals(event.getJDA().getSelfUser().getId()))
					break BLOCK;
			for (var v : event.getMessage().getMentionedMembers())
				if (v.getId().equals(event.getJDA().getSelfUser().getId()))
					break BLOCK;
			return false;
		}
		Menu menu = new Menu(clover.getEventHandler().getButtonClickProcessor());
		menu.getPaginator().setTarget(event.getAuthor());
		var p1 = new SimplePage(menu);
		p1.new MenuItem("\ud83d\uddc3\ufe0f", "Inventory", "inv",
				"See your items, balance (cloves), creatures, and everything else you own. You can open/use items, manage/upgrade your creatures and team, and see everything you own from here.");
		p1.new MenuItem("\u2694\ufe0f", "Explore", "exp",
				"You can play *dungeons* or explore the *wilderness* from here. Both will give you loot (items, cloves, multipliers, etc.) and experience to level you and your creatures up.");
		p1.new MenuItem("\uD83D\uDCCA", "Stats", "stat",
				"Check your level, rank, balance, and interesting data about your use of the bot.");
		p1.new MenuItem("\u2699", "Settings", "set",
				"Go here to configure settings (like vote reminders or loot notifications).");
		menu.getPaginator().setHandler(t -> {
			MessageActionHandler mah = menu.getPaginator().getMah();
			mah.convert(Action::disable);
			menu.getPaginator().getMsg().editMessageComponents(mah.generate()).queue();

			t.reply("The " + switch (t.getComponentId()) {
			case "inv" -> "Inventory";
			case "exp" -> "Exploration";
			case "stat" -> "Statistics";
			case "set" -> "Settings";
			default -> throw new IllegalArgumentException("Unexpected value: " + t.getComponentId());
			} + " menu has not yet been set up!").queue();

			return true;// Handled.
		});
		menu.getPaginator().setOneTime(true);
		menu.send(event.getChannel());
		return true;
	}

}
