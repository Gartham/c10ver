package gartham.c10ver.economy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gartham.c10ver.economy.items.ItemBunch;

public class Rewards {
	private final Map<String, ItemBunch<?>> items;
	private final List<Multiplier> multipliers;
	private final BigInteger cloves;

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, List<Multiplier> multipliers) {
		this.items = items instanceof Collection<?> ? new HashMap<>(((Collection<?>) items).size()) : new HashMap<>();
		for (ItemBunch<?> ib : items)
			this.items.put(ib.getItem().getItemType(), ib);
		this.multipliers = multipliers;
		this.cloves = cloves;
	}

	public Rewards(Iterable<ItemBunch<?>> items, BigInteger cloves, Multiplier... multipliers) {
		this.items = items instanceof Collection<?> ? new HashMap<>(((Collection<?>) items).size()) : new HashMap<>();
		for (ItemBunch<?> ib : items)
			this.items.put(ib.getItem().getItemType(), ib);
		this.multipliers = new ArrayList<>(multipliers.length);
		for (Multiplier m : multipliers)
			this.multipliers.add(m);
		this.cloves = cloves;
	}

	public Rewards(Map<String, ItemBunch<?>> items, BigInteger cloves, List<Multiplier> multipliers) {
		this.items = items;
		this.multipliers = multipliers;
		this.cloves = cloves;
	}

	public Rewards(Map<String, ItemBunch<?>> items, BigInteger cloves, Multiplier... multipliers) {
		this.items = items;
		this.multipliers = new ArrayList<>(multipliers.length);
		for (Multiplier m : multipliers)
			this.multipliers.add(m);
		this.cloves = cloves;
	}

	public Map<String, ItemBunch<?>> getItems() {
		return Collections.unmodifiableMap(items);
	}
	
	public List<ItemBunch<?>> getItemList(){
		return new ArrayList<>(items.values());
	}

	public List<Multiplier> getMultipliers() {
		return Collections.unmodifiableList(multipliers);
	}

	public BigInteger getCloves() {
		return cloves;
	}

}
