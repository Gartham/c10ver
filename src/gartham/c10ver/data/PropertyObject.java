package gartham.c10ver.data;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONConstant;
import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

/**
 * <h1>Property Objects</h1>
 * <p>
 * An object with <b>Properties</b> that can be stored to and recovered from
 * JSON format. Subclasses create and register properties with one of the
 * protected property methods in this class, and use those properties to store
 * aspects of the object. Whenever the {@link PropertyObject} needs to be
 * converted to JSON format (e.g., if the {@link PropertyObject} is being
 * written to a file in JSON), the {@link #toJSON()} method can be called. If a
 * {@link PropertyObject} is being loaded back from a file, the subclass can
 * call the {@link #load(JSONObject)} method from its constructor to load all
 * the values from the JSON data back into the {@link PropertyObject}.
 * </p>
 * 
 * <h3>Properties</h3>
 * <p>
 * {@link Property Properties} are similar to normal class fields. They each
 * store a value and have an associated type, and can have its value changed,
 * but they also can be "written out" in JSON format and loaded back from JSON
 * format. To do this, they keep track of a {@link Gateway}, that "bridges"
 * their values with JSON. Specifically, the {@link Gateway} lets the
 * {@link Property} convert its value to JSON and back at any desired time.
 * </p>
 * 
 * <h3>Default Values</h3>
 * <p>
 * {@link Property Properties} have default values, (just as fields can in
 * Java). If, at the time of a {@link PropertyObject} being saved, a
 * {@link Property}'s value is its default value, the {@link Property} is
 * <b>not</b> written to the JSON output. This is done in an effort to conserve
 * space in files. Likewise, if a {@link PropertyObject} is loaded from a
 * {@link JSONObject} and the {@link JSONObject} contains no entry for a
 * specific {@link Property}, that property's value <i><b>becomes</b></i> its
 * default value. This is done to make sure that {@link Property properties}
 * that were saved when their values were equal to their default values get
 * loaded back correctly.
 * </p>
 * <p>
 * To illustrate these characteristics, consider the following {@link Property
 * properties} in a {@link PropertyObject}:
 * 
 * <pre>
 * <code>
 * <span style=
"color:purple"><b>public class</b></span> GameCreature <span style=
"color:purple"><b>extends</b></span> PropertyObject {
 * 	<span style=
"color:purple"><b>private final</b></span> Property<String> <span style=
"color:green">type</span> = stringProperty(<span style=
"color:blue">"type"</span>);
 * 	<span style=
"color:purple"><b>private final</b></span> Property<Integer> <span style=
"color:green">health</span> = intProperty(<span style=
"color:blue">"health"</span>, 100); <span style=
"color:orange">// Health is full (100/100) by default.</span>
 * }
 * </code>
 * </pre>
 * 
 * Consider that a <code>new GameCreature()</code> is created. When calling the
 * {@link #toJSON()} method on the brand <code>new GameCreature()</code>, the
 * following {@link JSONObject} is returned:
 * 
 * <pre>
 * <code>
 * {	}
 * </code>
 * </pre>
 * 
 * This is because the value of the properties stored in the
 * <code>GameCreature</code> object are all their defaults (<code>null</code>
 * and <code>100</code>). If, instead, we created a
 * <code>new GameCreature()</code> and set its
 * <code style="color:green">health</code> to <code>50</code>, then
 * {@link #toJSON()} would return:
 * 
 * <pre>
 * <code>
 * {
 * 	"health":50
 * }
 * </code>
 * </pre>
 * 
 * If we then took a brand <code>new GameCreature()</code> object and
 * <i>loaded</i> it from this JSON data, the <code>GameCreature</code> would
 * have the values:
 * 
 * <pre>
 * <code>
 * type = null
 * health = 50
 * </code>
 * </pre>
 * 
 * This is because the <code style="color:green">health</code> property was not
 * its default (<code>100</code>) during saving, and was, thus, written to the
 * JSON output, causing it to be loaded back in the
 * <code>new GameCreature()</code> object, while the
 * <code style="color:green">type</code> property was its default:
 * <code>null</code>, and so, was not written to JSON, and was read back as
 * <code>null</code>.
 * 
 * To reemphasize, if a {@link Property} is not contained in a
 * {@link JSONObject} and the respective {@link PropertyObject} is loaded from
 * that {@link JSONObject}, the {@link Property} will have its value set to its
 * default. A quick way to force all {@link Property properties} to assume their
 * default values is to load the {@link PropertyObject} from an empty
 * {@link JSONObject}:
 * 
 * <pre>
 * <code>
 * PropertyObject <span style="color:green">myCreature = <span style="color:purple"><b>new</b></span> GameCreature();
 * myCreature.setType("dragon");
 * myCreature.setHealth(250); <span style="color:orange">// Dragons have extra health!</span>
 * myCreature.load(<span style="color:purple"><b>new</b></span> JSONObject());
 * 
 * System.out.println(myCreature.getHealth()); <span style="color:orange">// Prints 100! Not 250.</span>
 * System.out.println(myCreature.getType()); <span style="color:orange">// Prints null.</span>
 * </code>
 * </pre>
 * 
 * </p>
 * <p>
 * The
 * <ol>
 * <li>A {@link Property}'s value is its default value immediately after it is
 * instantiated.</li>
 * <li>A {@link Property}'s value is its default value immediately after it is
 * loaded if the provided {@link JSONObject} does not contain a mapping for it
 * (or if the {@link JSONObject} to be loaded from is <code>null</code>).</li>
 * </ol>
 * </p>
 * <p>
 * Default values are used to <b>shrink output size</b>. Specifically, if a
 * {@link Property}'s current value is its default value, its
 * {@link PropertyObject} will not include it in the map returned by
 * {@link #toJSON()}. This allows for less data in many situations, e.g., when
 * writing to a file.
 * </p>
 * 
 * <h3>JSON Conversion</h3>
 * <p>
 * Every {@link PropertyObject} has a {@link #toJSON()} method. This returns a
 * {@link JSONObject} which contains key:pair mappings of every property's
 * unique ID to its JSON-format value. Every {@link PropertyObject} can be
 * loaded using {@link #load(JSONObject)}. Doing this will cause every
 * {@link Property} to {@link Property#load(JSONObject)} itself from the
 * {@link JSONObject} map by obtaining the value with the {@link Property#key
 * property's key} in the map and converting it from JSON to whatever type the
 * {@link Property} is.
 * </p>
 * 
 * @author Gartham
 *
 */
