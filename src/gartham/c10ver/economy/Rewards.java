package gartham.c10ver.economy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gartham.c10ver.economy.items.ItemBunch;

public class Rewards {
	private final List<ItemBunch<?>> items;
	private final List<Multiplier> multipliers;
	private final BigInteger cloves;

	public boolean hasCloves() {
		return cloves != null;
	}

	private static <T> List<T> toList(T[] arr) {
		if (arr == null || arr.length == 0)
			return null;
		else {
			List<T> x = new ArrayList<>(arr.length);
			for (var t : arr)
				x.add(t);
			return x;
		}
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, Iterable<Multiplier> multipliers) {
		if (items == null)
			this.items = null;
		else {
			List<ItemBunch<?>> is = new ArrayList<>();
			for (var x : items)
				is.add(x);
			this.items = is.isEmpty() ? null : is;
		}
		if (multipliers == null)
			this.multipliers = null;
		else {
			List<Multiplier> mults = new ArrayList<>();
			for (var x : multipliers)
				mults.add(x);
			this.multipliers = mults.isEmpty() ? null : mults;
		}
		this.cloves = cloves == null || cloves.compareTo(BigInteger.ZERO) == 0 ? null : cloves;
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, Multiplier... multipliers) {
		this(items, cloves, toList(multipliers));
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves) {
		this(items, cloves, new Multiplier[0]);
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves) {
		this(items, cloves, new Multiplier[0]);
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves, List<Multiplier> multipliers) {
		this(toList(items), cloves, multipliers);
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves, Multiplier... multipliers) {
		this(toList(items), cloves, multipliers);
	}

	public Rewards(ItemBunch<?>... items) {
		this(toList(items), null, (List<Multiplier>) null);
	}

	public Rewards(Iterable<ItemBunch<?>> items) {
		this(items, null, (List<Multiplier>) null);
	}

	public Rewards(Multiplier... items) {
		this((Iterable<ItemBunch<?>>) null, null, toList(items));
	}

	public Rewards(BigInteger cloves) {
		this((Iterable<ItemBunch<?>>) null, cloves, (List<Multiplier>) null);
	}

	public boolean hasItems() {
		return items != null;
	}

	public List<ItemBunch<?>> getItemsAsList() {
		return items == null ? List.of() : Collections.unmodifiableList(items);
	}

	public List<Multiplier> getMultipliers() {
		return multipliers == null ? List.of() : Collections.unmodifiableList(multipliers);
	}

	public boolean hasMultipliers() {
		return multipliers != null;
	}

	public BigInteger getCloves() {
		return cloves == null ? BigInteger.ZERO : cloves;
	}

	public static Rewards combine(Rewards... others) {
		List<ItemBunch<?>> items = new ArrayList<>();
		List<Multiplier> mults = new ArrayList<>();
		BigInteger cloves = BigInteger.ZERO;
		for (var r : others) {
			items.addAll(r.items);
			mults.addAll(r.multipliers);
			cloves = cloves.add(r.cloves);
		}
		return new Rewards(items, cloves, mults);
	}

	public Rewards with(Rewards... others) {
		var arr = Arrays.copyOf(others, others.length + 1);
		arr[arr.length - 1] = this;
		return combine(arr);
	}

}
