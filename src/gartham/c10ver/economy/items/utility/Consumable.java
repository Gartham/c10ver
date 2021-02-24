package gartham.c10ver.economy.items.utility;

import gartham.c10ver.economy.User;

public interface Consumable {
	/**
	 * Consumes this {@link Consumable} as the provided {@link User}.
	 * 
	 * @param user The user that will consume this item.
	 */
	void consume(User user);
}
