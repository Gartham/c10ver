package gartham.c10ver.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

public class PropertyObject {

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
	 * <p>
	 * Calling this method automatically enables caching with the provided map as
	 * the cache.
	 * </p>
	 * 
	 * @param properties The {@link JSONObject} to load from.
	 */
	public void load(JSONObject properties) {
		for (Property<?> p : propertyMap.values())
			p.load(properties);
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

	protected final <V> Gateway<V, JSONValue> toStringGateway(StringGateway<V> gateway) {
		return toStringGateway((Gateway<String, V>) gateway);
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

	protected final <V, C extends Collection<? extends V>> Property<C> listProperty(String key,
			Function<? super V, ? extends JSONValue> valueToJSON, Function<JSONArray, C> listCreator) {
		return new Property<>(key, new Gateway<>() {

			@Override
			public JSONValue to(C value) {
				return new JSONArray(JavaTools.mask(value, valueToJSON));
			}

			@Override
			public C from(JSONValue value) {
				return listCreator.apply((JSONArray) value);
			}

		});
	}

	protected final <V, C extends Collection<? extends V>> Property<C> listProperty(String key,
			Function<? super V, ? extends JSONValue> valueToJSON,
			Function<? super Iterable<? extends V>, ? extends C> listCreator,
			Function<? super JSONValue, ? extends V> valueConverter) {
		return new Property<>(key, new Gateway<>() {

			@Override
			public JSONValue to(C value) {
				return new JSONArray(JavaTools.mask(value, valueToJSON));
			}

			@Override
			public C from(JSONValue value) {
				return listCreator.apply(JavaTools.mask((JSONArray) value, valueConverter));
			}

		});
	}

	protected final <V, C extends Collection<V>> Property<C> listProperty(String key,
			Function<? super V, ? extends JSONValue> valueToJSON, Supplier<? extends C> listGenerator,
			Function<? super JSONValue, ? extends V> valueConverter) {
		return new Property<>(key, new Gateway<>() {

			@Override
			public JSONValue to(C value) {
				return new JSONArray(JavaTools.mask(value, valueToJSON));
			}

			@Override
			public C from(JSONValue value) {
				var l = listGenerator.get();
				for (V v : JavaTools.mask((JSONArray) value, valueConverter))
					l.add(v);
				return l;
			}

		});
	}

	protected final <V> Property<ArrayList<V>> listProperty(String key, Gateway<V, JSONValue> gateway) {
		return new Property<>(key, new Gateway<>() {

			@Override
			public JSONValue to(ArrayList<V> value) {
				return new JSONArray(JavaTools.mask(value, gateway.from()));
			}

			@Override
			public ArrayList<V> from(JSONValue value) {
				var arr = new ArrayList<V>();
				for (var v : JavaTools.mask((JSONArray) value, gateway.to()))
					arr.add(v);
				return arr;
			}
		});
	}

	protected final <K, V, M extends Map<K, V>> Property<M> mapProperty(String key, Gateway<K, String> keyconv,
			Gateway<V, JSONValue> valconv, Supplier<? extends M> mapmaker) {
		return new Property<>(key, new Gateway<>() {

			@Override
			public JSONObject to(M value) {
				var obj = new JSONObject();
				for (Entry<K, V> e : value.entrySet())
					obj.put(keyconv.to(e.getKey()), valconv.to(e.getValue()));
				return obj;
			}

			@Override
			public M from(JSONValue value) {
				M map = mapmaker.get();
				for (var e : ((JSONObject) value).entrySet())
					map.put(keyconv.from(e.getKey()), valconv.from(e.getValue()));
				return map;
			}
		});
	}

	protected final <K, V> Property<HashMap<K, V>> mapProperty(String key, Gateway<K, String> keyconv,
			Gateway<V, JSONValue> valconv) {
		return mapProperty(key, keyconv, valconv, HashMap::new);
	}

	protected final <V, M extends Map<String, V>> Property<M> mapProperty(String key, Gateway<V, JSONValue> valconv,
			Supplier<? extends M> mapmaker) {
		return mapProperty(key, StringGateway.string(), valconv, mapmaker);
	}

	protected final <V> Property<HashMap<String, V>> mapProperty(String key, Gateway<V, JSONValue> valconv) {
		return mapProperty(key, valconv, HashMap::new);
	}

	protected final <K, V, M extends Map<K, V>> Property<M> mapProperty(String key, M def, Gateway<K, String> keyconv,
			Gateway<V, JSONValue> valconv, Supplier<? extends M> mapmaker) {
		return new Property<>(key, def, new Gateway<>() {

			@Override
			public JSONObject to(M value) {
				var obj = new JSONObject();
				for (Entry<K, V> e : value.entrySet())
					obj.put(keyconv.to(e.getKey()), valconv.to(e.getValue()));
				return obj;
			}

			@Override
			public M from(JSONValue value) {
				M map = mapmaker.get();
				for (var e : ((JSONObject) value).entrySet())
					map.put(keyconv.from(e.getKey()), valconv.from(e.getValue()));
				return map;
			}
		});
	}

	protected final <K, V> Property<HashMap<K, V>> mapProperty(String key, HashMap<K, V> def,
			Gateway<K, String> keyconv, Gateway<V, JSONValue> valconv) {
		return mapProperty(key, def, keyconv, valconv, HashMap::new);
	}

	protected final <V, M extends Map<String, V>> Property<M> mapProperty(String key, M def,
			Gateway<V, JSONValue> valconv, Supplier<? extends M> mapmaker) {
		return mapProperty(key, def, StringGateway.string(), valconv, mapmaker);
	}

	protected final <V> Property<HashMap<String, V>> mapProperty(String key, HashMap<String, V> def,
			Gateway<V, JSONValue> valconv) {
		return mapProperty(key, def, valconv, HashMap::new);
	}

	protected final <V extends PropertyObject> Property<V> toObjectProperty(String key,
			Function<? super JSONValue, ? extends V> generator) {
		return new Property<V>(key, toObjectGateway(generator));
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

	protected final void load(Property<?> property, JSONObject properties) {
		property.load(properties);
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
	public class Property<V> {

		protected final void load(JSONObject properties) {
			set(properties != null && properties.containsKey(key) ? converter.from(properties.get(key)) : def);
		}

		private final String key;
		private V value, def;
		private final Gateway<V, JSONValue> converter;

		private boolean attribute = true, trans;

		public boolean isTransient() {
			return trans;
		}

		public Property<V> setTransient(boolean trans) {
			this.trans = trans;
			return this;
		}

		public boolean isAttribute() {
			return attribute;
		}

		public Property<V> setAttribute(boolean attribute) {
			this.attribute = attribute;
			return this;
		}

		public V getDef() {
			return def;
		}

		public Property<V> setDef(V def) {
			this.def = def;
			return this;
		}

		public String getKey() {
			return key;
		}

		protected Property(String key, V defaultValue, Gateway<V, JSONValue> converter) {
			this.key = key;
			if (propertyMap.containsKey(key))
				throw new IllegalArgumentException("Key taken: " + key + " in class " + getClass().getSimpleName());
			propertyMap.put(key, this);
			def = defaultValue;
			this.converter = converter;
		}

		protected Property(String key, Gateway<V, JSONValue> converter) {
			this(key, null, converter);
		}

		public Property<V> set(V value) {
			this.value = value;
			return this;
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
			return trans || Objects.equals(value, def) ? null : converter.to(value);
		}

	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		for (Property<?> p : propertyMap.values()) {
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

	protected static <E extends Enum<E>> Gateway<E, JSONValue> enumGateway(Class<E> enumType) {
		return new Gateway<E, JSONValue>() {

			@Override
			public JSONValue to(E value) {
				return new JSONNumber(value.ordinal());
			}

			@Override
			public E from(JSONValue value) {
				return enumType.getEnumConstants()[((JSONNumber) value).intValue()];
			}
		};
	}

	protected final <E extends Enum<E>> Property<E> enumProperty(String key, E def, Class<E> enumType) {
		return new Property<E>(key, def, enumGateway(enumType));
	}

	protected final <E extends Enum<E>> Property<E> enumProperty(String key, Class<E> enumType) {
		return enumProperty(key, null, enumType);
	}

}
