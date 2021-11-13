package gartham.c10ver.economy;

import java.io.File;
import java.math.BigInteger;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.users.EconomyUser;

/**
 * <h1 style="font-size:1.2em;">Mailboxes</h1>
 * <p>
 * A user's {@link Mailbox}. This class represents the storage in which earnings
 * are collected by a user passively (meaning, not by a command or action
 * directly with the bot). When a user interacts directly with the bot to earn
 * rewards (e.g., receiving their daily rewards with the <code>daily</code>
 * command), they receive those rewards <i>directly</i>, and are shown the
 * rewards they received immediately (as a result of executing the interaction
 * with the bot), however, if a user earns rewards <i>passively</i>, e.g. by
 * talking to another user thereby getting rewards for sending messages, the
 * rewards are added to the user's <i>mailbox</i>, which the user can claim.
 * When claimed, the user will be shown what rewards they earned.
 * </p>
 * <p>
 * {@link Mailbox}es can contain cloves and items. Earned multipliers are
 * applied to a user immediately, even when they're idle, although they affect
 * rewards earned when idle.
 * </p>
 * <h3>Saving/Loading</h3>
 * <p>
 * {@link Mailbox}es accept a directory {@link File} object during construction
 * that represents where the {@link Mailbox} should be saved. The provided
 * {@link File} will be treated as a directory by this class. {@link Mailbox}es
 * require a directory because they store an {@link Inventory} in it.
 * </p>
 * 
 * @author Gartham
 *
 */
public class Mailbox extends SavablePropertyObject {

	private final Inventory inventory = new Inventory();
	private final Property<BigInteger> cloves = bigIntegerProperty("cloves", BigInteger.ZERO);
	private final File dir;
	private final EconomyUser user;

	public Mailbox(File dir, EconomyUser user) {
		super(new File(dir, "mb.txt"));
		this.dir = dir;
		this.user = user;
		load();
	}

	@Override
	public void save() {
		File i = new File(dir, "inv");
		inventory.saveAll(i);
		super.save();

	}

	@Override
	public void load() {
		File i = new File(dir, "inv");
		inventory.load(i);
		super.load();
	}

	/**
	 * Adds the rewards from the provided {@link RewardsOperation} to this
	 * {@link Mailbox}.
	 * 
	 * @param op The rewards to put in this {@link Mailbox}.
	 */
	public void reward(RewardsOperation op) {
		if (op.hasCloves()) {
			addCloves(op.getRewardedCloves());
			if (op.isShouldSave())
				super.save();
		}
		if (op.hasItems()) {
			op.getItems().putInto(inventory);
			if (op.isShouldSave())
				inventory.saveAll(new File(dir, "inv"));
		}
		if (op.hasMults()) {
			for (var m : op.getMults().entrySet())
				for (int i = 0; i < m.getValue(); i++)
					user.addMultiplier(m.getKey().reify());
			if (op.isShouldSave())
				user.save();
		}
	}

	public BigInteger getCloves() {
		return cloves.get();
	}

	/**
	 * Sets the raw number of cloves in this {@link Mailbox}.
	 * 
	 * @param cloves The number of cloves that the mailbox will have.
	 */
	public void setCloves(BigInteger cloves) {
		this.cloves.set(cloves);
	}

	/**
	 * Adds cloves to this {@link Mailbox}. No multipliers are considered when
	 * performing this operation.
	 * 
	 * @param amount The exact number of cloves to add.
	 */
	public void addCloves(BigInteger amount) {
		cloves.set(cloves.get().add(amount));
	}

	public Inventory getInventory() {
		return inventory;
	}

	public EconomyUser getUser() {
		return user;
	}

	/**
	 * <p>
	 * Prepares a {@link RewardsOperation} that contains all the rewards from this
	 * {@link Mailbox}. <b>Note that the {@link RewardsOperation} will have no
	 * effective multipliers.</b> This is because multipliers are applied to rewards
	 * in the mailbox when they are put into the {@link Mailbox}, so when claiming
	 * from the {@link Mailbox}, no multipliers are applied.
	 * </p>
	 * <h3>Side Effects</h3>
	 * <p>
	 * This {@link Mailbox} will be emptied as a result of a call to this method.
	 * (Specifically, this {@link Mailbox} will have its {@link #cloves} set to
	 * {@link BigInteger#ZERO} and its {@link Inventory} cleared, as if it were a
	 * newly created {@link Mailbox}.)
	 * </p>
	 * 
	 * @return A {@link RewardsOperation} that contains {@link #getCloves()} and
	 *         {@link #getInventory()}. The returned {@link RewardsOperation} has
	 *         its {@link RewardsOperation#isShouldSave() shouldSave} property set
	 *         to <code>true</code> and its
	 *         {@link RewardsOperation#isApplyEarnedMultipliers()
	 *         applyEarnedMultipliers} property set to <code>false</code>. Both
	 *         {@link RewardsOperation#getOtherMultipliers()} and
	 *         {@link RewardsOperation#getPersonalMultiplier()} are
	 *         {@link BigInteger#ONE}.
	 */
	public RewardsOperation claim() {
		RewardsOperation op = new RewardsOperation();
		op.setCloves(getCloves());
		inventory.putInto(op.getItems());
		op.setShouldSave(true);
		op.setApplyEarnedMultipliers(false);
		return op;
	}

	public boolean hasItems() {
		return !inventory.isEmpty();
	}

	public boolean hasCloves() {
		return !getCloves().equals(BigInteger.ZERO);
	}

	/**
	 * Returns <code>true</code> if there is nothing to be claimed in this
	 * {@link Mailbox}.
	 * 
	 * @return
	 * 
	 *         <pre>
	 * <code>!(hasItems() || hasCloves())</code>
	 *         </pre>
	 * 
	 *         <code>true</code> if this {@link Mailbox} has items or cloves,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return !(hasItems() || hasCloves());
	}

}
