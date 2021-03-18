package gartham.c10ver.economy.items.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.ItemBunch;

public class ItemList {

	private final List<Entry<?>> items = new ArrayList<>();

	public class Entry<I extends Item> {
		private final I item;
		private BigInteger count;

		private Entry(I item, BigInteger count) {
			this.item = item;
			this.count = count;
		}

		public I getItem() {
			return item;
		}

		public BigInteger getCount() {
			return count;
		}

		public void setCount(BigInteger count) {
			this.count = count;
		}

		public ItemBunch<I> toItemBunch() {
			return new ItemBunch<>(item, count);
		}

	}

	@SuppressWarnings("unchecked")
	public <I extends Item> Entry<I> get(I i) {
		for (var e : items)
			if (i.stackable(e.getItem()))
				return (Entry<I>) e;
		return null;
	}

	public <I extends Item> Entry<I> add(I i, BigInteger count) {
		if (count.compareTo(BigInteger.ZERO) < 0)
			throw new IllegalArgumentException("Value cannot be below 0.");
		var e = get(i);
		if (e != null)
			e.setCount(e.getCount().add(count));
		else
			items.add(e = new Entry<>(i, count));
		return e;
	}

	public BigInteger remove(Item i, BigInteger count) {
		for (Iterator<Entry<?>> iterator = items.iterator(); iterator.hasNext();) {
			var e = iterator.next();
			if (i.stackable(e.getItem())) {
				e.setCount(e.getCount().subtract(count));
				if (e.getCount().compareTo(BigInteger.ZERO) <= 0) {
					iterator.remove();
					return count.add(e.getCount());
				}
				return count;
			}
		}
		return BigInteger.ZERO;
	}

	public void clear(Item i) {
		for (Iterator<Entry<?>> iterator = items.iterator(); iterator.hasNext();) {
			var e = iterator.next();
			if (i.stackable(e.getItem())) {
				iterator.remove();
				return;
			}
		}
	}

	public List<Entry<?>> getItems() {
		return items;
	}

	public Iterable<ItemBunch<?>> getItemBunches() {
		return JavaTools.mask(getItems(), Entry::toItemBunch);
	}

}
