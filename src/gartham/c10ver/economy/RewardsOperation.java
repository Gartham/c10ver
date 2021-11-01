package gartham.c10ver.economy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import gartham.c10ver.economy.items.Inventory;

/**
 * Represents a single operation of rewarding a user. This class stores
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
	/**
	 * Whether to save the user's information (only the information that has been
	 * modified) upon reward reception.
	 */
	private boolean shouldSave = true;

	/**
	 * Represents the total personal multiplier applied to this
	 * {@link RewardsOperation}. This is distinct from {@link #otherMultipliers}
	 * because this value is added to (before being multiplied with
	 * {@link #otherMultipliers}) if {@link #applyEarnedMultipliers} is
	 * <code>true</code>.
	 * 
	 * Multipliers stack additively with other multipliers of the same type (e.g.
	 * two personal multipliers are added to get a total personal multiplier) but
	 * multiplicatively with each of other types (a total personal multiplier is
	 * multiplied by a server multiplier to give a final reward amount).
	 */
	private BigDecimal personalMultiplier;
	/**
	 * All non-personal multipliers that will take place during this
	 * {@link RewardsOperation} combined into a final multiplier.
	 * <code>(sum(serverMultipliers) + 1) * (nitroMultiplier + 1)</code>. The
	 * <code>+1</code>s are because users should have a multiplier of <code>1</code>
	 * in any category if they don't have any active multipliers there (otherwise
	 * their final rewards will be zero :( ).
	 */
	private BigDecimal otherMultipliers;

	public boolean isShouldSave() {
		return shouldSave;
	}

	public void setShouldSave(boolean save) {
		this.shouldSave = save;
	}

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

	public BigDecimal getPersonalMultiplier() {
		return personalMultiplier;
	}

	public void setPersonalMultiplier(BigDecimal personalMultiplier) {
		this.personalMultiplier = personalMultiplier;
	}

	public BigDecimal getOtherMultipliers() {
		return otherMultipliers;
	}

	public void setOtherMultipliers(BigDecimal otherMultipliers) {
		this.otherMultipliers = otherMultipliers;
	}

}
