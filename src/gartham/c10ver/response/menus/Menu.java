package gartham.c10ver.response.menus;

import java.util.List;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public abstract class Menu<M extends gartham.c10ver.response.menus.Menu<M>.MenuItem> extends ButtonPaginator {

	protected abstract void process(MessageAction action, List<M> items);

	public class MenuItem extends Action {

		public MenuItem(Button button) {
			getMah().super(button);
		}

	}

	public Menu(InputProcessor<ButtonClickEvent> processor) {
		super(processor);
	}

	public Menu(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor) {
		super(mah, processor);
	}

}
