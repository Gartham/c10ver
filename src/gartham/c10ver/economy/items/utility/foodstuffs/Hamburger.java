package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;

public class Hamburger extends Foodstuff {
	public static final String ITEM_TYPE = "hamburger", ITEM_NAME = "Hamburger", ITEM_ICON = ":hamburger:";
	public static final long ITEM_TTL = min(5);
	public static final BigDecimal ITEM_MULT = BigDecimal.valueOf(75, 2);

	{
		setIcon(ITEM_ICON);
		setItemName(ITEM_NAME);
		setTTL(ITEM_TTL);
		setMultiplier(ITEM_MULT);
	}

	public Hamburger(JSONObject properties) {
		super(ITEM_TYPE, properties);
	}

	public Hamburger() {
		super(ITEM_TYPE);
	}

}
