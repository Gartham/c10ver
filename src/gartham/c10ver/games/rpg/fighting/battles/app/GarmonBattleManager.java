package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.List;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import gartham.c10ver.Clover;
import gartham.c10ver.actions.DetailedAction;
import gartham.c10ver.actions.DetailedActionMessage;
import gartham.c10ver.games.rpg.GarmonUtils;
import gartham.c10ver.games.rpg.fighting.battles.api.ActionCompletion;
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
		// TODO Code
	}

	private void next() {
		chan.sendMessage("Battle Queue:").embed(GarmonUtils.printBattleQueue(battle).build()).queue();
		var actor = battle.getActingFighter();
		if (playerTeam.contains(actor)) {
			userTurnMessage().send(clover, chan, player);
		} else {
			List<GarmonFighter> list = battle.getRemainingFighters(playerTeam);
			battle.act(new GarmonBattleAction(list.get((int) (Math.random() * list.size()))));// Make this return a new
																								// ActionResult type.
			// Non-player-controlled turn.
		}
	}

	private GarmonActionMessage userTurnMessage() {
		GarmonActionMessage m = new GarmonActionMessage(battle.getActingFighter(), surrender(), skipTurn());
		m.getActions().add(1, attack(m));
		return m;
	}

	private DetailedAction surrender() {
		return new DetailedAction("\uD83C\uDFF3", "Surrender", "Give up and take the L.", t -> {
			if (battle.act(new GarmonBattleAction(ActionType.SURRENDER)).isBattleOver()) {
				chan.sendMessage("**Battle Lost!**\n" + player.getAsMention()
						+ " surrendered. No cloves were gained, but you still got some experience!").queue();
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

	private void sendAttackMessage(ActionCompletion<GarmonActionResult, GarmonFighter> act) {
		var wh = GarmonUtils.getClient(chan);
		var whm = new WebhookMessageBuilder();
		GarmonFighter fighter = act.getFighter();
		whm.setUsername(fighter.getName());
		whm.setAvatarUrl(fighter.getHeadshot());
		whm.setContent("*Attacks " + act.getResult().getTarget().getName() + " for \u2694 `"
				+ act.getResult().getDamage() + "`.");
		wh.send(whm.build());
	}

	private DetailedAction attack(DetailedActionMessage<DetailedAction> source) {
		return new DetailedAction("\u2694", "Attack", "Pow pow pow!\nTakes: \uD83D\uDD50\uFE0F 50", t -> {
			if (opponentTeam.memberView().size() == 1) {
				ActionCompletion<GarmonActionResult, GarmonFighter> act = battle
						.act(new GarmonBattleAction(opponentTeam.iterator().next()/* TODO Fix */));
				sendAttackMessage(act);
				if (act.isBattleOver())
					printWin();
				else
					next();
			} else {
				var dam = new DetailedActionMessage<>();
				var oplist = battle.getRemainingFighters(opponentTeam);
				for (var v : oplist)
					dam.getActions().add(
							new DetailedAction(v.getName(), "\uD83D\uDD50\uFE0F " + battle.getFighterTicks(v), t1 -> {
								var act = battle.act(new GarmonBattleAction(v));
								sendAttackMessage(act);
								if (act.isBattleOver())
									printWin();
								else
									next();
							}));
				dam.getActions().add(new DetailedAction("Back", "Return to the previous menu.",
						DetailedAction.actionMessageAction(source)));
				dam.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
			}
		});
	}

}
