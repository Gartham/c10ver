package gartham.c10ver.economy.items.utility.crates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.AbstractMultiplier;
import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Hamburger;
import gartham.c10ver.economy.items.utility.foodstuffs.Pizza;

public class MonthlyCrate extends LootCrateItem {

	public static final String ITEM_NAME = "Monthly Crate", ITEM_ICON = "<:crate:808762616456675338>",
			CRATE_TYPE = "monthly";

	public MonthlyCrate() {
	}

	public MonthlyCrate(JSONObject props) {
		super(props);
	}

	{
		setCrateType(CRATE_TYPE);
		setCustomName(ITEM_NAME);
		setIcon(ITEM_ICON);
	}

	@Override
	public Rewards open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		double rand = Math.random();
		items.add(new ItemBunch<>(new Hamburger(), BigInteger.valueOf((long) (rand * 3 + 1))));
		if (Math.random() < 0.2)
			items.add(new ItemBunch<>(new Pizza()));
		if (Math.random() < 0.3)
			items.add(new ItemBunch<>(new DailyCrate(), BigInteger.valueOf((long) (Math.random() * 3 + 1))));
		if (Math.random() < 0.4)
			items.add(new ItemBunch<>(new MonthlyCrate()));
		List<AbstractMultiplier> mults = new ArrayList<>();
		return new Rewards(items, BigInteger.valueOf((long) (Math.random() * 350 + 151)), mults);
	}

}
