package gartham.c10ver.economy.items.utility.crates;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.Rewards;
import gartham.c10ver.economy.items.Item;

public abstract class LootCrateItem extends Item {

	public static final String ITEM_TYPE = "loot-crate";
	private final Property<String> crateType = stringProperty("type").setTransient(true).setAttribute(false);

	protected abstract Rewards open();

	protected final void setCrateType(String type) {
		crateType.set(type);
	}

	public String getCrateType() {
		return crateType.get();
	}

	public LootCrateItem(JSONObject props) {
		super(ITEM_TYPE, props);// Item class does a check on item property with JSON input.
		load(crateType, props);
	}

	public LootCrateItem() {
		super(ITEM_TYPE);
	}

	public static LootCrateItem decipher(JSONObject obj) {
		String crateType = obj.getString("type");
		if (crateType == null)
			throw new IllegalArgumentException();
		return switch (crateType) {
		case DailyCrate.ITEM_TYPE -> new DailyCrate(obj);
		case WeeklyCrate.CRATE_TYPE -> new WeeklyCrate(obj);
		case MonthlyCrate.CRATE_TYPE -> new MonthlyCrate(obj);
		default -> throw new IllegalArgumentException("Unexpected value: " + crateType);
		};
	}

}
