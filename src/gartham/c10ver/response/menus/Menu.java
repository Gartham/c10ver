package gartham.c10ver.response.menus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public abstract class Menu {

	private final ButtonPaginator paginator;
	private final List<Page<?>> pages = new ArrayList<>();

	public ButtonPaginator getPaginator() {
		return paginator;
	}

	public abstract class Page<M extends Page<M>.MenuItem> {

		{
			if (paginator.isStarted())
				throw new IllegalStateException();
			pages.add(this);
		}

		// Any MenuItem can be added to this, so subclasses should use instanceof and
		// throw UnsupportedOperationExceptions when they can't render a menu item.
		private final List<MenuItem> items = new ArrayList<>();

		public List<MenuItem> getItems() {
			return items;
		}

		private void showPageButtons() {
			for (var v : items)
				v.add();
		}

		private void hidePageButtons() {
			for (var v : items)
				v.remove();
		}

		protected abstract Collection<MessageEmbed> generateEmbeds();

		public class MenuItem extends Action {

			{
				if (paginator.isStarted())
					throw new IllegalStateException();
				items.add(this);
				remove();
			}

			public MenuItem(Button button) {
				paginator.getMah().super(button);
			}

			public MenuItem(String emoji, String label, String id, ButtonStyle style) {
				this(Button.of(style, id, label, Emoji.fromMarkdown(emoji)));
			}

		}

	}

	protected Menu(InputProcessor<ButtonClickEvent> processor) {
		this(new ButtonPaginator(processor));
	}

	protected Menu(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor) {
		this(new ButtonPaginator(mah, processor));
	}

	protected Menu(ButtonPaginator paginator) {
		this.paginator = paginator;
		paginator.setPageHandler(t -> {
			pages.get(t.getOldPage()).hidePageButtons();
			Page<?> np = pages.get(t.getNewPage());
			np.showPageButtons();
			t.getE().editComponents(getPaginator().getMah().generate()).setEmbeds(np.generateEmbeds()).complete();
			t.consume();
		});
	}

	public void attachAndSend(MessageAction msg) {
		Page<?> fp = pages.get(0);
		fp.showPageButtons();
		msg.setEmbeds(fp.generateEmbeds());
		paginator.attachAndSend(msg);
	}

}
