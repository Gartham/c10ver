package gartham.c10ver.economy.items;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

public class ItemReifier {
	@SuppressWarnings("unchecked")
	public static <I extends Item> I reify(JSONObject json) {
		String type = json.getString("item-type");
		if (type == null)
			throw new IllegalArgumentException("Invalid item type.");
		return (I) switch (type) {
		case LootCrateItem.ITEM_TYPE:
			yield new LootCrateItem(json);
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
