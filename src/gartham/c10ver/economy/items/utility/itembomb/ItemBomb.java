package gartham.c10ver.economy.items.utility.itembomb;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.utility.Consumable;

public abstract class ItemBomb extends Item implements Consumable {
	private static final String ITEM_TYPE = "item-bomb";

	public ItemBomb(JSONObject properties) {
		super(ITEM_TYPE, properties);
		// TODO Auto-generated constructor stub
	}

	public ItemBomb(String name, String icon) {
		super(ITEM_TYPE, name, icon);
		// TODO Auto-generated constructor stub
	}

	public ItemBomb() {
		super(ITEM_TYPE);
		// TODO Auto-generated constructor stub
	}
}
