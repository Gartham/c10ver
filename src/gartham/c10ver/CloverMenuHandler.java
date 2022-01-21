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
		p1.new MenuItem("\u2694\ufe0f", "Inventory", "inv",
				"See your Items, Balance, Creatures, and everything else you own.");
		p1.new MenuItem("\uD83E\uDD54", "Potato", "pot",
				"The all-powerful potato button. (Does nothing, powerfully. :muscle:)");
		p1.new MenuItem("\uD83C\uDFFA", "Amphora", "amph",
				"Contains the essence of really powerful creatures. No one knows how it got there.");
		var p2 = new SimplePage(menu);
		p2.new MenuItem("\uD83E\uDDC3", "Orange Juice", "oj",
				"Replenishes all of your stamina, but also has a small chance of giving you diabetes and permanently impairing your gameplay.");
		menu.getPaginator().setHandler(t -> {
			MessageActionHandler mah = menu.getPaginator().getMah();
			mah.convert(Action::disable);
			menu.getPaginator().getMsg().editMessageComponents(mah.generate()).queue();
			t.reply("G fuel.").queue();
			return true;// Handled.
		});
		menu.getPaginator().setOneTime(true);
		menu.send(event.getChannel());
		return true;
	}

}
