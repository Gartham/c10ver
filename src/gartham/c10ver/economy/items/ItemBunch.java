package gartham.c10ver.economy.items;

import java.math.BigInteger;

import gartham.c10ver.utils.Bunch;

public class ItemBunch<I extends Item> extends Bunch<I> {

	public I getItem() {
		return getValue();
	}

	public ItemBunch(I item, BigInteger count) {
		super(item, count);
	}

	public static <I extends Item> ItemBunch<I> of(I item, BigInteger amount) {
		return new ItemBunch<I>(item, amount);
	}

	public static <I extends Item> ItemBunch<I> of(I item) {
		return of(item, BigInteger.ONE);
	}

}
