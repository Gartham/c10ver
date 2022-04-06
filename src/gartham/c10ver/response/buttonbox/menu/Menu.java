package gartham.c10ver.response.buttonbox.menu;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.response.buttonbox.ButtonBox;
import gartham.c10ver.response.buttonbox.ButtonBox.Button;
import gartham.c10ver.response.buttonbox.pagination.PaginationEvent;
import gartham.c10ver.response.buttonbox.pagination.Paginator;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Menu extends Paginator {

	private final List<Page> pages = new ArrayList<>();

	public void addPage(Page page, int position) {
		pages.add(position, page);
	}

	public void addPage(Page page) {
		pages.add(page);
	}

	public void removePage(int position) {
		pages.remove(position);
	}

	public Page getPage(int position) {
		return pages.get(position);
	}

	public void clear() {
		pages.clear();
	}

	@Override
	protected int getMaxPage() {
		return getMaxPage() - 1;
	}

	private final User target;

	public User getTarget() {
		return target;
	}

	public Menu(ButtonBox box, User target) {
		super(box);
		this.target = target;
	}

	public Menu(User target) {
		this(new ButtonBox(), target);
	}

	@Override
	protected void handle(PaginationEvent event) {
		if (event.getSource().getUser().getIdLong() != target.getIdLong()) {
			event.getSource().reply("That's not for you.").queue();
			event.consume();
		}
	}

	@Override
	protected void update(ButtonClickEvent event) {
		pages.get(getPage()).update(event, getBox(), this);
	}

	/**
	 * <p>
	 * Attaches this {@link Menu} to the specified {@link Message}. Invoking this
	 * method will edit the specified {@link Message} so that it contains
	 * {@link Button}s (according to the page this {@link Menu}) and will register
	 * this {@link Menu} to the provided {@link InputProcessor} for
	 * {@link ButtonClickEvent}s so that this {@link Menu} can respond to user
	 * input.
	 * </p>
	 * <p>
	 * The {@link #getBox() button box} used by this {@link Menu} is used to
	 * generate {@link Button}s to place on the provided {@link Message}.
	 * </p>
	 * 
	 * @param message   The message to attach to. This {@link Message} must have
	 *                  been sent by the bot.
	 * @param processor The processor to register to to listen for button presses.
	 */
	public void attach(Message message, InputProcessor<ButtonClickEvent> processor) {
		if (getMessage() != null)
			throw new RuntimeException("This Menu is already registered to a Message.");
		if (processor == null)
			throw null;
		setMessage(message);
		processor.registerInputConsumer(this);
		message.editMessageComponents(getBox().rows()).queue();
	}

	public void send(MessageAction message, InputProcessor<ButtonClickEvent> processor) {
		if (getMessage() != null)
			throw new RuntimeException("This Menu is already registered to a Message.");
		if (processor == null)
			throw null;
		processor.registerInputConsumer(this);
		message.setActionRows(getBox().rows()).queue(a -> setMessage(a));
	}

	public void detach(InputProcessor<ButtonClickEvent> processor) {
		processor.removeInputConsumer(this);
		setMessage(null);
	}

}
