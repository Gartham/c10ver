package gartham.c10ver.response.menus;

import java.util.List;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.response.menus.SimpleMenu.SimpleMenuItem;
import gartham.c10ver.utils.MessageActionHandler;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class SimpleMenu extends Menu<SimpleMenuItem> {

	public SimpleMenu(InputProcessor<ButtonClickEvent> processor) {
		super(processor);
	}

	public SimpleMenu(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor) {
		super(mah, processor);
	}

	public class SimpleMenuItem extends Menu<SimpleMenuItem>.MenuItem {

		public SimpleMenuItem(Button button) {
			super(button);
		}

	}

	@Override
	protected void process(MessageAction action, List<SimpleMenuItem> items) {
		// TODO Auto-generated method stub

	}
}
