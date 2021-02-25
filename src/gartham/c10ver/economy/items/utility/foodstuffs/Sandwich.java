package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;

public class Sandwich extends Foodstuff {

	public static final String ITEM_TYPE = "sandwich", ITEM_NAME = "Sandwich", ITEM_ICON = ":sandwich:";
	public static final BigDecimal ITEM_MULT = BigDecimal.valueOf(25, 2);// 0.25 (25, shifted to the right twice.)
	public static final long ITEM_TTL = sec(150);// 2.5min

	{
		setItemName(ITEM_NAME);
		setIcon(ITEM_ICON);
		setMultiplier(ITEM_MULT);
		setTTL(ITEM_TTL);
	}

	public Sandwich(JSONObject properties) {
		super(ITEM_TYPE, properties);
	}

	public Sandwich() {
		super(ITEM_TYPE);
	}

}
