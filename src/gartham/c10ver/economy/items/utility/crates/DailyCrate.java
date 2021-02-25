package gartham.c10ver.economy.items.utility.crates;

import org.alixia.javalibrary.json.JSONObject;

public class DailyCrate extends LootCrateItem {
	public static final String ITEM_NAME = "Daily Crate", ITEM_ICON = "<:crate:808762616456675338>";

	{
		setItemName(ITEM_NAME);
		setIcon(ITEM_ICON);
	}

	public DailyCrate() {
	}

	public DailyCrate(JSONObject props) {
		super(props);
	}

}
