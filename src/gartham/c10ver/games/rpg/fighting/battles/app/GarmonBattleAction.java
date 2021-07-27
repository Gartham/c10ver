package gartham.c10ver.games.rpg.fighting.battles.app;

public class GarmonBattleAction {
	public enum ActionType {
		SURRENDER, SKIP_TURN, ATTACK, SPECIAL_ATTACK;
	}

	private final ActionType type;
	private final GarmonAttack specialAttack;

	private GarmonBattleAction(ActionType type) {
		this.type = type;
		specialAttack = null;
	}

	private GarmonBattleAction(GarmonAttack specialAttack) {
		this.specialAttack = specialAttack;
		type = ActionType.SPECIAL_ATTACK;
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

}
