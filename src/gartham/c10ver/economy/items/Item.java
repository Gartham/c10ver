package gartham.c10ver.economy.items;

import java.util.Map.Entry;
import java.util.Objects;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;

public abstract class Item extends PropertyObject {
	private final Property<String> itemType = stringProperty("type").setAttribute(false),
			itemName = stringProperty("name").setAttribute(false).setTransient(true),
			icon = stringProperty("icon").setAttribute(false).setTransient(true),
			customName = stringProperty("custom-name").setAttribute(false).setTransient(true);

	@Override
	public void load(JSONObject properties) {
		String s = properties.getString("item-type");
		if (!getItemType().equals(s))
			throw new IllegalArgumentException("Invalid object being loaded. According to object, object is a: " + s
					+ ". This class represents: " + getItemType());
		super.load(properties);
	}

	protected void setCustomName(String name) {
		customName.set(name);
	}

	protected final Property<String> getCustomNameProperty() {
		return customName;
	}

	public String getCustomName() {
		return customName.get();
	}

	/**
	 * The name of this item. This is typically expected to be set up on loading or
	 * instantiation by subclasses, possibly based off of properties specific to the
	 * subclass of this item, so this
	 * {@link gartham.c10ver.data.PropertyObject.Property} is
	 * <code>transient</code>.
	 * 
	 * @return The {@link gartham.c10ver.data.PropertyObject.Property} that stores
	 *         the name of this {@link Item}.
	 */
	protected final Property<String> getItemNameProperty() {
		return itemName;
	}

	/**
	 * The icon of this item. This is typically expected to be set up on loading or
	 * instantiation by subclasses, possibly based off of properties specific to the
	 * subclass of this item, so this
	 * {@link gartham.c10ver.data.PropertyObject.Property} is
	 * <code>transient</code>.
	 * 
	 * @return The {@link gartham.c10ver.data.PropertyObject.Property} that stores
	 *         the icon of this item.
	 */
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

	public Item(String type) {
		itemType.set(type);
	}

	public Item(String type, JSONObject properties) {
		load(itemType, properties);
		if (!Objects.equals(type, getItemType()))
			throw new IllegalArgumentException("Invalid item type: " + getItemType());
	}

	public Item(String type, String name, String icon) {
		itemType.set(type);
		itemName.set(name);
		this.icon.set(icon);
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
