package gartham.c10ver.economy;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.users.EconomyUser;

/**
 * Represents an operation of rewarding a user. Instances of this class can be
 * used to reward an {@link EconomyUser}.
 * 
 * @author Gartham
 *
 */
public class RewardsOperation {

	/**
	 * The items to give the user.
	 */
	private final Inventory items = new Inventory();
	/**
	 * The cloves to give the user.
	 */
	private BigInteger cloves;
	/**
	 * The multipliers to give the user.
	 */
	private final Map<AbstractMultiplier, Integer> mults = new HashMap<>();
	/**
	 * Whether to use the {@link #mults} earned as a result of this
	 * {@link RewardsOperation} on the {@link #cloves} earned in this operation.
	 * This is <code>true</code> by default. If this value is <code>true</code>, the
	 * multipliers granted to the user when rewards are bestowed are stacked
	 * (additively) with the personal multipliers for the user.
	 */
	private boolean applyEarnedMultipliers = true;

	public BigInteger getCloves() {
		return cloves;
	}

	public void setCloves(BigInteger cloves) {
		this.cloves = cloves;
	}

	public boolean isApplyEarnedMultipliers() {
		return applyEarnedMultipliers;
	}

	public void setApplyEarnedMultipliers(boolean applyEarnedMultipliers) {
		this.applyEarnedMultipliers = applyEarnedMultipliers;
	}

	public Inventory getItems() {
		return items;
	}

	public Map<AbstractMultiplier, Integer> getMults() {
		return mults;
	}

}
