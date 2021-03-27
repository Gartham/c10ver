package gartham.c10ver.economy.items.utility.crates;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.Item;

public abstract class LootCrateItem extends Item {

	public static final String ITEM_TYPE = "loot-crate", ITEM_NAME = "Loot Crate";// Values
	private static final String CRATE_TYPE_PK = "type";// Key

	{
		stringProperty(CRATE_TYPE_PK);
		setItemName(ITEM_NAME);
	}

	public abstract Rewards open();

	private Property<String> crateTypeProperty() {
		return getProperty(CRATE_TYPE_PK);
	}

	protected final void setCrateType(String type) {
		crateTypeProperty().set(type);
	}

	public String getCrateType() {
		return crateTypeProperty().get();
	}

	public LootCrateItem(JSONObject props) {
		super(ITEM_TYPE, props);// Item class does a check on item property with JSON input.
		load(crateTypeProperty(), props);
	}

	public LootCrateItem() {
		super(ITEM_TYPE);
	}

	public static LootCrateItem decipher(JSONObject obj) {
		String crateType = obj.getString("type");
		if (crateType == null)
			throw new IllegalArgumentException();
		return switch (crateType) {
		case DailyCrate.CRATE_TYPE -> new DailyCrate(obj);
		case WeeklyCrate.CRATE_TYPE -> new WeeklyCrate(obj);
		case MonthlyCrate.CRATE_TYPE -> new MonthlyCrate(obj);
		default -> throw new IllegalArgumentException("Unexpected value: " + crateType);
		};
	}

}
