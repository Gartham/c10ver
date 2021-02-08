package gartham.c10ver.economy.items;

import java.io.File;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.util.Gateway;

import gartham.c10ver.data.autosave.AutosaveValue;
import gartham.c10ver.data.autosave.Saveable;
import gartham.c10ver.utils.DataUtils;

public abstract class Item implements Saveable {
	private final JSONObject properties = new JSONObject();

	private File saveLoc;

	public Item(File saveLoc) {
		JavaTools.requireNonNull(saveLoc);
		this.saveLoc = saveLoc;
	}

	/**
	 * Does not move the file on the hard disk.
	 * 
	 * @param saveLoc
	 */
	public void setSaveLoc(File saveLoc) {
		JavaTools.requireNonNull(saveLoc);
		this.saveLoc = saveLoc;
	}

	public File getSaveLoc() {
		return saveLoc;
	}

	private final Property<String> ownerID = stringProperty("owner-id");

	protected Property<String> stringProperty(String key) {
		return new Property<>(key, new Gateway<String, JSONValue>() {

			@Override
			public JSONValue to(String value) {
				return new JSONString(value);
			}

			@Override
			public String from(JSONValue value) {
				return ((JSONString) value).getValue();
			}
		});
	}

	protected class Property<V> implements Saveable {
		private final String key;
		private final AutosaveValue<V> value;
		private final Gateway<V, JSONValue> converter;

		public Property(String key, Gateway<V, JSONValue> converter) {
			this(key, null, converter);
		}

		public Property(String key, V value, Gateway<V, JSONValue> converter) {
			this.key = key;
			this.value = new AutosaveValue<>(value, Item.this);
			this.converter = converter;
		}

		public void set(V value) {
			this.value.setValue(value);
		}

		public V get() {
			return value.getValue();
		}

		@Override
		public void save() {
			if (value.getValue() == null)
				properties.remove(key);
			else
				properties.put(key, converter.to(value.getValue()));
			Item.this.save();
		}

	}

	@Override
	public void save() {
		DataUtils.save(properties, saveLoc);
	}

}
