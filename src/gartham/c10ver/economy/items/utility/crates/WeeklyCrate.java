package gartham.c10ver.economy.items.utility.crates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;

public class WeeklyCrate extends LootCrateItem {

	public static final String ITEM_NAME = "Weekly Crate", ITEM_ICON = "<:crate:808762616456675338>",
			CRATE_TYPE = "weekly";

	{
		setCustomName(ITEM_NAME);
		setIcon(ITEM_ICON);
		setCrateType(CRATE_TYPE);
	}

	public WeeklyCrate() {
	}

	public WeeklyCrate(JSONObject props) {
		super(props);
	}

	@Override
	public Rewards open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		items.add(new ItemBunch<>(new Sandwich(), BigInteger.valueOf((long) (Math.random() * 4 + 2))));
		if (Math.random() > 0.8)
			items.add(new ItemBunch<>(new DailyCrate(), BigInteger.valueOf((long) (2 + Math.random() * 2))));
		if (Math.random() > 0.4)
			items.add(new ItemBunch<>(new Sandwich()));
		if (Math.random() > 0.98)
			items.add(new ItemBunch<>(new MonthlyCrate(), BigInteger.ONE));
		return new Rewards(items,
				BigInteger.valueOf((long) (350 + Math.random() * 150)));
	}

}
