package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.utils.Utilities;

public class MutableRewards extends Rewards {

	@Override
	public void setCloves(BigInteger cloves) {
		super.setCloves(cloves);
	}

	public void addCloves(BigInteger cloves) {
		super.setCloves(getCloves().add(cloves));
	}

	@Override
	public void setInventory(Inventory n) {
		super.setInventory(n);
	}

	@Override
	public Inventory getItemsModifiable() {
		return super.getItemsModifiable();
	}

	@Override
	public Map<AbstractMultiplier, Integer> getMultipliersModifiable() {
		return super.getMultipliersModifiable();
	}

	public MutableRewards() {
	}

	public MutableRewards(AbstractMultiplier... items) {
		super(items);
	}

	public MutableRewards(BigInteger cloves) {
		super(cloves);
	}

	public MutableRewards(ItemBunch<?>... items) {
		super(items);
	}

	public MutableRewards(ItemBunch<?>[] items, BigInteger cloves, AbstractMultiplier... multipliers) {
		super(items, cloves, multipliers);
	}

	public MutableRewards(ItemBunch<?>[] items, BigInteger cloves, List<AbstractMultiplier> multipliers) {
		super(items, cloves, multipliers);
	}

	public MutableRewards(ItemBunch<?>[] items, BigInteger cloves) {
		super(items, cloves);
	}

	public MutableRewards(Iterable<ItemBunch<?>> items, BigInteger cloves, AbstractMultiplier... multipliers) {
		super(items, cloves, multipliers);
	}

	public MutableRewards(Iterable<ItemBunch<?>> items, BigInteger cloves, Iterable<AbstractMultiplier> multipliers) {
		super(items, cloves, multipliers);
	}

	public MutableRewards(Iterable<ItemBunch<?>> items, BigInteger cloves) {
		super(items, cloves);
	}

	public MutableRewards(Iterable<ItemBunch<?>> items) {
		super(items);
	}

	public MutableRewards(File dir) {
		loadFrom(dir);
	}

	/**
	 * Updates this {@link Rewards} to represent the {@link Rewards} saved into the
	 * specified folder. This is the instance-method symmetric counterpart of
	 * {@link #save(File)}.
	 * 
	 * @param rewardsFolder The folder that will be read from.
	 */
	public void loadFrom(File rewardsFolder) {
		File i = new File(rewardsFolder, "items");
		Inventory inv = new Inventory();
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

		setInventory(inv);
		setCloves(cloves);
		setMultipliers(multipliers);
	}

	public void subsume(Rewards... others) {
		for (var r : others) {
			r.getItemsModifiable().putInto(getItemsModifiable());
			for (var e : r.getMultipliersModifiable().entrySet())
				getMultipliersModifiable().put(e.getKey(),
						getMultipliersModifiable().containsKey(e.getKey())
								? e.getValue() + getMultipliersModifiable().get(e.getKey())
								: e.getValue());
			setCloves(getCloves().add(r.getCloves()));
		}
	}

	public void clear() {
		getItemsModifiable().clear();
		setCloves(BigInteger.ZERO);
		getMultipliersModifiable().clear();
	}

}
