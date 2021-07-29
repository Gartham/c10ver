package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.games.rpg.GarmonUtils;
import net.dv8tion.jda.api.entities.TextChannel;

public final class GarmonBattleManager {
	private final GarmonBattle battle;
	private final GarmonTeam opponentTeam, playerTeam;

	private final TextChannel chan;

	public GarmonBattleManager(GarmonBattle battle, GarmonTeam opponentTeam, GarmonTeam playerTeam, TextChannel chan) {
		this.battle = battle;
		this.opponentTeam = opponentTeam;
		this.playerTeam = playerTeam;
		this.chan = chan;
	}

	public void start() {
		battle.start();
		chan.sendMessage("Battle Queue:").embed(GarmonUtils.printBattleQueue(battle).build());
		next();
	}

	private void next() {
		var actor = battle.getActingFighter();
		if (playerTeam.contains(actor)) {
			// Send battle option.
//			ActionMess
		} else {

		}
	}

}
