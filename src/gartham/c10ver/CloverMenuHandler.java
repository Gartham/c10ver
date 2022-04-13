package gartham.c10ver;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import gartham.c10ver.response.buttonbox.ButtonBox;
import gartham.c10ver.response.buttonbox.menu.ButtonMenu;
import gartham.c10ver.response.buttonbox.menu.ButtonPage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

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

		ButtonMenu bm = new ButtonMenu(event.getAuthor());
		ButtonBox box = new ButtonBox();
		box.get(0, 0).setPresent(true).setLabel("Button 1").setStyle(ButtonStyle.PRIMARY).setID("1");
		box.get(0, 1).setPresent(true).setLabel("Button 2").setStyle(ButtonStyle.DANGER).setID("2");
		box.get(0, 2).setPresent(true).setLabel("Button 3").setStyle(ButtonStyle.SUCCESS).setID("3");
		var p = ButtonPage.page(a -> {
			String cid = a.getComponentId();
//			switch (cid) {
//			case "1":
//				
//				break;
//			case "2":
//
//				break;
//			default:
//				
//			}
			a.reply("You hit the right one!").queue();
			a.getMessage().delete().queue();
			bm.detach(clover.getEventHandler().getButtonClickProcessor());
		}, box, "Click one of the buttons below! If you click the wrong one, you will be banned!");

		bm.addPage(ButtonPage.page(a -> a.reply("Something something something...").queue(), new ButtonBox(),
				"Go to the next page."));
		bm.addPage(p);
		bm.send(event.getChannel().sendMessage("Go to the next page."),
				clover.getEventHandler().getButtonClickProcessor());

//		Menu<Page> menu = new Menu<>(event.getAuthor());
//		menu.addPage(Page.page("This is the 1st page."));
//		menu.addPage(Page.page("This is the second page."));
//		menu.addPage(Page.page("This is the third page on this menu."));
//		menu.send(
//				event.getChannel()
//						.sendMessage("**Initial Message** - This can be (and usually is) the same as the first page."),
//				clover.getEventHandler().getButtonClickProcessor());

		return true;
	}

}
