package gartham.c10ver.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

import gartham.c10ver.data.observe.Observable;
import gartham.c10ver.economy.items.Item;

public class PropertyObject {

	private final List<Runnable> changeListeners = new ArrayList<>(1);

	public void register(Runnable r) {
		changeListeners.add(r);
	}

	public void unregister(Runnable r) {
		changeListeners.remove(r);
	}

	private JSONObject cache;

	public void enableCache() {
		cache = toJSON();
	}

	public void disableCache() {
		cache = null;
	}

	private final Map<String, Property<?>> propertyMap = new HashMap<>();

	/**
	 * <p>
	 * Loads the values of all non-transient {@link Property properties} in this
	 * {@link PropertyObject} based off of the specified {@link JSONObject}.
	 * <b>NOTE: The provided {@link JSONObject} is then used as the cache!</b>. It
	 * should not be modified after being provided to this method, either until the
	 * cache is disabled or until this method is called once more.
	 * </p>
	 * <p>
	 * Calling this method with <code>null</code> as an argument is effectively
	 * equivalent to calling {@link #disableCache()}.
	 * </p>
	 * 
	 * @param properties The {@link JSONObject} to load from.
	 */
	public void load(JSONObject properties) {
		if (properties != null)
			for (Property<?> p : propertyMap.values())
				if (!p.isTransient())
					p.forceLoad(properties);
		cache = properties;
	}

	protected Map<String, Property<?>> getPropertyMap() {
		return propertyMap;
	}

	public final Map<String, Property<?>> getPropertyMapView() {
		return Collections.unmodifiableMap(getPropertyMap());
	}

	public PropertyObject() {
	}

