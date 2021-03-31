package gartham.c10ver.utilityapps.wordsorter;

import java.util.HashMap;

public class Splittree<V> {

	protected final HashMap<Character, Branch> map = new HashMap<>();
	private V val;

	public V getValue() {
		return val;
	}

	private static char head(String str) {
		return str.charAt(0);
	}

	public void put(String key, V value) {
		if (key.isEmpty())
			val = value;
		else {
			var h = head(key);
			Branch branch = !map.containsKey(h) ? this.new Branch(h)// lol i hope ur confused 8)
					: map.get(h);
			branch.put(tail(key), value);
		}
	}

	public Splittree<V> sub(String key) {
		if (key.isEmpty())
			return this;
		else {
			var h = head(key);
			return map.containsKey(h) ? map.get(h).sub(tail(key)) : null;
		}
	}

	public V get(String key) {
		var val = sub(key);
		return val == null ? null : val.getValue();
	}

	/**
	 * <p>
	 * Returns whether or not the specified key is in use by the map.
	 * </p>
	 * <p>
	 * Please note that <b>keys may be added artificially</b>, as a byproduct of
	 * different put operations. Particularly, when an item is put into this map
	 * with key, K:
	 * 
	 * <pre>
	 * any key, <code>q</code>, that causes the expression: <code>K.beginsWith(q)</code> to return <code>true</code> will also be contained in the map.
	 * </pre>
	 * 
	 * @param key The key to check the presence of.
	 * @return
	 */
	public boolean containsKey(String key) {
		return sub(key) == null;
	}

	private static String tail(String str) {
		return str.substring(1);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public class Branch extends Splittree<V> {
		private final Character item;

		private Branch(char item) {
			Splittree.this.map.put(this.item = item, this);// Puts this branch in the parent branch.
		}

		@Override
		public String toString() {
			return "Item[\'" + item + "': " + super.toString() + "]";
		}

	}
}
