package gartham.c10ver.economy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import gartham.c10ver.economy.items.ItemBunch;

public class Rewards {
	private final List<ItemBunch<?>> items;
	private final List<Multiplier> multipliers;
	private final BigInteger cloves;

	public boolean hasCloves() {
		return cloves != null && cloves.compareTo(BigInteger.ZERO) != 0;
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, List<Multiplier> multipliers) {
		this.items = items instanceof Collection<?> ? new ArrayList<>(((Collection<?>) items).size())
				: new ArrayList<>();
		for (ItemBunch<?> ib : items)
			this.items.add(ib);
		this.multipliers = multipliers;
		this.cloves = cloves;
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, Multiplier... multipliers) {
		this.items = items instanceof Collection<?> ? new ArrayList<>(((Collection<?>) items).size())
				: new ArrayList<>();
		for (ItemBunch<?> ib : items)
			this.items.add(ib);
		this.multipliers = new ArrayList<>(multipliers.length);
		for (Multiplier m : multipliers)
			this.multipliers.add(m);
		this.cloves = cloves;
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves) {
		this.items = items instanceof Collection<?> ? new ArrayList<>(((Collection<?>) items).size())
				: new ArrayList<>();
		for (ItemBunch<?> ib : items)
			this.items.add(ib);
		this.multipliers = Collections.emptyList();
		this.cloves = cloves;
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves) {
		this.items = new ArrayList<>(items.length);
		for (ItemBunch<?> ib : items)
			this.items.add(ib);
		this.multipliers = Collections.emptyList();
		this.cloves = cloves;
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves, List<Multiplier> multipliers) {
		this.items = new ArrayList<>(items.length);
		for (ItemBunch<?> ib : items)
			this.items.add(ib);
		this.multipliers = multipliers;
		this.cloves = cloves;
	}

	public Rewards(ItemBunch<?>[] items, BigInteger cloves, Multiplier... multipliers) {
		this.items = new ArrayList<>(items.length);
		for (ItemBunch<?> ib : items)
			this.items.add(ib);
		this.multipliers = new ArrayList<>(multipliers.length);
		for (Multiplier m : multipliers)
			this.multipliers.add(m);
		this.cloves = cloves;
	}

	public boolean hasItems() {
		return !items.isEmpty();
	}

	public List<ItemBunch<?>> getItemsAsList() {
		return Collections.unmodifiableList(items);
	}

	public List<Multiplier> getMultipliers() {
		return Collections.unmodifiableList(multipliers);
	}

	public boolean hasMultipliers() {
		return !multipliers.isEmpty();
	}

	public BigInteger getCloves() {
		return cloves;
	}

}
