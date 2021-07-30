package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.Clover;
import gartham.c10ver.actions.DetailedAction;
import gartham.c10ver.games.rpg.GarmonUtils;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonBattleAction.ActionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public final class GarmonBattleManager {
	private final GarmonBattle battle;
	private final GarmonTeam opponentTeam, playerTeam;
	private final Clover clover;
	private final User player;

	private final TextChannel chan;

	public GarmonBattleManager(GarmonBattle battle, GarmonTeam opponentTeam, GarmonTeam playerTeam, Clover clover,
			User player, TextChannel chan) {
		this.battle = battle;
		this.opponentTeam = opponentTeam;
		this.playerTeam = playerTeam;
		this.clover = clover;
		this.player = player;
		this.chan = chan;
	}

	public void start() {
		battle.start();
		next();
	}

	private void printWin() {

	}

	private void next() {
		chan.sendMessage("Battle Queue:").embed(GarmonUtils.printBattleQueue(battle).build()).queue();
		var actor = battle.getActingFighter();
		if (playerTeam.contains(actor)) {
			userTurnMessage().send(clover, chan, player);
		} else {
			// Non-player-controlled turn.
		}
	}

	private GarmonActionMessage userTurnMessage() {
		return new GarmonActionMessage(battle.getActingFighter(), attack(), surrender(), skipTurn());
	}

	private DetailedAction surrender() {
		return new DetailedAction("\uD83C\uDFF3", "Surrender", "Give up and take the L.", t -> {
			if (battle.act(new GarmonBattleAction(ActionType.SURRENDER))) {
				chan.sendMessage("**Battle Lost!**\n" + player.getAsMention()
						+ " surrendered. No cloves were gained, but you still got some experience.").queue();
			} else
				next();
		});
	}

	private DetailedAction skipTurn() {
		return new DetailedAction("\uD83D\uDCA8", "Skip Turn", "Pass up this creature's move.", t -> {
			battle.act(new GarmonBattleAction(ActionType.SKIP_TURN));
			next();
		});
	}

	private DetailedAction attack() {
		return new DetailedAction("\u2694", "Attack", "Pow pow pow!\nTakes: \uD83D\uDD50\uFE0F 50", t -> {
			if (opponentTeam.memberView().size() == 1)
				if (battle.act(new GarmonBattleAction(opponentTeam.iterator().next()/* TODO Fix */)))
					printWin();
				else
					next();
			else {

			}
		});
	}

}
