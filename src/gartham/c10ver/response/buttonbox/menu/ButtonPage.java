package gartham.c10ver.response.buttonbox.menu;

import gartham.c10ver.response.buttonbox.ButtonBox.Button;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ButtonPage extends Page {

	/**
	 * Returns <code>true</code> if this {@link Page} contains a component with the
	 * specified ID.
	 * 
	 * @param buttonID The button ID of the component.
	 * @return <code>true</code> if contained, <code>false</code> otherwise.
	 */
	boolean containsComponent(String buttonID);

	/**
	 * Handles a {@link ButtonClickEvent} caused by one of the {@link Button}s
	 * tracked by this {@link ButtonPage} having been clicked.
	 * 
	 * @param event the {@link ButtonClickEvent}. This should be handled in full.
	 */
	void handle(ButtonClickEvent event);

}
