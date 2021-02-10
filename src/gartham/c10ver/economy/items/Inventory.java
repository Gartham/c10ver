package gartham.c10ver.economy.items;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.utils.DataUtils;
import gartham.c10ver.utils.Paginator;

/**
 * A compressed way of storing items.
 * 
 * @author Gartham
 *
 */
public class Inventory {

	private final File invdir;

	public Inventory(File userDir) {
		invdir = new File(userDir, "inventory");
		File[] files = invdir.listFiles();
		if (files != null) {
			for (File type : files)
				new Entry<>(type);
		}
	}

	private final Map<String, Entry<?>> entries = new HashMap<>();
	private final List<Entry<?>> entryList = new ArrayList<>();

	/**
	 * Gets a page of entries in this {@link Inventory}. The number of entries per
	 * page is specified by the <code>pagesize</code> argument, and the page is
	 * specified by the <code>page</code> argument. <code>null</code> is returned,
	 * in leiu of an empty list, if the page number is invalid. The only scenario in
	 * which the returned list is empty is when the 1st page is requested (the value
	 * of the <code>page</code> argument is 1) but there are no entries in this
	 * {@link Inventory}.
	 * 
	 * @param page     The page to return.
	 * @param pagesize The maximum number of elements that can be returned in this
	 *                 page.
	 * @return A new, unmodifiable list containing the entries that belong to the
	 *         specified page.
	 */
	public List<Entry<?>> getPage(int page, int pagesize) {
		List<Entry<?>> res = Paginator.paginate(page, pagesize, entryList);
		return res == null ? null : Collections.unmodifiableList(res);
	}

	private static final Comparator<Object> COMPARATOR = (o1, o2) -> (o1 instanceof String ? (String) o1
			: o1 instanceof Entry<?> ? ((Entry<?>) o1).getType() : ((Entry<?>.ItemStack) o1).getType())
					.compareTo(o2 instanceof String ? (String) o2
							: o2 instanceof Entry<?> ? ((Entry<?>) o2).getType() : ((Entry<?>.ItemStack) o2).getType());

	@SuppressWarnings("unchecked")
	public <I extends Item> void add(I item, BigInteger amt) {
		if (entries.containsKey(item.getItemType()))
			((Entry<I>) entries.get(item.getItemType())).add(item, amt);
		else
			entries.put(item.getItemType(), new Entry<>(item, amt));
	}

	public void add(Item item) {
		add(item, BigInteger.ONE);
	}

	@SuppressWarnings("unchecked")
	public <I extends Item> boolean remove(I item, BigInteger amt) {
		return entries.containsKey(item.getItemType()) ? ((Entry<I>) entries.get(item.getItemType())).remove(item, amt)
				: false;
	}

	/**
	 * Returns the {@link Entry entries} in this {@link Inventory}. This
	 * {@link List} should not be modified directly, although the items in this list
	 * can be modified using their appropriate methods.
	 * 
	 * @return The entries in this {@link Inventory}.
	 */
	public List<Entry<?>> getEntries() {
		return entryList;
	}

	public Entry<?> get(int index) {
		return entryList.get(index);
	}

	public Entry<?> get(String key) {
		return entries.get(key);
	}

	public final class Entry<I extends Item> implements Comparable<Entry<?>> {
		private final List<ItemStack> stacks = new ArrayList<>(1);// The different stacks of this type of item.
		private boolean alive = false;

		public BigInteger getTotalCount() {
			BigInteger bi = BigInteger.ZERO;
			for (ItemStack is : stacks)
				bi = bi.add(is.count());
			return bi;
		}

		public List<ItemStack> getPage(int page, int pagesize) {
			return Paginator.paginate(page, pagesize, stacks);
		}

		private File getFile() {
			return new File(invdir, getType() + ".txt");
		}

		private boolean conatins(I item) {
			return get(item) != null;
		}

		public ItemStack get(I item) {
			for (ItemStack is : stacks)
				if (is.stackable(item))
					return is;
			return null;
		}

