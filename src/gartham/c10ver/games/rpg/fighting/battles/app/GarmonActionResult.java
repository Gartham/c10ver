package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;

import gartham.c10ver.games.rpg.fighting.battles.api.ActionResult;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonBattleAction.ActionType;

public class GarmonActionResult extends ActionResult {

	private final ActionType type;
	private final BigInteger damage;
	private final GarmonFighter target;

	public GarmonActionResult(int ticks, ActionType type, BigInteger damage, GarmonFighter target) {
		super(ticks);
		this.type = type;
		this.damage = damage;
		this.target = target;
	}

	public ActionType getType() {
		return type;
	}

	public BigInteger getDamage() {
		return damage;
	}

	public GarmonFighter getTarget() {
		return target;
	}

}
