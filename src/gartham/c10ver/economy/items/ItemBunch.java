package gartham.c10ver.economy.items;

import java.math.BigInteger;

public class ItemBunch<I extends Item> {
	private final I item;
	private final BigInteger count;

	public BigInteger getCount() {
		return count;
	}

	public I getItem() {
		return item;
	}

	public ItemBunch(I item, BigInteger count) {
		this.item = item;
		this.count = count;
	}

	public static <I extends Item> ItemBunch<I> of(I item, BigInteger amount) {
		return new ItemBunch<I>(item, amount);
	}

	public static <I extends Item> ItemBunch<I> of(I item) {
		return of(item, BigInteger.ONE);
	}
	
}
