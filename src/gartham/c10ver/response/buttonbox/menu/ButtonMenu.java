package gartham.c10ver.response.buttonbox.menu;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.buttonbox.ButtonBox;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class ButtonMenu extends Menu<ButtonPage> {

	public ButtonMenu(ButtonBox box, User target) {
		super(box, target);
	}

	public ButtonMenu(User target) {
		super(target);
	}

	@Override
	public boolean consume(ButtonClickEvent event, InputProcessor<? extends ButtonClickEvent> processor,
			InputConsumer<ButtonClickEvent> consumer) {
		if (event.getUser().getIdLong() == getTarget().getIdLong()
				&& event.getMessageIdLong() == getMessage().getIdLong()) {
			String compid = event.getComponentId();
			var bl = getCurrentPage();
			if (bl.containsComponent(compid)) {
				bl.handle(event);
				return true;
			}
		}
		return super.consume(event, processor, consumer);
	}

}
