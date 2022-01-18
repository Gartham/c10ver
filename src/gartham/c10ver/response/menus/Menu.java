package gartham.c10ver.response.menus;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public abstract class Menu<M extends gartham.c10ver.response.menus.Menu<M>.MenuItem> extends ButtonPaginator {

	private final Class<M> menuItem;

	protected abstract void process(MessageAction action, List<M> items);

	public class MenuItem extends Action {

		public MenuItem(Button button) {
			getMah().super(button);
		}

	}

	protected Menu(InputProcessor<ButtonClickEvent> processor, Class<M> menuItemType) {
		super(processor);
		menuItem = menuItemType;
	}

	protected Menu(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor, Class<M> menuItemType) {
		super(mah, processor);
		menuItem = menuItemType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void attachAndSend(MessageAction msg) {
		var actions = getMah().getActions();
		List<M> m = new ArrayList<>();
		for (var v : actions)
			if (menuItem.isInstance(v))
				m.add((M) v);
		process(msg, m);
		super.attachAndSend(msg);
	}

}
