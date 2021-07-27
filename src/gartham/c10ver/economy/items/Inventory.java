package gartham.c10ver.economy.items;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.Inventory.Entry.ItemStack;
import gartham.c10ver.utils.Utilities;

/**
 * A compressed way of storing items.
 * 
 * @author Gartham
 *
 */
public class Inventory implements Cloneable, Iterable<Entry<?>> {

	public void clear() {
		entries.clear();
		entryList.clear();
	}

	/**
	 * Clears this {@link Inventory} then loads it from the specified directory, if
	 * the dir has any files. If it does not, this method is equivalent to
	 * {@link #clear()}. If it does, this method will result in an error if any of
	 * the files are not interpretable as an {@link Entry}.
	 * 
	 * @param dir The {@link File directory} to save to.
	 */
	public void load(File dir) throws RuntimeException {
		clear();
		File[] files = dir.listFiles();
		if (files != null) {
			RuntimeException ex = null;
			for (File type : files)
				try {
					newEntry(type);
				} catch (RuntimeException e) {
					if (ex == null)
						ex = e;
					else
						ex.addSuppressed(e);
				}
			if (ex != null)
				throw ex;
		}
	}

	private Map<String, Entry<?>> entries = new HashMap<>();
	private List<Entry<?>> entryList = new ArrayList<>();

	protected <I extends Item> Entry<I> newEntry(File file) {
		return new Entry<>(file);
	}

	protected <I extends Item> Entry<I> newEntry(I item, BigInteger amount) {
		return new Entry<>(item, amount);
	}

	/**
	 * Gets a page of entries in this {@link UserInventory}. The number of entries
	 * per page is specified by the <code>pagesize</code> argument, and the page is
	 * specified by the <code>page</code> argument. <code>null</code> is returned,
	 * in lieu of an empty list, if the page number is invalid. The only scenario in
	 * which the returned list is empty is when the 1st page is requested (the value
	 * of the <code>page</code> argument is 1) but there are no entries in this
	 * {@link UserInventory}.
	 * 
	 * @param page     The page to return.
	 * @param pagesize The maximum number of elements that can be returned in this
	 *                 page.
	 * @return A new, unmodifiable list containing the entries that belong to the
	 *         specified page.
	 */
	public List<? extends Entry<?>> getPage(int page, int pagesize) {
		List<Entry<?>> res = JavaTools.paginate(page, pagesize, entryList);
		return res == null ? null : Collections.unmodifiableList(res);
	}

	public int maxPage(int pagesize) {
		return JavaTools.maxPage(pagesize, entryList);
	}

	private static String res(Object o) {
		if (o instanceof String)
			return (String) o;
		else if (o instanceof Entry<?>)
			return ((Entry<?>) o).getType();
		else if (o instanceof ItemStack)
			return ((Entry<?>.ItemStack) o).getType();
		else if (o instanceof Item)
			return ((Item) o).getItemType();
		else
			throw new IllegalArgumentException();
	}

	private static final Comparator<Object> COMPARATOR = (o1, o2) -> res(o1).compareTo(res(o2));

	@SuppressWarnings("unchecked")
	public <I extends Item> Entry<I> add(I item, BigInteger amt) {
		Entry<I> entry;
		if (entries.containsKey(item.getItemType())) {
			entry = (Entry<I>) entries.get(item.getItemType());
			entry.add(item, amt);
		} else
			entries.put(item.getItemType(), entry = newEntry(item, amt));
		return entry;
	}

	public <I extends Item> Entry<I> add(I item) {
		return add(item, BigInteger.ONE);
	}

	public Set<? extends Entry<?>> add(ItemBunch<?>... items) {
		Set<Entry<?>> res = new HashSet<>();
		for (ItemBunch<?> ib : items)
			res.add(add(ib));
		return res;
	}

	public Set<? extends Entry<?>> add(Iterator<ItemBunch<?>> items) {
		Set<Entry<?>> res = new HashSet<>();
		while (items.hasNext())
			res.add(add(items.next()));
		return res;
	}

	public Set<? extends Entry<?>> add(Iterable<ItemBunch<?>> items) {
		return add(items.iterator());
	}

	public <I extends Item> Entry<I> add(ItemBunch<? extends I> items) {
		return add(items.getItem(), items.getCount());
	}

	@SuppressWarnings("unchecked")
	public <I extends Item> boolean remove(I item, BigInteger amt) {
		return entries.containsKey(item.getItemType()) ? ((Entry<I>) entries.get(item.getItemType())).remove(item, amt)
				: false;
	}

