package gartham.c10ver.economy.items.utility.crates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.economy.items.ItemBunch;

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
	public RewardsOperation open() {
		return new RewardsOperation().with(BigInteger.valueOf((long) (Math.random() * 30 + 10)));
	}

}
