package gartham.c10ver.economy.items.utility.crates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Rewards;
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
	public Rewards open() {
		List<ItemBunch<?>> items = new ArrayList<>();
		if (Math.random() < 0.02)
			items.add(new ItemBunch<>(switch ((int) (Math.random() * 3)) {
			case 0 -> new DailyCrate();
			case 1 -> new WeeklyCrate();
			case 2 -> new MonthlyCrate();
			default -> throw new IllegalArgumentException("Unexpected value.");
			}));
		return new Rewards(items, BigInteger.valueOf(250));
	}

}
