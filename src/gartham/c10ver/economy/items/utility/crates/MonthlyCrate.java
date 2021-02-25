package gartham.c10ver.economy.items.utility.crates;

import gartham.c10ver.economy.Rewards;

public class MonthlyCrate extends LootCrateItem {

	public static final String ITEM_NAME = "Monthly Crate", ITEM_ICON = "<:crate:808762616456675338>",
			CRATE_TYPE = "monthly";

	{
		setCrateType(CRATE_TYPE);
		setItemName(ITEM_NAME);
		setIcon(ITEM_ICON);
	}

	@Override
	protected Rewards open() {
		// TODO Auto-generated method stub
		return null;
	}

}
