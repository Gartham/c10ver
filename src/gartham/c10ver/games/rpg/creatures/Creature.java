package gartham.c10ver.games.rpg.creatures;

import gartham.c10ver.data.PropertyObject;

public class Creature extends PropertyObject {

	private final Property<Integer> hp = intProperty("hp"), attack = intProperty("attack"),
			speed = intProperty("speed"), defense = intProperty("defense"), level = intProperty("level");

	public Property<Integer> getHPProperty() {
		return hp;
	}

	public Property<Integer> getAttackProperty() {
		return attack;
	}

	public Property<Integer> getSpeedProperty() {
		return speed;
	}

	public Property<Integer> getDefenseProperty() {
		return defense;
	}

	public Property<Integer> getLevelProperty() {
		return level;
	}

	public int getHp() {
		return hp.get();
	}

	public int getAttack() {
		return attack.get();
	}

	public int getSpeed() {
		return speed.get();
	}

	public int getDefense() {
		return defense.get();
	}

	public int getLevel() {
		return level.get();
	}

}
