package gartham.c10ver.economy.accolades;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.items.Item;

public final class Accolade extends Item {

	public static final String ITEM_TYPE = "accolade";
	private static final String ITEM_NAME_SUFFIX = " - Accolade";

	private final Property<AccoladeType> type = enumProperty("type", AccoladeType.class);

	private Accolade(JSONObject properties) {
		super(ITEM_TYPE, properties);
		load(type, properties);
		setIcon(type.get().getIcon());
		setItemName(type.get().getName() + ITEM_NAME_SUFFIX);
	}

	public Accolade(AccoladeType type) {
		super(ITEM_TYPE);
		this.type.set(type);
		setIcon(type.getIcon());
		setItemName(type.getName() + ITEM_NAME_SUFFIX);
	}

}
