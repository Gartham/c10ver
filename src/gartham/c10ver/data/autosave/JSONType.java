package gartham.c10ver.data.autosave;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

import gartham.c10ver.data.observe.Observable;

public class JSONType extends Observable {

	private final JSONObject properties = new JSONObject();

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

	protected final <V> Property<V> toStringProperty(String key, Gateway<String, V> strGateway) {
		return new Property<>(key, toStringGateway(strGateway));
	}

	protected final <V> Property<V> toStringProperty(String key, StringGateway<V> strGateway) {
		return new Property<>(key, toStringGateway(strGateway));
	}

	protected final <N extends Number> Property<N> integralProperty(String key, Function<JSONNumber, N> getter) {
		return new Property<>(key, integralJsonGateway(getter));
	}

	protected final Property<Integer> intProperty(String key) {
		return integralProperty(key, JSONNumber::intValue);
	}

	protected final Property<Byte> byteProperty(String key) {
		return integralProperty(key, JSONNumber::byteValue);
	}

	protected final Property<Long> longProperty(String key) {
		return integralProperty(key, JSONNumber::longValue);
	}

	protected final Property<BigDecimal> bigDecimalProperty(String key) {
		return toStringProperty(key, BigDecimal::new);
	}

	protected final Property<BigInteger> bigIntegerProperty(String key) {
		return toStringProperty(key, BigInteger::new);
	}

	protected final Property<Instant> instantProperty(String key) {
		return toStringProperty(key, Instant::parse);
	}

	protected final Property<Duration> durationProperty(String key) {
		return toStringProperty(key, Duration::parse);
	}

	protected class Property<V> implements Changeable {
		private final String key;
		private final AutosaveValue<V> value;
		private final Gateway<V, JSONValue> converter;

		public Property(String key, Gateway<V, JSONValue> converter) {
			this(key, null, converter);
		}

		public Property(String key, V value, Gateway<V, JSONValue> converter) {
			this.key = key;
			this.value = new AutosaveValue<>(value, JSONType.this);
			this.converter = converter;
		}

		public void set(V value) {
			this.value.setValue(value);
		}

		public V get() {
			return value.getValue();
		}

		@Override
		public void change() {
			if (value.getValue() == null)
				properties.remove(key);
			else
				properties.put(key, converter.to(value.getValue()));
		}

	}

}
