package gartham.c10ver.utilityapps.wordsorter;

import java.util.HashMap;

public class Splittree {

	protected final HashMap<Character, Branch> map = new HashMap<>();
	private boolean hit;

	private static char head(String str) {
		return str.charAt(0);
	}

	public boolean add(String value) {
		if (value.isEmpty())
			return hit ? false : (hit = true);
		else {
			var h = head(value);
			Branch branch = !map.containsKey(h) ? this.new Branch(h)// lol i hope ur confused 8)
					: map.get(h);
			return branch.add(tail(value));
		}
	}

	private static String tail(String str) {
		return str.substring(1);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public class Branch extends Splittree {
		private final Character item;

		private Branch(char item) {
			Splittree.this.map.put(this.item = item, this);// Puts this branch in the parent branch.
		}

		@Override
		public String toString() {
			return "{\'" + item + ": " + super.toString() + "}";
		}

	}
}
