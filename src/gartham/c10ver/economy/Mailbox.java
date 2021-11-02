package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.utils.Utilities;

public class Mailbox extends SavablePropertyObject {

	private Map<AbstractMultiplier, Integer> mults = new HashMap<>();
	private BigInteger cloves;

	public Mailbox() {
	}

	public Mailbox(File dir) {
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
		clear();
		File i = new File(rewardsFolder, "items");
		inv.load(i);

		JSONObject rewards = Utilities.loadObj(new File(rewardsFolder, "rewards.txt"));
		if (rewards != null) {
			cloves = new BigInteger(rewards.getString("c"));

			JSONArray a = (JSONArray) rewards.get("m");
			for (var o : a) {
				JSONObject obj = (JSONObject) o;
				AbstractMultiplier m = new AbstractMultiplier(new BigDecimal(obj.getString("a")),
						Duration.parse(obj.getString("d")));
				mults.put(m, obj.getInt("c"));
			}
		}
	}

	public void saveInto(File rewardsFolder) {

	}

	public void add(RewardsOperation... ro) {
		for (var o : ro) {
			o.getItems().putInto(inv);
			for (var e : o.getMults().entrySet())
				mults.put(e.getKey(),
						mults.containsKey(e.getKey()) ? mults.get(e.getKey()) + e.getValue() : e.getValue());
			cloves = cloves.add(o.getRewardedCloves());
		}
	}

	public void clear() {
		inv.clear();
		mults.clear();
		cloves = BigInteger.ZERO;
	}

}
