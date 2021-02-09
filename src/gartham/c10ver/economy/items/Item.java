package gartham.c10ver.economy.items;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;

public abstract class Item extends PropertyObject {
	private final Property<String> ownerID = stringProperty("owner-id"), itemType = stringProperty("item-type"), icon;

	public Item() {
		this((String) null);
	}

	public Item(JSONObject properties) {
		this(properties, null);
	}

	public Item(String defaultIcon) {
		icon = defaultIcon == null ? stringProperty("icon") : stringProperty("icon", defaultIcon);
	}

	public Item(JSONObject properties, String defaultIcon) {
		super(properties);
		icon = stringProperty("icon");
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

	public String getIcon() {
		return icon.get();
	}

	protected final void setIcon(String icon) {
		this.icon.set(icon);
	}

}
