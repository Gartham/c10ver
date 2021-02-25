package gartham.c10ver.economy.items.utility.crates;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;

public class DailyCrate extends LootCrateItem {
	public static final String ITEM_NAME = "Daily Crate", ITEM_ICON = "<:crate:808762616456675338>",
			CRATE_TYPE = "daily";

	{
		setItemName(ITEM_NAME);
		setIcon(ITEM_ICON);
		setCrateType(CRATE_TYPE);
	}

	public DailyCrate() {
	}

	public DailyCrate(JSONObject props) {
		super(props);
	}

	@Override
	protected Rewards open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		if (Math.random() < 0.05)
			items.add(new ItemBunch<>(new Sandwich(), Math.random() < 0.2 ? BigInteger.TWO : BigInteger.ONE));
		if (Math.random() < 0.1)
			items.add(new ItemBunch<>(new DailyCrate(), BigInteger.valueOf(3)));
		else if (Math.random() < 0.04)
			items.add(new ItemBunch<>(new WeeklyCrate(), BigInteger.ONE));
		BigInteger cloves = BigInteger.valueOf((long) (Math.random() * 650 + 300));
		return Math.random() < 0.01 ? new Rewards(items, cloves, Multiplier.ofSec(90, BigDecimal.valueOf(25, 2)))
				: new Rewards(items, cloves);
	}

}
