package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;

import gartham.c10ver.games.rpg.creatures.Creature;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;
import gartham.c10ver.games.rpg.fighting.fighters.SimpleFighter;

public class GarmonFighter extends SimpleFighter {

	private final String name, emoji, headshot;

	public GarmonFighter(String name, String emoji, String headshot, BigInteger speed, BigInteger maxHealth,
			BigInteger health, BigInteger attack, BigInteger defense) {
		super(speed, maxHealth, health, attack, defense);
		this.name = name;
		this.emoji = emoji;
		this.headshot = headshot;
	}

	/**
	 * Makes a {@link GarmonFighter} with the specified parameters and the default,
	 * clover logo emoji.
	 * 
	 * @param name      The name of the {@link Fighter}.
	 * @param headshot  A url referring to a pictographic headshot of the
	 *                  {@link Fighter}.
	 * @param speed     The speed of the {@link Fighter}.
	 * @param maxHealth The maximum amount of health that the {@link Fighter} can
	 *                  have.
	 * @param health    The current health of the {@link Fighter}
	 * @param attack    The {@link Fighter}'s attack stat.
	 * @param defense   The {@link Fighter}'s defense stat.
	 */
	public GarmonFighter(String name, String headshot, BigInteger speed, BigInteger maxHealth, BigInteger health,
			BigInteger attack, BigInteger defense) {
		this(name, "<:clover:869763513495748609>", headshot, speed, maxHealth, health, attack, defense);
	}

	public String getName() {
		return name;
	}

	public String getEmoji() {
		return emoji;
	}

	public String getHeadshot() {
		return headshot;
	}

	public GarmonFighter(Creature creature) {
		this(creature.getName(), creature.getEmoji(), creature.getPFP(), creature.getSpeed(), creature.getHp(),
				creature.getHp(), creature.getAttack(), creature.getDefense());
	}

	public GarmonFighter(String namePrefix, Creature creature) {
		this(namePrefix + ' ' + creature.getName(), creature.getEmoji(), creature.getPFP(), creature.getSpeed(),
				creature.getHp(), creature.getHp(), creature.getAttack(), creature.getDefense());
	}

	public void modDef(BigInteger amount) {
		setDefense(getDefense().add(amount));
	}

	public void modAtt(BigInteger amount) {
		setAttack(getAttack().add(amount));
	}

	public void modMaxHealth(BigInteger amount) {
		setMaxHealth(getMaxHealth().add(amount));
	}
}
