package gartham.c10ver.economy.accolades;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.util.Gateway;

import gartham.c10ver.data.autosave.SavablePropertyObject;

public class AccoladeList extends SavablePropertyObject {

	private static final AccoladeType[] ACCOLADE_TYPES = AccoladeType.values();

	public AccoladeList(File file) {
		super(file);
	}

	public final class ListEntry {
		public final AccoladeType type;
		public BigInteger count;

		public ListEntry(AccoladeType type, BigInteger count) {
			this.type = type;
			this.count = count;
		}

		public ListEntry(AccoladeType type) {
			this.type = type;
			count = BigInteger.ZERO;
		}

		public JSONValue toJSON() {
			return new JSONArray(new JSONNumber(type.ordinal()), new JSONString(count.toString()));
		}

		public ListEntry(JSONValue json) {
			var ja = (JSONArray) json;
			type = ACCOLADE_TYPES[((JSONNumber) ja.get(0)).intValue()];
			count = new BigInteger(((JSONString) ja.get(1)).getValue());
		}

	}

	private final Property<ArrayList<ListEntry>> accolades = listProperty("accolades", new Gateway<>() {
		@Override
		public JSONValue to(ListEntry value) {
			return value.toJSON();
		}

		@Override
		public ListEntry from(JSONValue value) {
			return new ListEntry(value);
		}
	});

}
