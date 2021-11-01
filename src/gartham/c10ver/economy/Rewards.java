package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.utils.Utilities;

public class Rewards extends PropertyObject {

	private Inventory items;
	private Map<AbstractMultiplier, Integer> multipliers;
	private BigInteger cloves;

	protected void setInventory(Inventory n) {
		items = n;
	}

	protected Inventory getItemsModifiable() {
		return items;
	}

	protected Map<AbstractMultiplier, Integer> getMultipliersModifiable() {
		return multipliers;
	}

	protected void setItems(Inventory items) {
		this.items = items;
	}

	protected void setCloves(BigInteger cloves) {
		this.cloves = cloves;
	}

	protected void setMultipliers(Map<AbstractMultiplier, Integer> multipliers) {
		this.multipliers = multipliers;
	}

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

	protected Rewards(Inventory inventory, BigInteger cloves, List<AbstractMultiplier> mults) {
		items = inventory == null || inventory.getEntryCount() == 0 ? null : inventory;
		this.cloves = cloves == null ? BigInteger.ZERO : cloves;
		multipliers = mults == null || mults.isEmpty() ? null : JavaTools.frequencyMap(mults);
	}

	protected Rewards(Inventory inventory, BigInteger cloves, Map<AbstractMultiplier, Integer> mults) {
		items = inventory == null || inventory.getEntryCount() == 0 ? null : inventory;
		this.cloves = cloves == null ? BigInteger.ZERO : cloves;
		multipliers = mults == null || mults.isEmpty() ? null : mults;
	}

	public Rewards() {
		this(BigInteger.ZERO);
	}

	public boolean hasItems() {
		return items != null || items.isEmpty();
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
		return multipliers != null || multipliers.isEmpty();
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

	/**
	 * Saves this {@link Rewards} object into the provided folder. The folder
	 * logically represents the {@link Rewards} object.
	 * 
	 * @param rewardsFolder The folder that will be the serialized {@link Rewards}
	 *                      object.
	 */
	public void save(File rewardsFolder) {
		saveInventory(rewardsFolder);
		saveClovesAndMults(rewardsFolder);
	}

	/**
	 * Saves just the inventory part of this {@link Rewards}. Should be called with
	 * the same folder that would be provided when calling {@link #save(File)}.
	 * 
	 * @param rewardsFolder The folder in which (only part) of this {@link Rewards}
	 *                      will be saved. Calling {@link #saveClovesAndMults(File)}
	 *                      with the same folder after (or before) a call to this
	 *                      method will be equivalent to calling
	 *                      {@link #save(File)}.
	 */
	public void saveInventory(File rewardsFolder) {
		File i = new File(rewardsFolder, "items");
		i.mkdirs();
		items.saveAll(i);
	}

	/**
	 * Saves just the rewards and cloves part of this {@link Rewards}. Should be
	 * called with the same folder that would be provided when calling
	 * {@link #save(File)}.
	 * 
	 * @param rewardsFolder The folder in which (only part) of this {@link Rewards}
	 *                      will be saved. Calling {@link #saveInventory(File)} with
	 *                      the same folder after (or before) a call to this method
	 *                      will be equivalent to calling {@link #save(File)}.
	 */
	public void saveClovesAndMults(File rewardsFolder) {
		File rew = new File(rewardsFolder, "rewards.txt");
		JSONObject rewards = new JSONObject();
		rewards.put("c", cloves.toString());
		JSONArray mults = new JSONArray(
				JavaTools.mask(multipliers.entrySet(), (Function<Entry<AbstractMultiplier, Integer>, JSONValue>) t -> {
					JSONObject o = new JSONObject();
					o.put("a", t.getKey().getAmt().toString());
					o.put("c", t.getValue());
					o.put("d", t.getKey().getDuration().toString());
					return o;
				}));
		rewards.put("m", mults);

		Utilities.save(rewards, rew);
	}

	/**
	 * Loads a {@link Rewards} object out of the provided folder. This is the
	 * symmetric counterpart of {@link #save(File)}.
	 * 
	 * @param rewardsFolder The folder that will be read from.
	 * @return The loaded {@link Rewards} object.
	 */
	public static Rewards load(File rewardsFolder) {
		File i = new File(rewardsFolder, "items");
		Inventory inv = new Inventory();
		if (i.isDirectory())
			inv.load(i);

		JSONObject rewards = Utilities.loadObj(new File(rewardsFolder, "rewards.txt"));
		BigInteger cloves;
		Map<AbstractMultiplier, Integer> multipliers;
		if (rewards != null) {
			cloves = new BigInteger(rewards.getString("c"));

			JSONArray a = (JSONArray) rewards.get("m");
			multipliers = new HashMap<>();
			for (var o : a) {
				JSONObject obj = (JSONObject) o;
				AbstractMultiplier m = new AbstractMultiplier(new BigDecimal(obj.getString("a")),
						Duration.parse(obj.getString("d")));
				multipliers.put(m, obj.getInt("c"));
			}
		} else {
			cloves = BigInteger.ZERO;
			multipliers = new HashMap<>();
		}

		return new Rewards(inv, cloves, multipliers);
	}

	public boolean isEmpty() {
		return items.isEmpty() && multipliers.isEmpty() && cloves.equals(BigInteger.ZERO);
	}

}
