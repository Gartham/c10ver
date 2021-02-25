package gartham.c10ver.economy.items.utility.crates;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Hamburger;
import gartham.c10ver.economy.items.utility.foodstuffs.Pizza;
import gartham.c10ver.economy.items.utility.foodstuffs.Spaghetti;

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
	protected Rewards open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		items.add(new ItemBunch<>(new Spaghetti(), BigInteger.valueOf((long) (Math.random() * 3 + 6))));
		double rand = Math.random();
		if (rand > 1d / 18)
			items.add(new ItemBunch<>(new Hamburger(), BigInteger.valueOf((long) (rand * 18 + 1))));
		if (Math.random() > 0.2)
			items.add(new ItemBunch<>(new Pizza(), BigInteger.valueOf((long) (Math.random() * 5 + 8))));
		items.add(new ItemBunch<>(new DailyCrate(), BigInteger.valueOf((long) (3 + Math.random() * 8))));
		if (Math.random() > 0.6)
			items.add(new ItemBunch<>(new WeeklyCrate(),
					Math.random() > 0.6 ? BigInteger.valueOf((long) (Math.random() * 3 + 1)) : BigInteger.ONE));
		List<Multiplier> mults = new ArrayList<>();
		if (Math.random() > 0.3)
			mults.add(Multiplier.ofMin(90, BigDecimal.valueOf(2, 1)));
		if (Math.random() > 0.6)
			mults.add(Multiplier.ofHr(1, BigDecimal.valueOf(35, 2)));
		if (Math.random() > 0.9)
			mults.add(Multiplier.ofMin(10, BigDecimal.valueOf(1)));
		return new Rewards(items, BigInteger.valueOf((long) (Math.random() * 80000 + 100000)), mults);
	}

}
