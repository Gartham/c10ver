package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;

public class Spaghetti extends Foodstuff {

	// Every item needs a name, type, and icon.
	public static final String ITEM_TYPE = "spaghetti", ITEM_NAME = "Spaghetti", ITEM_ICON = "\uD83C\uDF5D";

	public static final BigDecimal ITEM_MULT = BigDecimal.valueOf(5, 1);// A value of 5, shifted to the right 1 time.

	public static final long ITEM_TTL = min(20);// 5 minutes long. See the `min` convenience function
														// documentation.

	{
		// Set the properties of the object we're creating. After we run this code,
		// running "getIcon()" for example will return ":spaghetti:"
		setIcon(ITEM_ICON);
		setItemName(ITEM_NAME);
		setMultiplier(ITEM_MULT);
		setTTL(ITEM_TTL);
	}

	public Spaghetti() {// Normal constructor that we use in our commands.
		super(ITEM_TYPE);
	}

	public Spaghetti(JSONObject properties) {// Constructor used to load things from file.
		super(ITEM_TYPE, properties);
	}

}
