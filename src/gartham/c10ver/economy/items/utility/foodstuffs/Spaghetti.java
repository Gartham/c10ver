package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;

public class Spaghetti extends Foodstuff {

	public static final String ITEM_TYPE = "spaghetti", ITEM_NAME = "Spaghetti", ITEM_ICON = ":spaghetti:";
	public static final BigDecimal ITEM_MULT = BigDecimal.valueOf(5, 1);

	public Spaghetti() {
		super(ITEM_TYPE, ITEM_NAME, ITEM_ICON);
		setMultiplierValue(ITEM_MULT);
	}

	public Spaghetti(JSONObject properties) {
		super(ITEM_TYPE, properties);
	}

}
