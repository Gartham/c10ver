package gartham.c10ver.economy.items;

import java.util.Map.Entry;
import java.util.Objects;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;

public abstract class Item extends PropertyObject {
	protected final Property<String> ownerID = stringProperty("owner-id").setAttribute(false),
			itemType = stringProperty("item-type").setAttribute(false), icon;

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

	public Item(String type) {
		this(type, (String) null);
	}

	public Item(String type, JSONObject properties) {
		this(type, properties, null);
	}

	public Item(String type, String defaultIcon) {
		icon = defaultIcon == null ? stringProperty("icon") : stringProperty("icon", defaultIcon);
		itemType.load(type);
	}

	public Item(String type, JSONObject properties, String defaultIcon) {
		super(properties);
		icon = stringProperty("icon");
		if (!getItemType().equals(type))
			throw new IllegalArgumentException("Invalid object to load from. JSONObject represents a(n) "
					+ getItemType() + ", while construction is for object of type: " + type + '.');
		itemType.load(type);
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
