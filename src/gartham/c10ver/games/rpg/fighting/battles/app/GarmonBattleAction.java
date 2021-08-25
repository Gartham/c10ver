package gartham.c10ver.games.rpg.fighting.battles.app;

public class GarmonBattleAction {
	public enum ActionType {
		SURRENDER, SKIP_TURN, ATTACK, SPECIAL_ATTACK;
	}

	private final ActionType type;
	private final GarmonAttack specialAttack;
	private final GarmonFighter target;

	public GarmonBattleAction(ActionType type) {
		if (type == ActionType.ATTACK || type == ActionType.SPECIAL_ATTACK)
			throw new IllegalArgumentException("Wrong constructor!");
		this.type = type;
		specialAttack = null;
		target = null;
	}

	public GarmonBattleAction(GarmonAttack specialAttack, GarmonFighter target) {
		this.specialAttack = specialAttack;
		type = ActionType.SPECIAL_ATTACK;
		this.target = target;
	}

	public GarmonBattleAction(GarmonFighter target) {
		specialAttack = null;
		type = ActionType.ATTACK;
		this.target = target;
	}

	public ActionType getType() {
		return type;
	}

	public GarmonAttack getSpecialAttack() {
		return specialAttack;
	}

	public boolean isSpecialAttack() {
		return getType() == ActionType.SPECIAL_ATTACK;
	}

	public GarmonFighter getTarget() {
		return target;
	}

}
