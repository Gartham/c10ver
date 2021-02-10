package gartham.c10ver.economy.items;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import gartham.c10ver.data.PropertyObject;

public class Inventory {

	private final Map<String, List<ItemStack<? extends Item>>> entries = new HashedMap<>();

	private static class ItemStack<I extends Item> extends PropertyObject {

		private final Property<I> item = toObjectProperty("item", null);// TODO Finish
		private final Property<BigInteger> count = bigIntegerProperty("count", BigInteger.ONE);

		public I getItem() {
			return item.get();
		}

		public String getType() {
			return getItem().getItemType();
		}

		public BigInteger getCount() {
			return count.get();
		}

	}

}
