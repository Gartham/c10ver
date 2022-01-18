package gartham.c10ver.response.menus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public abstract class Menu<M extends gartham.c10ver.response.menus.Menu<M>.Page.MenuItem> {

	private final ButtonPaginator paginator;
	private final List<Page> pages = new ArrayList<>();

	public ButtonPaginator getPaginator() {
		return paginator;
	}

	private final Class<M> menuItem;

	protected abstract Collection<MessageEmbed> process(List<M> items);

	public class Page {

		{
			if (paginator.isStarted())
				throw new IllegalStateException();
			pages.add(this);
		}

		private final List<MenuItem> items = new ArrayList<>();

		public class MenuItem extends Action {

			{
				if (paginator.isStarted())
					throw new IllegalStateException();
				items.add(this);
			}

			public MenuItem(Button button) {
				paginator.getMah().super(button);
			}

		}

	}

	protected Menu(InputProcessor<ButtonClickEvent> processor, Class<M> menuItemType) {
		paginator = new ButtonPaginator(processor);
		menuItem = menuItemType;
		paginator.setPageHandler(new BiFunction<Integer, ButtonClickEvent, Boolean>() {

			@Override
			public Boolean apply(Integer t, ButtonClickEvent u) {

				return false;
			}
		});
	}

	protected Menu(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor, Class<M> menuItemType) {
		paginator = new ButtonPaginator(mah, processor);
		menuItem = menuItemType;
	}

	@SuppressWarnings("unchecked")
	public void send(MessageChannel channel) {
		var actions = paginator.getMah().getActions();
		List<M> m = new ArrayList<>();
		for (var v : actions)
			if (menuItem.isInstance(v))
				m.add((M) v);
		MessageAction action = channel.sendMessageEmbeds(process(m));
		paginator.attachAndSend(action);
	}

}
