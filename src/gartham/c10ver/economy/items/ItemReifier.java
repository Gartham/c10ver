package gartham.c10ver.economy.items;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.economy.items.utility.crates.LootCrateItem;
import gartham.c10ver.economy.items.utility.foodstuffs.Hamburger;
import gartham.c10ver.economy.items.utility.foodstuffs.Pizza;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;
import gartham.c10ver.economy.items.utility.foodstuffs.Spaghetti;
import gartham.c10ver.economy.items.utility.multickets.MultiplierTicket;

public class ItemReifier {
	@SuppressWarnings("unchecked")
	public static <I extends Item> I reify(JSONObject json) {
		String type = json.getString("$type");
		if (type == null)
			throw new IllegalArgumentException("Invalid item type.");
		return (I) switch (type) {
		case LootCrateItem.ITEM_TYPE -> LootCrateItem.decipher(json);
		case Spaghetti.ITEM_TYPE -> new Spaghetti(json);
		case Sandwich.ITEM_TYPE -> new Sandwich(json);
		case Hamburger.ITEM_TYPE -> new Hamburger(json);
		case Pizza.ITEM_TYPE -> new Pizza(json);
		case MultiplierTicket.ITEM_TYPE -> new MultiplierTicket(json);
		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		};
	}

	public static <I extends Item> I reify(JSONValue value) {
		if (!(value instanceof JSONObject))
			throw new IllegalArgumentException("Illegal type.");
		return reify((JSONObject) value);
	}
}
