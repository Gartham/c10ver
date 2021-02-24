package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;

public class Spaghetti extends Foodstuff {

	public static final String ITEM_TYPE = "spaghetti", ITEM_NAME = "Spaghetti", ITEM_ICON = ":spaghetti:";
	public static final BigDecimal ITEM_MULT = BigDecimal.valueOf(5, 1);
	public static final BigInteger ITEM_TTL = min(5);

	{
		setIcon(ITEM_ICON);
		setItemName(ITEM_NAME);
		setMultiplier(ITEM_MULT);
	}

	public Spaghetti() {
		super(ITEM_TYPE);
	}

	public Spaghetti(JSONObject properties) {
		super(ITEM_TYPE, properties);
	}

}