	protected final Property<String> stringProperty(String key) {
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

	protected final <N extends Number> Gateway<N, JSONValue> integralJsonGateway(Function<JSONNumber, N> getter) {
		return new Gateway<N, JSONValue>() {

			@Override
			public JSONValue to(N value) {
				return new JSONNumber(value.longValue());
			}

			@Override
			public N from(JSONValue value) {
				return getter.apply((JSONNumber) value);
			}
		};
	}

	/**
	 * Returns a {@link Gateway} that converts its "from" values (<code>N</code>
	 * values) to {@link JSONString} values, using the provided
	 * {@link StringGateway}. The returned {@link Gateway} first converts incoming
	 * arguments to strings using the provided {@link StringGateway}, then from
	 * {@link String}s to {@link JSONString}s. When converting from
	 * {@link JSONString} to the specified type, the resulting {@link Gateway} will
	 * first convert the {@link JSONString} to a {@link String} using the
	 * {@link JSONString#getValue() JSONString's getValue()} method, and then from a
	 * {@link String} to the specified type using the provided
	 * {@link StringGateway}.
	 * 
	 * @param <V>
	 * @param strGateway
	 * @return
	 */
	protected final <V> Gateway<V, JSONValue> toStringGateway(Gateway<String, V> strGateway) {
		return new Gateway<V, JSONValue>() {

			@Override
			public JSONValue to(V value) {
				return new JSONString(strGateway.from(value));
			}

			@Override
			public V from(JSONValue value) {
				return strGateway.to(((JSONString) value).getValue());
			}
		};
	}

	protected final <V extends PropertyObject> Gateway<V, JSONValue> toObjectGateway(
			Function<? super JSONValue, ? extends V> generator) {
		return new Gateway<V, JSONValue>() {

			@Override
			public JSONValue to(V value) {
				return value.toJSON();
			}

			@Override
			public V from(JSONValue value) {
				return generator.apply(value);
			}
		};
	}

	protected final <V extends PropertyObject> Property<V> toObjectProperty(String key, V def,
			Function<? super JSONValue, ? extends V> generator) {
		return new Property<V>(key, def, toObjectGateway(generator));
	}

	protected final <V extends PropertyObject> Property<V> toObjectProperty(String key,
			Function<? super JSONValue, ? extends V> generator) {
		return new Property<V>(key, null, toObjectGateway(generator));
	}

	protected final <V> Property<V> toStringProperty(String key, Gateway<String, V> strGateway) {
		return toStringProperty(key, null, strGateway);
	}

	protected final <V> Property<V> toStringProperty(String key, StringGateway<V> strGateway) {
		return toStringProperty(key, null, strGateway);
	}

	protected final <N extends Number> Property<N> integralProperty(String key, Function<JSONNumber, N> getter) {
		return integralProperty(key, null, getter);
	}

	protected final Property<Integer> intProperty(String key) {
		return intProperty(key, 0);
	}

	protected final Property<Byte> byteProperty(String key) {
		return byteProperty(key, (byte) 0);
	}

	protected final Property<Long> longProperty(String key) {
		return longProperty(key, 0);
	}

	protected final Property<BigDecimal> bigDecimalProperty(String key) {
		return bigDecimalProperty(key, null);
	}

	protected final Property<BigInteger> bigIntegerProperty(String key) {
		return bigIntegerProperty(key, null);
	}

	protected final Property<Instant> instantProperty(String key) {
		return instantProperty(key, null);
	}

	protected final Property<Duration> durationProperty(String key) {
		return durationProperty(key, null);
	}

	/**
	 * <p>
	 * Represents a {@link Property} that belongs to the containing object.
	 * </p>
	 * <p>
	 * {@link PropertyObject}s primarily store their data through the use of
	 * {@link Property properties}. A {@link Property} is a specialized type of
	 * attribute to an object that is aware of how to convert itself to and from a
	 * serializable state. (Without caching), whenever a {@link PropertyObject}
	 * needs to be saved, all of its characteristic, (non-{@link #isTransient()
	 * transient}) properties are converted to a serialized form and returned.
	 * Whenever the {@link PropertyObject} is {@link #load(JSONObject) loaded}, all
	 * non-{@link #isTransient() transient} {@link Property properties} are loaded
	 * from the {@link JSONObject} to be loaded from.
	 * </p>
	 * <p>
	 * 
	 * 
	 * @author Gartham
	 *
	 * @param <V>
	 */
	public class Property<V> extends Observable<V> {

		private final String key;
		private V value, def;
		private final Gateway<V, JSONValue> converter;

		/**
		 * Sets the "default" value of this {@link Property}. When the actual value of a
		 * {@link Property} is equal to its default value (as per
		 * {@link Objects#equals(Object)}), the property is not saved nor cached. The
		 * value of this field never affects the value stored by the {@link Property} it
		 * belongs to, and is only used for efficiency when saving.
		 * 
		 * @param def
		 */
		public void setDefault(V def) {
			if (def == this.def)
				return;
//			V temp = this.def;
			this.def = def;
//			if (!Objects.equals(def, value)) {
//				if (Objects.equals(def, temp))
//					save();
//			} else if (cache != null)
//				cache.remove(key);
		}

		public V getDefault() {
			return def;
		}

		private void forceLoad(JSONObject properties) {
			value = !properties.containsKey(key) ? def : converter.from(properties.get(key));
			if (cache != null && Objects.equals(def, value))
				cache.remove(key);
		}

		public void load(JSONObject properties) {
			if (!isTransient())
				forceLoad(properties);
		}

		private Property<? extends V> propertyBinding;
		private Set<Property<? super V>> propertyBindings = new HashSet<>();
		/**
		 * Determines whether this property represents an attribute of this
		 * {@link PropertyObject} or not. In a check for {@link Item#stackable(Item)
		 * stackability}, properties that are not designated as {@link #attribute
		 * attributes} are ignored.
		 */
		private boolean attribute = true, transient0;

		public String getKey() {
			return key;
		}

		public boolean isTransient() {
			return transient0;
		}

		public Property<V> setTransient(boolean transient0) {
			this.transient0 = transient0;
//			if (!this.transient0 && transient0)
//				save();
//			else if (this.transient0 && !transient0 && cache != null)
//				cache.remove(key);
			return this;
		}

		private void save() {
			if (isTransient())
				return;
			if (cache != null)
				if (Objects.equals(value, def))
					cache.remove(key);
				else
					cache.put(key, converter.to(value));
			PropertyObject.this.save();
		}

		public boolean isAttribute() {
			return attribute;
		}

		public Property<V> setAttribute(boolean attribute) {
			this.attribute = attribute;
			return this;
		}

		/**
		 * <p>
		 * Binds the specified property to this property. If the provided property has a
		 * different value from this one, this one is set to the value of the provided
		 * property.
		 * </p>
		 * <p>
		 * Providing <code>null</code> to this method is equivalent to calling
		 * {@link #unbind()}.
		 * </p>
		 * 
		 * @param binding The other property to bind this property to.
		 */
		public void bind(Property<? extends V> binding) {
			for (Property<? extends V> b = binding; b.propertyBinding != null; b = b.propertyBinding)
				if (b == this)
					throw new IllegalArgumentException("Cyclic binding detected.");
			binding.propertyBindings.add(this);
			if (binding != null && !Objects.equals(binding.get(), get()))
				set(binding.get());
			propertyBinding = binding;
		}

		/**
		 * Unbinds this property from the bound property, if it was already bound.
		 * Otherwise, does nothing.
		 */
		public void unbind() {
			if (propertyBinding != null) {
				propertyBinding.propertyBindings.remove(this);
				propertyBinding = null;
			}
		}

		protected Property(String key, Gateway<V, JSONValue> converter) {
			this(key, null, converter);
		}

		protected Property(String key, V def, Gateway<V, JSONValue> converter) {
			this.key = key;
			if (cache != null && def != null)
				cache.put(key, converter.to(def));
			value = this.def = def;
			this.converter = converter;
			propertyMap.put(key, this);
		}

		public void set(V value) {
			if (value == this.value)
				return;

			V temp = this.value;
			this.value = value;

			change(temp, value);
			for (Property<? super V> p : propertyBindings)
				p.set(value);

			save();
		}

		/**
		 * Sets the value of this {@link Property} without triggering any saving or
		 * listeners.
		 * 
		 * @param value The new value of the property.
		 */
		public void setSilent(V value) {
			this.value = value;
		}

		public V get() {
			return value;
		}

		/**
		 * Returns <code>null</code> if this {@link Property} holds its default value,
		 * or the converted value otherwise.
		 * 
		 * @return
		 */
		public JSONValue toJSON() {
			return Objects.equals(value, def) ? null : converter.to(value);
		}

	}

	/**
	 * Called by {@link Property properties} when changes have been made to this
	 * {@link PropertyObject} such that this {@link PropertyObject} needs to be
	 * saved.
	 */
	private void save() {
		for (Runnable r : changeListeners)
			r.run();
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		for (Property<?> p : propertyMap.values())
			if (!p.isTransient()) {
				JSONValue value = p.toJSON();
				if (value != null)
					o.put(p.key, value);
			}
		return o;
	}

	protected final <V> Property<V> toStringProperty(String key, V def, Gateway<String, V> strGateway) {
		return new Property<>(key, def, toStringGateway(strGateway));
	}

	protected final <V> Property<V> toStringProperty(String key, V def, StringGateway<V> strGateway) {
		return new Property<>(key, def, toStringGateway(strGateway));
	}

	protected final <N extends Number> Property<N> integralProperty(String key, N def, Function<JSONNumber, N> getter) {
		return new Property<>(key, def, integralJsonGateway(getter));
	}

	protected final Property<Integer> intProperty(String key, int def) {
		return integralProperty(key, def, JSONNumber::intValue);
	}

	protected final Property<Byte> byteProperty(String key, byte def) {
		return integralProperty(key, def, JSONNumber::byteValue);
	}

	protected final Property<Long> longProperty(String key, long def) {
		return integralProperty(key, def, JSONNumber::longValue);
	}

	protected final Property<BigDecimal> bigDecimalProperty(String key, BigDecimal def) {
		return toStringProperty(key, def, BigDecimal::new);
	}

	protected final Property<BigInteger> bigIntegerProperty(String key, BigInteger def) {
		return toStringProperty(key, def, BigInteger::new);
	}

	protected final Property<Instant> instantProperty(String key, Instant def) {
		return toStringProperty(key, def, Instant::parse);
	}

	protected final Property<Duration> durationProperty(String key, Duration def) {
		return toStringProperty(key, def, Duration::parse);
	}

	protected final Property<String> stringProperty(String key, String def) {
		return new Property<>(key, def, new Gateway<String, JSONValue>() {

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

	protected final <E extends Enum<E>> Property<E> enumStringProperty(String key, E def, Class<E> enumType) {
		return toStringProperty(key, def, value -> Enum.valueOf(enumType, key));
	}

	protected final <E extends Enum<E>> Property<E> enumStringProperty(String key, Class<E> enumType) {
		return toStringProperty(key, null, value -> Enum.valueOf(enumType, key));
	}

	protected final <E extends Enum<E>> Property<E> enumProperty(String key, E def, Class<E> enumType) {
		return new Property<E>(key, def, new Gateway<E, JSONValue>() {

			@Override
			public JSONValue to(E value) {
				return new JSONNumber(value.ordinal());
			}

			@Override
			public E from(JSONValue value) {
				return enumType.getEnumConstants()[((JSONNumber) value).intValue()];
			}
		});
	}

	protected final <E extends Enum<E>> Property<E> enumProperty(String key, Class<E> enumType) {
		return enumProperty(key, null, enumType);
	}

}
