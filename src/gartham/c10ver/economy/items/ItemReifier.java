package gartham.c10ver.economy.items;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;
import gartham.c10ver.economy.items.utility.foodstuffs.Spaghetti;

public class ItemReifier {
	@SuppressWarnings("unchecked")
	public static <I extends Item> I reify(JSONObject json) {
		String type = json.getString("$type");
		if (type == null)
			throw new IllegalArgumentException("Invalid item type.");
		return (I) switch (type) {
		case LootCrateItem.ITEM_TYPE:
			yield new LootCrateItem(json);
		case Spaghetti.ITEM_TYPE:
			yield new Spaghetti(json);
		case Sandwich.ITEM_TYPE:
			yield new Sandwich(json);
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		};
	}

	public static <I extends Item> I reify(JSONValue value) {
		if (!(value instanceof JSONObject))
			throw new IllegalArgumentException("Illegal type.");
		return reify((JSONObject) value);
	}
}
