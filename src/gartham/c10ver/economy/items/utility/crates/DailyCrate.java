package gartham.c10ver.economy.items.utility.crates;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;

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

	@Override
	protected Rewards open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		if (Math.random() < 0.1)
			items.add(new ItemBunch<>(new Sandwich(), Math.random() < 0.2 ? BigInteger.TWO : BigInteger.ONE));
		BigInteger cloves = BigInteger.valueOf((long) (Math.random() * 650 + 300));
		return Math.random() < 0.01
				? new Rewards(items, cloves, new Multiplier(Instant.now().plusSeconds(150), BigDecimal.valueOf(25, 2)))
				: new Rewards(items, cloves);
	}

}
