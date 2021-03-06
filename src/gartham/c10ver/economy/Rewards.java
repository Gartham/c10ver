package gartham.c10ver.economy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.ItemBunch;

public class Rewards {
	private final Inventory items;
	private final Map<AbstractMultiplier, Integer> multipliers;
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

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, Iterable<AbstractMultiplier> multipliers) {
		if (items == null)
			this.items = null;
		else {
			Inventory inv = new Inventory();
			for (var x : items)
				inv.add(x);
			this.items = inv.getEntryCount() == 0 ? null : inv;
		}
		this.multipliers = multipliers == null ? null : JavaTools.frequencyMap(multipliers);
		this.cloves = cloves == null || cloves.compareTo(BigInteger.ZERO) == 0 ? null : cloves;
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, AbstractMultiplier... multipliers) {
		this(items, cloves, toList(multipliers));
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves) {
		this(items, cloves, new AbstractMultiplier[0]);
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves) {
		this(items, cloves, new AbstractMultiplier[0]);
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves, List<AbstractMultiplier> multipliers) {
		this(toList(items), cloves, multipliers);
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves, AbstractMultiplier... multipliers) {
		this(toList(items), cloves, multipliers);
	}

	public Rewards(ItemBunch<?>... items) {
		this(toList(items), null, (List<AbstractMultiplier>) null);
	}

	public Rewards(Iterable<ItemBunch<?>> items) {
		this(items, null, (List<AbstractMultiplier>) null);
	}

	public Rewards(AbstractMultiplier... items) {
		this((Iterable<ItemBunch<?>>) null, null, toList(items));
	}

	public Rewards(BigInteger cloves) {
		this((Iterable<ItemBunch<?>>) null, cloves, (List<AbstractMultiplier>) null);
	}

	private Rewards(Inventory inventory, BigInteger cloves, List<AbstractMultiplier> mults) {
		items = inventory == null || inventory.getEntryCount() == 0 ? null : inventory;
		this.cloves = cloves == null ? BigInteger.ZERO : cloves;
		multipliers = mults == null || mults.isEmpty() ? null : JavaTools.frequencyMap(mults);
	}

	private Rewards(Inventory inventory, BigInteger cloves, Map<AbstractMultiplier, Integer> mults) {
		items = inventory == null || inventory.getEntryCount() == 0 ? null : inventory;
		this.cloves = cloves == null ? BigInteger.ZERO : cloves;
		multipliers = mults == null || mults.isEmpty() ? null : mults;
	}

	public Rewards() {
		this(BigInteger.ZERO);
	}

	public boolean hasItems() {
		return items != null;
	}

	/**
	 * Modifiable list containing all the items. Modifying the list does not affect
	 * this object.
	 * 
	 * @return The items, contained in bunches, as a list.
	 */
	public List<ItemBunch<?>> getItemsAsList() {
		if (items == null)
			return List.of();
		List<ItemBunch<?>> ibs = new ArrayList<>();
		for (var e : items)
			for (var is : e)
				ibs.add(is.toItemBunch());
		return ibs;
	}

	/**
	 * Unmodifiable map of multipliers.
	 * 
	 * @return The multipliers, as a frequency map.
	 */
	public Map<AbstractMultiplier, Integer> getMultipliers() {
		return multipliers == null ? Map.of() : Collections.unmodifiableMap(multipliers);
	}

	public boolean hasMultipliers() {
		return multipliers != null;
	}

	public BigInteger getCloves() {
		return cloves == null ? BigInteger.ZERO : cloves;
	}

	private static <K> void arithmeticAdd(Map<? super K, Integer> first, Map<? extends K, Integer> second) {
		for (var e : second.entrySet())
			first.put(e.getKey(), first.containsKey(e.getKey()) ? first.get(e.getKey()) + e.getValue() : e.getValue());
	}

	public static Rewards combine(Rewards... others) {
		Inventory res = new Inventory();
		Map<AbstractMultiplier, Integer> mults = new HashMap<>();
		BigInteger cloves = BigInteger.ZERO;
		for (var r : others) {
			if (r.hasItems())
				r.items.putInto(res);
			if (r.hasMultipliers())
				arithmeticAdd(mults, r.multipliers);
			if (r.hasCloves())
				cloves = cloves.add(r.cloves);
		}
		return new Rewards(res, cloves, mults);
	}

	public Rewards with(Rewards... others) {
		var arr = Arrays.copyOf(others, others.length + 1);
		arr[arr.length - 1] = this;
		return combine(arr);
	}

}
