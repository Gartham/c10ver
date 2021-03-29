package gartham.c10ver.economy.items.utility.itembomb;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.utils.ItemList;
import net.dv8tion.jda.api.entities.Guild;

public class ItemBomb extends Item {
	public static final String ITEM_TYPE = "item-bomb";
	// TODO Consumables should take a `ConsumedEvent` which provides all the state
	// applicable when the item is consumed. Currently, just a user object is not
	// enough to fulfill the requirements of this item.

	public void consume(Guild guild, Clover clover) {
		ItemList items = new ItemList();
		for (var x : guild.loadMembers().get()) {
			int cloves = (int) (Math.random() * 200) + 50;
		}

	}

	public ItemBomb(JSONObject properties) {
		super(ITEM_TYPE, properties);
	}

	public ItemBomb(String name, String icon) {
		super(ITEM_TYPE, name, icon);
	}

	public ItemBomb() {
		super(ITEM_TYPE);
	}
}
