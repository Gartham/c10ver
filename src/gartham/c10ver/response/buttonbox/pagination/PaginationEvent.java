package gartham.c10ver.response.buttonbox.pagination;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class PaginationEvent {
	private final Paginator paginator;
	private final int oldPage, newPage;
	private boolean consumed;
	private final ButtonClickEvent source;

	public ButtonClickEvent getSource() {
		return source;
	}

	public Paginator getPaginator() {
		return paginator;
	}

	public PaginationEvent(Paginator paginator, int oldPage, int newPage, ButtonClickEvent source) {
		this.paginator = paginator;
		this.oldPage = oldPage;
		this.newPage = newPage;
		this.source = source;
	}

	public int getOldPage() {
		return oldPage;
	}

	public int getNewPage() {
		return newPage;
	}

	public void consume() {
		consumed = true;
	}

	public boolean isConsumed() {
		return consumed;
	}

}
