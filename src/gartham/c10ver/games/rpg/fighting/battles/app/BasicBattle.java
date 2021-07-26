package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.games.rpg.fighting.battles.api.Battle;
import gartham.c10ver.games.rpg.fighting.battles.api.Team;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public class BasicBattle extends Battle<BattleAction, Fighter, Team<Fighter>> {

	@Override
	protected int handleAction(BattleAction action, Fighter fighter) {
		// TODO Auto-generated method stub
		return 0;
	}

}