public class PropertyObject {

	private Map<String, Property<?>> propertyMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	protected final <V> Property<V> getProperty(String key) {
		return (Property<V>) propertyMap.get(key);
	}

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

	public static final StringGateway<JSONValue> JSON_STRING_GATEWAY = new StringGateway<JSONValue>() {

		@Override
		public JSONValue to(String value) {
			return new JSONString(value);
		}

		@Override
		public String from(JSONValue value) {
			return ((JSONString) value).getValue();
		}
	};

	protected final <V> Property<HashSet<V>> setProperty(String key, Gateway<V, JSONValue> gateway) {
		return new Property<>(key, new Gateway<>() {

			@Override
			public JSONValue to(HashSet<V> value) {
				return new JSONArray(JavaTools.mask(value, gateway.from()));
			}

			@Override
			public HashSet<V> from(JSONValue value) {
				var arr = new HashSet<V>();
				for (var v : JavaTools.mask((JSONArray) value, gateway.to()))
					arr.add(v);
				return arr;
			}
		});
	}

	protected final <V> Property<HashSet<V>> setProperty(String key, HashSet<V> def, Gateway<V, JSONValue> gateway) {
		return new Property<>(key, def, new Gateway<>() {

			@Override
			public JSONValue to(HashSet<V> value) {
				return new JSONArray(JavaTools.mask(value, gateway.from()));
			}

			@Override
			public HashSet<V> from(JSONValue value) {
				var arr = new HashSet<V>();
				for (var v : JavaTools.mask((JSONArray) value, gateway.to()))
					arr.add(v);
				return arr;
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

	protected final <V> Property<Map<String, V>> mapProperty(String key, Map<String, V> def,
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
	public class Property<V> implements Cloneable {

		public Property<V> cloneTo(PropertyObject parent) throws CloneNotSupportedException {
			var x = parent.new Property<>(key, def, converter);
			x.value = value;
			x.attribute = attribute;
			x.trans = trans;
			return x;
		}

		protected final void load(JSONObject properties) {
			set(properties != null && properties.containsKey(key) ? converter.from(properties.get(key)) : def);
		}

		private final String key;
		private V value, def;
		private final Gateway<V, JSONValue> converter;
		private Map<BiConsumer<? super V, ? super V>, Integer> listeners;

		public void addListener(BiConsumer<? super V, ? super V> listener) {
			int c;
			if (listeners == null) {
				listeners = new IdentityHashMap<>();
				c = 0;
			} else
				c = listeners.containsKey(listener) ? listeners.get(listener) : 0;
			listeners.put(listener, c + 1);
		}

		public void removeListener(BiConsumer<? super V, ? super V> listener) {
			if (listeners == null || !listeners.containsKey(listener))
				return;
			int c = listeners.get(listener);
			if (--c == 0) {
				listeners.remove(listener);
				if (listeners.isEmpty())
					listeners = null;
			}
		}

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

		/**
		 * Sets the default value for this {@link PropertyObject}, but <b>does not</b>
		 * modify the {@link PropertyObject}'s current value. The default value is used
		 * in two scenarios:
		 * <ol>
		 * <li>When the property is first instantiated, its value is the default value
		 * (if any is set, otherwise <code>null</code>).</li>
		 * <li>If
		 * 
		 * @param def
		 * @return
		 */
		public Property<V> setDef(V def) {
			this.def = def;
			return this;
		}

		public String getKey() {
			return key;
		}

		public Property(String key, V defaultValue, Gateway<V, JSONValue> converter) {
			this.key = key;
			if (propertyMap.containsKey(key))
				throw new IllegalArgumentException("Key taken: " + key + " in class " + getClass().getSimpleName());
			propertyMap.put(key, this);
			def = defaultValue;
			this.converter = converter;
		}

		public Property(String key, Gateway<V, JSONValue> converter) {
			this(key, null, converter);
		}

		public Property<V> set(V value) {
			V old = this.value;
			this.value = value;
			if (listeners != null)
				for (var e : listeners.entrySet())
					for (int i = 0; i < e.getValue(); i++)
						e.getKey().accept(old, value);
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

	protected final Property<Color> colorProperty(String key) {
		return colorProperty(key, null);
	}

	protected final Property<Color> colorProperty(String key, Color def) {
		return toStringProperty(key, def, new Gateway<String, Color>() {

			@Override
			public Color to(String value) {
				return new Color(Integer.valueOf(value), true);
			}

			@Override
			public String from(Color value) {
				return String.valueOf(value.getRGB());
			}
		});
	}

	protected final Property<Boolean> booleanProperty(String key) {
		return booleanProperty(key, false);
	}

	protected final Property<Boolean> booleanProperty(String key, Boolean def) {
		return new Property<Boolean>(key, def, new Gateway<>() {

			@Override
			public JSONValue to(Boolean value) {
				return value == null ? JSONConstant.NULL : value ? JSONConstant.TRUE : JSONConstant.FALSE;
			}

			@Override
			public Boolean from(JSONValue value) {
				var c = (JSONConstant) value;
				return c == JSONConstant.NULL ? null : c == JSONConstant.TRUE;
			}
		});
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

	/**
	 * <p>
	 * Clones this {@link PropertyObject} using {@link Object#clone}, then replaces
	 * the backing {@link #propertyMap} map with a new {@link Map} containing a
	 * clone of each {@link Property} contained in this {@link PropertyObject}.
	 * </p>
	 * <p>
	 * Essentially, the {@link #propertyMap}s of the two {@link PropertyObject} will
	 * be different map objects, and an attempt will be made to clone every single
	 * {@link Property} in this object (using
	 * {@link Property#cloneTo(PropertyObject)}), into the resulting object.
	 * </p>
	 * <p>
	 * The cloning mechanism for {@link Property#cloneTo(PropertyObject)} performs a
	 * shallow copy, so whatever object a {@link Property} has when it is cloned,
	 * the resulting {@link Property} will also have. This means that if some of
	 * this {@link PropertyObject} is defined by the value in one of its
	 * {@link Property Properties} and that value is mutable, whenever that
	 * property's actual value is modified (not when the {@link Property#value}
	 * variable is modified), both the cloned {@link Property} and the original
	 * {@link Property} will see that change, unless one of them changes the object
	 * pointed to by their {@link Property#value} field.
	 * </p>
	 */
	@Override
	public PropertyObject clone() throws CloneNotSupportedException {
		var po = (PropertyObject) super.clone();
		var newmap = new HashMap<String, Property<?>>();
		po.propertyMap = newmap;
		for (var e : propertyMap.entrySet())
			newmap.put(e.getKey(), e.getValue().cloneTo(po));
		return po;
	}

}
