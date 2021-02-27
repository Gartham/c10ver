package gartham.c10ver.economy.items.utility.crates;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Rewards;

public class NormalCrate extends LootCrateItem {
	public static final String ITEM_NAME = "Normal Crate", ITEM_ICON = "<:normalcrate:815039013085773825>",
			CRATE_TYPE = "normal";

	public NormalCrate() {
	}

	public NormalCrate(JSONObject props) {
		super(props);
	}

	{
		setCrateType(CRATE_TYPE);
		setIcon(ITEM_ICON);
		setCustomName(ITEM_NAME);
	}

	@Override
	public Rewards open() {
		// TODO Auto-generated method stub
		return null;
	}

}
