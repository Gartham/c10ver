package gartham.c10ver.games.rpg.fighting.fighters;

import java.math.BigInteger;

public class SimpleFighter implements Fighter {

	protected BigInteger speed;
	protected BigInteger maxHealth;
	protected BigInteger health;
	protected BigInteger attack;
	protected BigInteger defense;

	protected void setMaxHealth(BigInteger maxHealth) {
		this.maxHealth = maxHealth;
		if (health.compareTo(maxHealth) > 0)
			health = maxHealth;
	}

	protected void setHealth(BigInteger health) {
		this.health = health;
	}

	protected void setAttack(BigInteger attack) {
		this.attack = attack;
	}

	protected void setDefense(BigInteger defense) {
		this.defense = defense;
	}

	public SimpleFighter() {
	}

	/**
	 * Returns this {@link CustomFighter}'s speed. The speed of a
	 * {@link CustomFighter} canonically determines how many ticks it begins a
	 * battle with. (A speed higher than other {@link CustomFighter}s' results in a
	 * lower initial tick).
	 * 
	 * @return This {@link CustomFighter}'s speed.
	 */
	@Override
	public BigInteger getSpeed() {
		return speed;
	}

	@Override
	public BigInteger getMaxHealth() {
		return maxHealth;
	}

	@Override
	public BigInteger getHealth() {
		return health;
	}

	@Override
	public BigInteger getAttack() {
		return attack;
	}

	@Override
	public BigInteger getDefense() {
		return defense;
	}

	public void heal(BigInteger amount) {
		if (amount.equals(BigInteger.ZERO))
			return;
		else if (amount.compareTo(BigInteger.ZERO) < 0)
			damage(amount.negate());
		else {
			health = health.add(amount);
			if (health.compareTo(maxHealth) >= 0)
				health = maxHealth;
		}
	}

	public void heal(long amount) {
		heal(BigInteger.valueOf(amount));
	}

	public boolean damage(long amount) {
		return damage(BigInteger.valueOf(amount));
	}

	/**
	 * Damages this fighter by the specified amount and returns <code>true</code> if
	 * the fighter fainted (i.e. this fighter's health dropped below 1) as a result.
	 * 
	 * @param amount The amount of damage to do. If zero, this method returns
	 *               {@link #isFainted()}. If negative, calls
	 *               <code>{@link #heal(BigInteger) heal(-amount)}</code> and then
	 *               returns <code>false</code>.
	 * @return If this fighter is fainted after processing of this method.
	 */
	public boolean damage(BigInteger amount) {
		if (amount.equals(BigInteger.ZERO))
			return isFainted();
		else if (amount.compareTo(BigInteger.ZERO) < 0) {
			heal(amount.negate());
			return false;
		}
		if ((health = health.subtract(amount)).compareTo(BigInteger.ZERO) < 0) {
			health = BigInteger.ZERO;
			return true;
		}
		return health.equals(BigInteger.ZERO);
	}

	public String getHealthString() {
		return health == maxHealth ? "FULL" : isFainted() ? "FAINTED" : health + " / " + maxHealth;
	}

	public Fighter setSpeed(BigInteger speed) {
		this.speed = speed;
		return this;
	}

}