package gartham.c10ver.economy.items.utility;

import gartham.c10ver.economy.users.EconomyUser;

public interface Consumable {
	/**
	 * Consumes this {@link Consumable} as the provided {@link EconomyUser}.
	 * 
	 * @param user The user that will consume this item.
	 */
	void consume(EconomyUser user);
}
