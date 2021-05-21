package gartham.c10ver.economy.items;

import java.util.Map.Entry;
import java.util.Objects;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;

public abstract class Item extends PropertyObject implements Cloneable {
	private static final String ITEM_TYPE_PK = "$type", ITEM_NAME_PK = "$name", CUSTOM_NAME_PK = "$custom-name",
			ITEM_ICON_PK = "$icon";

	public String userFriendlyName(String propertyKey) {
		return propertyKey;
	}

	public final String userFriendlyValue(String propertyKey) {
		return userFriendlyValue(propertyKey, getProperty(propertyKey).get());
	}

	protected String userFriendlyValue(String propertyKey, Object value) {
		return String.valueOf(value);
	}

	{
		stringProperty(ITEM_TYPE_PK).setAttribute(false);
		stringProperty(ITEM_NAME_PK).setAttribute(false).setTransient(true);
		stringProperty(ITEM_ICON_PK).setAttribute(false).setTransient(true);
		stringProperty(CUSTOM_NAME_PK).setAttribute(false).setTransient(true);
	}

	public String getEffectiveName() {
		return getCustomName() == null ? getItemName() : getCustomName();
	}

	@Override
	public void load(JSONObject properties) {
		String s = properties.getString(ITEM_TYPE_PK);
		if (!getItemType().equals(s))
			throw new IllegalArgumentException("Invalid object being loaded. According to object, object is a: " + s
					+ ". This class represents: " + getItemType());
		super.load(properties);
	}

	protected void setCustomName(String name) {
		customNameProperty().set(name);
	}

	protected final Property<String> customNameProperty() {
		return getProperty(CUSTOM_NAME_PK);
	}

	public String getCustomName() {
		return customNameProperty().get();
	}

	protected final void setItemName(String name) {
		itemNameProperty().set(name);
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
	protected final Property<String> itemNameProperty() {
		return getProperty(ITEM_NAME_PK);
	}

	public String getItemName() {
		return itemNameProperty().get();
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
	protected final Property<String> iconProperty() {
		return getProperty(ITEM_ICON_PK);
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

	private final Property<String> itemTypeProperty() {
		return getProperty(ITEM_TYPE_PK);
	}

	public Item(String type) {
		itemTypeProperty().set(type);
	}

	public Item(String type, JSONObject properties) {
		load(itemTypeProperty(), properties);
		if (!Objects.equals(type, getItemType()))
			throw new IllegalArgumentException("Invalid item type: " + getItemType());
	}

	public Item(String type, String name, String icon) {
		this(type);
		itemNameProperty().set(name);
		iconProperty().set(icon);
	}

	public String getItemType() {
		return itemTypeProperty().get();
	}

	public String getIcon() {
		return iconProperty().get();
	}

	protected final void setIcon(String icon) {
		iconProperty().set(icon);
	}

	@Override
	public Item clone() throws CloneNotSupportedException {
		return (Item) super.clone();
	}

}
