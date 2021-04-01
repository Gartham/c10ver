package gartham.c10ver.utilityapps.wordsorter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

// Untested
public class SortedSplitTree<V> {

	private static final Comparator<Object> SEARCHER = (o1, o2) -> Character.compare(
			o1 instanceof SortedSplitTree<?>.Branch ? ((SortedSplitTree<?>.Branch) o1).item : (Character) o1,
			o2 instanceof SortedSplitTree<?>.Branch ? ((SortedSplitTree<?>.Branch) o2).item : (Character) o2);

	protected final List<Branch> map = new ArrayList<>();
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
			var p = JavaTools.binarySearch(h, map, SEARCHER);
			Branch branch;
			if (p < 0)
				map.add(-p - 1, branch = new Branch(h));
			else
				branch = map.get(p);
			branch.put(tail(key), value);
		}
	}

	public SortedSplitTree<V> sub(String key) {
		if (key.isEmpty())
			return this;
		else {
			var h = head(key);
			var p = JavaTools.binarySearch(h, map, SEARCHER);
			return p >= 0 ? map.get(p).sub(tail(key)) : null;
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

	public class Branch extends SortedSplitTree<V> implements Comparable<Branch> {
		private final Character item;

		private Branch(char item) {
			this.item = item;
		}

		@Override
		public String toString() {
			return "Item[\'" + item + "': " + super.toString() + "]";
		}

		@Override
		public int compareTo(SortedSplitTree<V>.Branch o) {
			return Character.compare(item, o.item);
		}

	}

}
