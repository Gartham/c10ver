package gartham.c10ver.games.rpg.fighting.fighters;

import java.math.BigInteger;

public interface Fighter extends Comparable<Fighter> {

	/**
	 * Returns this {@link SimpleFighter}'s speed. The speed of a
	 * {@link SimpleFighter} canonically determines how many ticks it begins a
	 * battle with. (A speed higher than other {@link SimpleFighter}s' results in a
	 * lower initial tick).
	 * 
	 * @return This {@link SimpleFighter}'s speed.
	 */
	BigInteger getSpeed();

	BigInteger getMaxHealth();

	BigInteger getHealth();

	BigInteger getAttack();

	BigInteger getDefense();

	/**
	 * Returns whether this {@link Fighter}'s {@link #getHealth() health} is
	 * {@link BigInteger#ZERO zero} or not.
	 * 
	 * @return <code>true</code> if this {@link Fighter}'s {@link #getHealth()
	 *         health} is zero, <code>false</code> otherwise.
	 */
	default boolean isFainted() {
		return getHealth().equals(BigInteger.ZERO);
	}

	@Override
	default int compareTo(Fighter o) {
		return getSpeed().compareTo(o.getSpeed());
	}

}