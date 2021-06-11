package gartham.c10ver.economy.items.utility;

import gartham.c10ver.economy.Rewards;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Consumable {
	/**
	 * Calculates and returns the rewards from consuming this item.
	 * 
	 * @param event The event that the item was consumed in.
	 * @return The {@link Rewards}.
	 */
	Rewards consume(MessageReceivedEvent event);

	/**
	 * Returns the message to be displayed when this item is consumed. This is
	 * typically something similar to <code>"Steve#0314 is opening a crate!"</code>
	 * or <code>"Joe#8484 just ate 12 pizzas!</code>.
	 * 
	 * @param event The event that the item was consumed in.
	 * @return The message.
	 */
	String message(MessageReceivedEvent event);
}
