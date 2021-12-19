package gartham.c10ver.economy.items.utility.crates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;

public class DailyCrate extends LootCrateItem {
	public static final String ITEM_NAME = "Daily Crate", ITEM_ICON = "<:crate:808762616456675338>",
			CRATE_TYPE = "daily";

	{
		setCustomName(ITEM_NAME);
		setIcon(ITEM_ICON);
		setCrateType(CRATE_TYPE);
	}

	public DailyCrate() {
	}

	public DailyCrate(JSONObject props) {
		super(props);
	}

	@Override
	public RewardsOperation open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		if (Math.random() < 0.009)
			items.add(new ItemBunch<>(new Sandwich(), Math.random() < 0.2 ? BigInteger.TWO : BigInteger.ONE));
		if (Math.random() < 0.3)
			items.add(new ItemBunch<>(new DailyCrate()));
		else if (Math.random() < 0.007)
			items.add(new ItemBunch<>(new WeeklyCrate()));
		BigInteger cloves = BigInteger.valueOf((long) (Math.random() * 250 + 100));
		return new RewardsOperation().with(items).with(cloves);
	}

}
