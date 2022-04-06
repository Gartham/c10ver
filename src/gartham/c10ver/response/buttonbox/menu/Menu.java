package gartham.c10ver.response.buttonbox.menu;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.response.buttonbox.ButtonBox;
import gartham.c10ver.response.buttonbox.pagination.PaginationEvent;
import gartham.c10ver.response.buttonbox.pagination.Paginator;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

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

}