	public void saveAll(File inventoryRoot) {
		for (Entry<?> e : entryList)
			e.saveInto(inventoryRoot);
	}

	/**
	 * Returns the {@link Entry entries} in this {@link UserInventory}. This
	 * {@link List} should not be modified directly, although the items in this list
	 * can be modified using their appropriate methods.
	 * 
	 * @return The entries in this {@link UserInventory}.
	 */
	public List<? extends Entry<?>> getEntries() {
		return entryList;
	}

	public Entry<?> get(int index) {
		return entryList.get(index);
	}

	public Entry<?> get(String key) {
		return entries.get(key);
	}

	public Entry<?> get(Item item) {
		return get(item.getItemType());
	}

	public int getEntryCount() {
		return entryList.size();
	}

	public BigInteger getTotalItemCount() {
		BigInteger bi = BigInteger.ZERO;
		for (Entry<?> e : entryList)
			bi = bi.add(e.getTotalCount());
		return bi;
	}

	public class Entry<I extends Item> implements Comparable<Entry<?>>, Iterable<Entry<I>.ItemStack> {
		protected final List<ItemStack> stacks = new ArrayList<>(1);// The different stacks of this type of item.
		protected boolean alive = false;

		public Entry<I> cloneTo(Inventory other) {
			var e = other.new Entry<I>();
			for (var is : stacks)
				is.cloneTo(e);
			other.entries.put(e.stacks.get(0).getItem().getItemType(), e);
			other.entryList.add(-Collections.binarySearch(entryList, e.getStacks().get(0).getItem(), COMPARATOR) - 1,
					e);
			return e;
		}

		public BigInteger getTotalCount() {
			BigInteger bi = BigInteger.ZERO;
			for (ItemStack is : stacks)
				bi = bi.add(is.count());
			return bi;
		}

		public List<? extends ItemStack> getPage(int page, int pagesize) {
			return JavaTools.paginate(page, pagesize, stacks);
		}

