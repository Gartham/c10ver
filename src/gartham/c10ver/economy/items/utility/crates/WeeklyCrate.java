package gartham.c10ver.economy.items.utility.crates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Pizza;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;
import gartham.c10ver.economy.items.utility.foodstuffs.Spaghetti;

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
	protected Rewards open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		items.add(new ItemBunch<>(new Sandwich(), BigInteger.valueOf((long) (Math.random() * 4 + 2))));
		if (Math.random() > 0.8)
			items.add(new ItemBunch<>(new DailyCrate(), BigInteger.valueOf(10)));
		if (Math.random() > 0.4)
			items.add(new ItemBunch<>(new Pizza(), BigInteger.valueOf((long) (Math.random() * 3 + 1))));
		if (Math.random() > 0.6)
			items.add(new ItemBunch<>(new Spaghetti(), BigInteger.ONE));
		if (Math.random() > 0.92)
			items.add(new ItemBunch<>(new MonthlyCrate(), BigInteger.ONE));
		double random = Math.random();
		return new Rewards(items,
				BigInteger.valueOf((long) (7000 + Math.random() * 4000 + random * random * random * random * 10000)));
	}

}
