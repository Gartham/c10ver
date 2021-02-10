package gartham.c10ver.economy.items;

import org.alixia.javalibrary.json.JSONObject;

public class LootCrateItem extends Item {

	public static final String ITEM_TYPE = "loot-crate", ITEM_NAME = "Loot Crate";
	private final Property<CrateType> type = enumProperty("type", CrateType.DAILY, CrateType.class);
	{
		type.register(a -> icon.set(a.icon));
	}

	public CrateType getType() {
		return type.get();
	}

	public enum CrateType {
		DAILY("<:crate:808762616456675338>"), WEEKLY("<:crate:808762616456675338>"),
		MONTHLY("<:crate:808762616456675338>");

		private final String icon;

		private CrateType(String icon) {
			this.icon = icon;
		}

	}

	public LootCrateItem(CrateType type) {
		super(ITEM_TYPE, ITEM_NAME);
		this.type.set(type);
		setIcon(type.icon);
	}

	public LootCrateItem(JSONObject obj) {
		super(ITEM_TYPE, ITEM_NAME, obj);
	}
}