		/**
		 * Returns the {@link File} that this {@link Entry} would be stored at if the
		 * provided {@link File} is the directory representing this {@link Entry}'s
		 * {@link Inventory}.
		 * 
		 * @param inv The {@link File} where this {@link Entry}'s {@link Inventory} is.
		 * @return The {@link File} representing this {@link Entry} inside its
		 *         {@link Inventory}.
		 */
		public File getFile(File inv) {
			return new File(inv, getType() + ".txt");
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

		protected ItemStack newItemStack(I item, BigInteger amt) {
			return new ItemStack(item, amt);
		}

		protected ItemStack newItemStack(JSONObject json) {
			return new ItemStack(json);
		}

		public void add(I item, BigInteger amt) {
			if (!alive)
				throw new IllegalStateException("Cannot perform operation while entry is discarded.");
			ItemStack is = get(item);
			if (is == null)
				is = newItemStack(item, amt);
			else
				is.add(amt);
		}

		/**
		 * Removes the specified amount of the specified {@link Item} from this
		 * {@link Entry}. Returns <code>false</code> if the provided {@link Item} is not
		 * in this {@link Entry} to begin with. Otherwise, performs the removal and
		 * returns <code>true</code>.
		 * 
		 * @param item The type of {@link Item} to be removed.
		 * @param amt  The amount to remove.
		 * @return <code>true</code> if the item was contained in this {@link Entry}
		 *         before the call to this method.
		 */
		public boolean remove(I item, BigInteger amt) {
			if (!alive)
				throw new IllegalStateException("Cannot perform operation while entry is discarded.");
			ItemStack is = get(item);
			if (is == null)
				return false;
			is.remove(amt);// Does removal and any necessary cleanup.
			return true;
		}

		public boolean has(I item, BigInteger amt) {
			if (!alive)
				throw new IllegalStateException("Cannot perform operation while entry is discarded.");
			var is = get(item);
			return is != null && is.count().compareTo(amt) >= 0;
		}

		/**
		 * Called by an {@link ItemStack} to remove this {@link Entry} when this
		 * {@link Entry} no longer has any {@link ItemStack}s in it.
		 * 
		 * @param is The {@link ItemStack} containing the item type to use to remove
		 *           this entry.
		 */
		protected void remove(ItemStack is) {
			entries.remove(is.getItem().getItemType());
			entryList.remove(JavaTools.binarySearch(is.getItem(), entryList, COMPARATOR));
			alive = false;
		}

		protected Entry(I item, BigInteger amt) {
			entries.put(item.getItemType(), this);
			entryList.add(-Collections.binarySearch(entryList, item, COMPARATOR) - 1, this);
			newItemStack(item, amt);
			alive = true;
		}

		protected Entry(File f) {
			for (var jv : (JSONArray) Utilities.load(f))
				newItemStack((JSONObject) jv);
			if (stacks.isEmpty())
				throw new IllegalArgumentException("Invalid file. No stacks found in an entry. File: " + f);
			String type = this.stacks.get(0).getType();
			entries.put(type, this);
			entryList.add(-Collections.binarySearch(entryList, type, COMPARATOR) - 1, this);
			alive = true;
		}

		/**
		 * Used for cloning.
		 */
		private Entry() {
		}

		public void save(File file) {
			if (alive)
				Utilities.save(new JSONArray(JavaTools.mask(stacks, ItemStack::toJSON)), file);
		}

		public void saveInto(File inventoryRoot) {
			save(getFile(inventoryRoot));
		}

		public class ItemStack extends PropertyObject implements Comparable<ItemStack> {

			public ItemBunch<I> toItemBunch() {
				return new ItemBunch<>(getItem(), getCount());
			}

			public boolean has(BigInteger amt) {
				return getCount().compareTo(amt) >= 0;
			}

			public Entry<I>.ItemStack cloneTo(Entry<I> other) {
				if (!alive)
					throw new IllegalStateException();
				return other.newItemStack(getItem(), getCount());
			}

			protected boolean alive = true;

			/**
			 * <p>
			 * Saves this {@link ItemStack} to the specified {@link File} in the format
			 * delineated by {@link Inventory}.
			 * </p>
			 * <p>
			 * Note: This method actually just calls {@link Entry#save(File)
			 * Entry.this.save(File)}.
			 * </p>
			 * 
			 * @param file The {@link File} to save the {@link ItemStack} to.
			 */
			public void save(File file) {
				Entry.this.save(file);
			}

			public void saveInto(File inventoryRoot) {
				Entry.this.saveInto(inventoryRoot);
			}

			public boolean stackable(Item other) {
				return getItem().stackable(other);
			}

			public BigInteger count() {
				return count.get();
			}

			public String getEffectiveName() {
				return getCustomName() == null ? getName() : getCustomName();
			}

			public String getCustomName() {
				return item.get().getCustomName();
			}

			public ItemStack remove(BigInteger amt) {
				if (!alive)
					throw new IllegalStateException("Cannot perform operation while stack is discarded.");
				if (amt.compareTo(count()) > 0)
					throw new IllegalArgumentException(
							"Cannot remove more items from this stack than there are items in this stack.");
				else
					count.set(count().subtract(amt));
				if (count.get().equals(BigInteger.ZERO)) {
					if (stacks.size() == 1 && stacks.contains(this))
						Entry.this.remove(this);
					stacks.remove(this);
					alive = false;
					return null;
				}
				return this;
			}

			{
				stacks.add(this);
			}

			protected final Property<I> item = toObjectProperty("item", ItemReifier::reify);
			protected final Property<BigInteger> count = bigIntegerProperty("count", BigInteger.ONE);

			public void add(BigInteger amount) {
				if (!alive)
					throw new IllegalStateException("Cannot perform operation while stack is discarded.");
				count.set(count.get().add(amount));
			}

			public void add(long amount) {
				add(BigInteger.valueOf(amount));
			}

			protected ItemStack(I item, BigInteger amount) {
				this.item.set(item);
				count.set(amount);
			}

			protected ItemStack(JSONObject json) {
				load(item, json);
				load(count, json);
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

		public List<? extends ItemStack> getStacks() {
			if (!alive)
				throw new IllegalStateException("Cannot perform operation while entry is discarded.");
			return stacks;
		}

		public ItemStack get(int index) {
			return stacks.get(index);
		}

		@Override
		public int compareTo(Entry<?> o) {
			return getType().compareTo(o.getType());
		}

		@Override
		public Iterator<Entry<I>.ItemStack> iterator() {
			return stacks.iterator();
		}
	}

	public void cloneTo(Inventory inv) {
		for (var e : entryList)
			e.cloneTo(inv);
	}

	@Override
	public Inventory clone() throws CloneNotSupportedException {
		var i = (Inventory) super.clone();
		i.entries = new HashMap<>(entries.size());
		i.entryList = new ArrayList<>(entryList.size());
		cloneTo(i);
		return i;
	}

	public Inventory copy() {
		Inventory i = new Inventory();
		cloneTo(i);
		return i;
	}

	@Override
	public Iterator<Entry<?>> iterator() {
		return entryList.iterator();
	}

	public void putInto(Inventory other) {
		for (var e : this)
			other.add(JavaTools.mask(e, ItemStack::toItemBunch));
	}

}