		public void add(I item, BigInteger amt) {
			if (!alive)
				throw new IllegalArgumentException("Cannot perform operation while entry is discarded.");
			ItemStack is = get(item);
			if (is == null)
				is = new ItemStack(item, amt);
			else
				is.add(amt);
		}

		public boolean remove(I item, BigInteger amt) {
			if (!alive)
				throw new IllegalArgumentException("Cannot perform operation while entry is discarded.");
			ItemStack is = get(item);
			if (is == null)
				return false;
			is.remove(amt);
			if (stacks.isEmpty()) {
				entries.remove(item.getItemType());
				entryList.remove(Collections.binarySearch(entryList, item, COMPARATOR));
				getFile().delete();
				alive = false;
			}
			return true;
		}

		private Entry(I item, BigInteger amt) {
			entries.put(item.getItemType(), this);
			entryList.add(-Collections.binarySearch(entryList, item, COMPARATOR) - 1, this);
			new ItemStack(item, amt);
			alive = true;
			save();
		}

		private Entry(File f) {
			for (var jv : (JSONArray) DataUtils.load(f))
				new ItemStack((JSONObject) jv);
			String type = this.stacks.get(0).getType();
			entries.put(type, this);
			entryList.add(-Collections.binarySearch(entryList, type, COMPARATOR) - 1, this);
			alive = true;
			save();
		}

		private void save() {
			if (alive)
				DataUtils.save(new JSONArray(JavaTools.mask(stacks, ItemStack::getProperties)), getFile());
		}

		public final class ItemStack extends PropertyObject implements Comparable<ItemStack> {

			@Override
			protected JSONObject getProperties() {
				return super.getProperties();
			}

			private boolean alive = true;

			public boolean stackable(Item other) {
				return getItem().stackable(other);
			}

			public BigInteger count() {
				return count.get();
			}

			public void remove(BigInteger amt) {
				if (!alive)
					throw new IllegalArgumentException("Cannot perform operation while stack is discarded.");
				if (amt.compareTo(count()) > 0)
					throw new IllegalArgumentException(
							"Cannot remove more items from this stack than there are items in this stack.");
				else
					count.set(count().subtract(amt));
				if (count.get().equals(BigInteger.ZERO)) {
					stacks.remove(this);
					alive = false;
				}
			}

			{
				stacks.add(this);
			}

			private final Property<I> item = toObjectProperty("item", ItemReifier::reify);
			private final Property<BigInteger> count = bigIntegerProperty("count", BigInteger.ONE);

			public void add(BigInteger amount) {
				if (!alive)
					throw new IllegalArgumentException("Cannot perform operation while stack is discarded.");
				count.set(count.get().add(amount));
			}

			public void add(long amount) {
				add(BigInteger.valueOf(amount));
			}

			private ItemStack(I item, BigInteger amount) {
				this.item.set(item);
				count.set(amount);
			}

			private ItemStack(JSONObject json) {
				super(json);
			}

			public I getItem() {
				return item.get();
			}

			public String getType() {
				return getItem().getItemType();
			}

			public BigInteger getCount() {
				return count.get();
			}

			@Override
			public int compareTo(ItemStack o) {
				return getType().compareTo(o.getType());
			}

			@Override
			public void change() {
				super.change();
				save();
			}

			public String getIcon() {
				return getItem().getIcon();
			}

			public String getName() {
				return getItem().getItemName();
			}

		}

		public String getType() {
			return stacks.get(0).getType();
		}

		public String getIcon() {
			return stacks.get(0).getIcon();
		}

		public String getName() {
			return stacks.get(0).getName();
		}

		public List<ItemStack> getStacks() {
			if (!alive)
				throw new IllegalArgumentException("Cannot perform operation while entry is discarded.");
			return stacks;
		}

		public ItemStack get(int index) {
			return stacks.get(index);
		}

		@Override
		public int compareTo(Entry<?> o) {
			return getType().compareTo(o.getType());
		}
	}

}
