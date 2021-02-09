package gartham.c10ver.economy.items;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.autosave.JSONType;

public abstract class Item extends JSONType {
	private final Property<String> ownerID = stringProperty("owner-id"), itemType = stringProperty("item-type");

	public Item() {
	}

	public Item(JSONObject properties) {
		super(properties);
	}

	public void setOwnerID(String value) {
		ownerID.set(value);
	}

	public String getOwnerID() {
		return ownerID.get();
	}

	public String getItemType() {
		return itemType.get();
	}

}
