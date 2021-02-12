package gartham.c10ver.economy.items;

import java.util.Map.Entry;
import java.util.Objects;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;

public abstract class Item extends PropertyObject {
	protected final Property<String> itemType = stringProperty("item-type").setAttribute(false),
			itemName = stringProperty("item-name").setAttribute(false), icon;

	@Override
	public void load(JSONObject properties) {
		String s = properties.getString("item-type");
		if (!getItemType().equals(s))
			throw new IllegalArgumentException("Invalid object being loaded. According to object, object is a: " + s
					+ ". This class represents: " + getItemType());
		super.load(properties);
	}

	protected final Property<String> getItemTypeProperty() {
		return itemType;
	}

	protected final Property<String> getIconProperty() {
		return icon;
	}

	public String getItemName() {
		return itemName.get();
	}

	protected final void setItemName(String name) {
		itemName.set(name);
	}

	/**
	 * Determines whether the other item can be combined with this one. More
	 * formally, this method determines whether the specified item has equal values
	 * in all {@link gartham.c10ver.data.PropertyObject.Property properties} that
	 * have their {@link gartham.c10ver.data.PropertyObject.Property#isAttribute()
	 * attribute} field set to <code>true</code>.
	 * 
	 * @param other The other {@link Item} to check stackability with.
	 * @return <code>true</code> if the {@link Item} can be stacked with this one,
	 *         <code>false</code> otherwise.
	 */
	public boolean stackable(Item other) {
		if (other == null)
			throw null;
		if (!getItemType().equals(other.getItemType()))
			return false;
		for (Entry<String, Property<?>> e : getPropertyMap().entrySet())
			if (e.getValue().isAttribute())
				if (!(other.getPropertyMap().containsKey(e.getKey())
						&& Objects.equals(e.getValue().get(), other.getPropertyMap().get(e.getKey()).get())))
					return false;
		for (Entry<String, Property<?>> e : other.getPropertyMap().entrySet())
			if (e.getValue().isAttribute() && !getPropertyMap().containsKey(e.getKey()))
				return false;
		return true;
	}

	public Item(String type, String itemName) {
		this(type, itemName, (String) null);
	}

	public Item(String type, String itemName, String defaultIcon) {
		itemType.set(type);// We don't set a default because we always want this written to file.
		this.itemName.set(itemName);// Default property means nothing because this is never written to file.
		icon = stringProperty("icon", defaultIcon).setAttribute(false);
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
