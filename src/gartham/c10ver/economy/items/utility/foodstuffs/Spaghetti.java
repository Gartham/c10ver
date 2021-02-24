package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;

public class Spaghetti extends Foodstuff {

	// Every item needs a name, type, and icon.
	public static final String ITEM_TYPE = "spaghetti", ITEM_NAME = "Spaghetti", ITEM_ICON = ":spaghetti:";

	public static final BigDecimal ITEM_MULT = BigDecimal.valueOf(5, 1);// A value of 5, shifted to the right 1 time.

	public static final BigInteger ITEM_TTL = min(5);// 5 minutes long. See the `min` convenience function
														// documentation.

	{
		// Set the properties of the object we're creating. After we run this code,
		// running "getIcon()" for example will return ":spaghetti:"
		setIcon(ITEM_ICON);
		setItemName(ITEM_NAME);
		setMultiplier(ITEM_MULT);
	}

	public Spaghetti() {// Normal constructor that we use in our commands.
		super(ITEM_TYPE);
	}

	public Spaghetti(JSONObject properties) {// Constructor used to load things from file.
		super(ITEM_TYPE, properties);
	}

}
