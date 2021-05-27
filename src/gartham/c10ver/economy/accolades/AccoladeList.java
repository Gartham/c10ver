package gartham.c10ver.economy.accolades;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.util.Gateway;

import gartham.c10ver.data.autosave.SavablePropertyObject;

public class AccoladeList extends SavablePropertyObject implements Iterable<AccoladeList.ListEntry> {

	private static final AccoladeType[] ACCOLADE_TYPES = AccoladeType.values();

	public AccoladeList(File file) {
		this(file, true);
	}

	public AccoladeList(File file, boolean load) {
		super(file);
		if (load)
			load();
		if (accolades.get() == null)
			accolades.set(new ArrayList<>());
	}

	public BigInteger getCount() {
		return accolades.get().stream().map(ListEntry::count).reduce(BigInteger.ZERO, BigInteger::add);
	}

	public int typeCount() {
		return accolades.get().size();
	}

	public boolean isEmpty() {
		return accolades.get().isEmpty();
	}

	public BigInteger get(AccoladeType accolade) {
		var arr = accolades.get();
		var pos = Collections.binarySearch(arr, accolade);
		return (0x80000000 & pos) == 0 ? accolades.get().get(pos).count : BigInteger.ZERO;
	}

	public void set(AccoladeType accolade, BigInteger amount) {
		if (amount.compareTo(BigInteger.ZERO) != 1)
			clear(accolade);
		var arr = accolades.get();
		var pos = Collections.binarySearch(arr, accolade);
		if ((0x80000000 & pos) == 0x80000000) {
			pos = -pos - 1;
			ListEntry entry = new ListEntry(accolade, amount);
			accolades.get().add(pos, entry);
		} else
			accolades.get().get(pos).count = amount;
	}

	public void remove(AccoladeType accolade) {
		remove(accolade, BigInteger.ONE);
	}

	public void remove(AccoladeType accolade, BigInteger amt) {
		if (amt.compareTo(BigInteger.ZERO) == -1)
			add(accolade, amt.negate());
		var arr = accolades.get();
		var pos = Collections.binarySearch(arr, accolade);
		if ((0x80000000 & pos) == 0) {
			ListEntry ye = accolades.get().get(pos);
			ye.count = ye.count.subtract(amt);
			if (ye.count.compareTo(BigInteger.ZERO) == 1)
				arr.remove(pos);
		}
	}

	public void clear(AccoladeType accolade) {
		var arr = accolades.get();
		var pos = Collections.binarySearch(arr, accolade);
		if ((0x80000000 & pos) == 0)
			arr.remove(pos);
	}

	public void add(AccoladeType accolade) {
		add(accolade, BigInteger.ONE);
	}

	public void add(AccoladeType accolade, BigInteger amt) {
		if (amt.compareTo(BigInteger.ZERO) == -1)
			remove(accolade, amt.negate());
		var arr = accolades.get();
		var pos = Collections.binarySearch(arr, accolade);
		if ((0x80000000 & pos) == 0x80000000) {
			pos = -pos - 1;
			ListEntry entry = new ListEntry(accolade, amt);
			accolades.get().add(pos, entry);
		} else {
			ListEntry ye = accolades.get().get(pos);
			ye.count = ye.count.add(amt);
		}
	}

	public final class ListEntry implements Comparable<AccoladeType> {
		public final AccoladeType type;
		public BigInteger count;

		private ListEntry(AccoladeType type, BigInteger count) {
			this.type = type;
			this.count = count;
		}

		private ListEntry(AccoladeType type) {
			this.type = type;
			count = BigInteger.ZERO;
		}

		public JSONValue toJSON() {
			return new JSONArray(new JSONNumber(type.ordinal()), new JSONString(count.toString()));
		}

		private ListEntry(JSONValue json) {
			var ja = (JSONArray) json;
			type = ACCOLADE_TYPES[((JSONNumber) ja.get(0)).intValue()];
			count = new BigInteger(((JSONString) ja.get(1)).getValue());
		}

		@Override
		public int compareTo(AccoladeType o) {
			return type.compareTo(o);
		}

		public BigInteger count() {
			return count;
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

	@Override
	public Iterator<ListEntry> iterator() {
		return accolades.get().iterator();
	}

}
