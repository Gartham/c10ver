package gartham.c10ver.economy.items.utility.foodstuffs;

import org.alixia.javalibrary.json.JSONObject;

public class Sandwhich extends Foodstuff{

	
	public static final String ITEM_TYPE ="sandwhich", ITEM_NAME = "Sandwhich", ITEM_ICON = ":sandwhich:";
	
	public Sandwhich(JSONObject properties) {
		super(ITEM_TYPE, properties);
		setItemName(ITEM_NAME);
		setIcon(ITEM_ICON);
	}

	public Sandwhich() {
		super(ITEM_TYPE, ITEM_NAME, ITEM_ICON);

	}

}
