package gartham.c10ver.economy.items;

import org.alixia.javalibrary.json.JSONObject;

public class LootCrateItem extends Item {

	public static final String ITEM_TYPE = "loot-crate", ITEM_NAME = "Loot Crate";

	protected final Property<CrateType> type = enumProperty("type", CrateType.DAILY, CrateType.class);

	public CrateType getType() {
		return type.get();
	}

	public enum CrateType {
		DAILY("<:crate:808762616456675338>", "Daily"), WEEKLY("<:crate:808762616456675338>", "Weekly"),
		MONTHLY("<:crate:808762616456675338>", "Monthly");

		private final String icon, name;

		private CrateType(String icon, String name) {
			this.icon = icon;
			this.name = name;
		}

	}

	public LootCrateItem(JSONObject props) {
		super(ITEM_TYPE, props);// Item class does a check on item property with JSON input.
		load(type, props);
		getIconProperty().set(getType().icon);
		getItemNameProperty().set(ITEM_NAME);
		setCustomName(getType().name + ' ' + ITEM_NAME);
	}

	public LootCrateItem(CrateType type) {
		super(ITEM_TYPE, ITEM_NAME, type.icon);
		this.type.set(type);
		setCustomName(getType().name + ' ' + ITEM_NAME);
	}

}
