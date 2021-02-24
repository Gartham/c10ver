package gartham.c10ver.economy.items.utils;

import java.math.BigInteger;

import gartham.c10ver.economy.items.Item;

public class ItemList {
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
}
