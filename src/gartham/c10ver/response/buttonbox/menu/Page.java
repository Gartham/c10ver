package gartham.c10ver.response.buttonbox.menu;

import gartham.c10ver.response.buttonbox.ButtonBox;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface Page {
	/**
	 * <p>
	 * This function is tasked with displaying this {@link Page} on the message from
	 * which the provided {@link ButtonClickEvent} originated. An example definition
	 * of this function is as follows.
	 * </p>
	 * 
	 * <pre>
	 * <code>event.editMessage("New message content").setActionRows(buttonbox.rows()).queue();</code>
	 * </pre>
	 * 
	 * <p>
	 * Whenever a pagination button in a {@link Menu} is pressed by the intended
	 * recipient, the {@link Menu#getPage() new page index} is calculated and the
	 * page buttons are updated or disabled accordingly, but, since
	 * {@link ButtonClickEvent}s can only be responded to once, the message is not
	 * automatically updated by the {@link Menu} API. It is up to the {@link Page}
	 * implementation to update the {@link Page} and respond to the button click all
	 * in one signal using the provided {@link ButtonClickEvent}.
	 * </p>
	 * 
	 * @param event     The event that sourced the pagination to this page.
	 * @param buttonbox The button box that has been updated to reflect the fact
	 *                  that the {@link Menu} is now on this page.
	 * @param menu      The {@link Menu} that this {@link Page} belongs to.
	 */
	void update(ButtonClickEvent event, ButtonBox buttonbox, Menu menu);
}
