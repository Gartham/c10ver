package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;

public class Pizza extends Foodstuff {
	public static final String ITEM_TYPE = "pizza", ITEM_NAME = "Pizza", ITEM_ICON = ":pizza:";
	public static final long ITEM_TTL = min(10);
	public static final BigDecimal ITEM_MULT = BigDecimal.valueOf(5, 2);

	{
		setIcon(ITEM_ICON);
		setItemName(ITEM_NAME);
		setTTL(ITEM_TTL);
		setMultiplier(ITEM_MULT);
	}

	public Pizza(JSONObject properties) {
		super(ITEM_TYPE, properties);
	}

	public Pizza() {
		super(ITEM_TYPE);
	}

}
