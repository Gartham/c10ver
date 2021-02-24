package gartham.c10ver.economy.items.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.economy.items.Item;

public class ItemList {

	private final List<Entry<?>> items = new ArrayList<>();

	public class Entry<I extends Item> {
		private final I item;
		private final BigInteger count;

		public Entry(I item, BigInteger count) {
			this.item = item;
			this.count = count;
		}

		public I getItem() {
			return item;
		}

		public BigInteger getCount() {
			return count;
		}
	}

	public List<Entry<?>> getItems() {
		return items;
	}

}
