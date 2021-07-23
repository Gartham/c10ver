package gartham.c10ver.games.rpg.fighting.fighters.controllers;

import gartham.c10ver.Clover;
import gartham.c10ver.games.rpg.fighting.battles.AttackAction;
import gartham.c10ver.games.rpg.fighting.battles.AttackActionMessage;
import gartham.c10ver.games.rpg.fighting.battles.Battle;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;
import net.dv8tion.jda.api.entities.User;

public class PlayerFighterController implements FighterController {

	private final Battle battle;
	private final Fighter fighter;
	private final User user;
	private final Clover clover;

	public PlayerFighterController(Battle battle, Fighter fighter, User user, Clover clover) {
		this.battle = battle;
		this.fighter = fighter;
		this.user = user;
		this.clover = clover;
	}

	@Override
	public void act() {
		AttackActionMessage aam = new AttackActionMessage(fighter.getTeam().getName(), "???", fighter.getName(),
				fighter.getPfp(), fighter.getHealth(), fighter.getMaxHealth(),
				new AttackAction(null, "Attack", null, "Attack an oponent."));
	}

}
