package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import gartham.c10ver.Clover;
import gartham.c10ver.games.rpg.GarmonUtils;
import gartham.c10ver.games.rpg.fighting.battles.api.ActionCompletion;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonBattleAction.ActionType;
import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.DetailedActionButton;
import gartham.c10ver.response.actions.DetailedActionReaction;
import gartham.c10ver.response.menus.DetailedMenuMessage;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public final class GarmonBattleManager {
	private final GarmonBattle battle;
	private final GarmonTeam opponentTeam, playerTeam;
	private final Clover clover;
	private final User player;
	private final GarmonFighter playerFighter;

	private final TextChannel chan;

	public GarmonBattleManager(GarmonBattle battle, GarmonTeam opponentTeam, GarmonTeam playerTeam, Clover clover,
			User player, TextChannel chan, GarmonFighter playerFighter) {
		this.playerFighter = playerFighter;
		this.battle = battle;
		this.opponentTeam = opponentTeam;
		this.playerTeam = playerTeam;
		this.clover = clover;
		this.player = player;
		this.chan = chan;
	}

	public GarmonBattleManager(GarmonBattle battle, GarmonTeam opponentTeam, GarmonTeam playerTeam, Clover clover,
			User player, TextChannel chan) {
		this(battle, opponentTeam, playerTeam, clover, player, chan, null);
	}

	public void start() {
		battle.start();
		next();
	}

	private void printBattleOver() {
		chan.sendMessage("**Battle Finished!**\n" + battle.getWinningTeam().getName() + " has won the battle!").queue();
	}

	private void next() {
		var actor = battle.getActingFighter();
		if (playerTeam.contains(actor)) {
			userTurnMessage(actor).send(clover, chan, player);
		} else {
			var c = (Consumer<Object>) t -> {
				List<GarmonFighter> list = battle.getRemainingFighters(playerTeam);
				var act = battle.act(new GarmonBattleAction(list.get((int) (Math.random() * list.size()))));
				sendAttackMessage(act);
				if (act.isBattleOver())
					printBattleOver();
				else
					next();
			};
			chan.sendTyping().queueAfter((int) (Math.random() * 2400 + 1200), TimeUnit.MILLISECONDS, c, c);
		}
	}

	private GarmonActionMessage userTurnMessage(GarmonFighter actor) {
		GarmonActionMessage m = new GarmonActionMessage(battle.getActingFighter(), surrender(),
				skipTurn(actor == playerFighter));
		m.getReactions().add(1, attack(m));
		m.getReactions().add(info(m));
		return m;
	}

	private DetailedActionReaction surrender() {
		return new DetailedActionReaction("\uD83C\uDFF3", "Surrender", "Give up and take the L.", t -> {
			if (battle.act(new GarmonBattleAction(ActionType.SURRENDER)).isBattleOver())
				chan.sendMessage("**Battle Lost!**\n" + player.getAsMention() + " surrendered. Better luck next time.")
						.queue();
			else
				next();
		});
	}

	private DetailedActionReaction skipTurn(boolean player) {
		return new DetailedActionReaction("\uD83D\uDCA8", "Skip Turn",
				"Pass up " + (player ? "your" : "this creature's") + "move.", t -> {
					battle.act(new GarmonBattleAction(ActionType.SKIP_TURN));
					next();
				});

	}

	private void sendAttackMessage(ActionCompletion<GarmonActionResult, GarmonFighter> act) {
		var whm = new WebhookMessageBuilder();
		GarmonFighter fighter = act.getFighter();
		whm.setUsername(fighter.getName());
		whm.setAvatarUrl(fighter.getHeadshot());
		whm.setContent("*Attacks " + act.getResult().getTarget().getName() + " for \u2694 `"
				+ act.getResult().getDamage() + "`.*");
		GarmonUtils.queueWithClient(chan, t -> t.send(whm.build()));
	}

	private DetailedActionReaction attack(DetailedMenuMessage<DetailedActionReaction, DetailedActionButton> source) {
		return new DetailedActionReaction("\u2694", "Attack", "Pow pow pow!\nTakes: \uD83D\uDD50\uFE0F 50", t -> {
			if (opponentTeam.memberView().size() == 1) {
				ActionCompletion<GarmonActionResult, GarmonFighter> act = battle
						.act(new GarmonBattleAction(opponentTeam.iterator().next()/* TODO Fix */));
				sendAttackMessage(act);
				if (act.isBattleOver())
					printBattleOver();
				else
					next();
			} else {
				var dam = new DetailedMenuMessage<>(new ActionMessage<>());
				var oplist = battle.getRemainingFighters(opponentTeam);
				for (var v : oplist)
					dam.getReactions().add(new DetailedActionReaction(v.getName(),
							"\uD83D\uDD50\uFE0F " + battle.getFighterTicks(v), t1 -> {
								var act = battle.act(new GarmonBattleAction(v));
								sendAttackMessage(act);
								if (act.isBattleOver())
									printBattleOver();
								else
									next();
							}));
				dam.getReactions().add(new DetailedActionReaction("Back", "Return to the previous menu.",
						DetailedActionReaction.actionMessageAction(source)));
				dam.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
			}
		});
	}

	private DetailedActionReaction info(DetailedMenuMessage<DetailedActionReaction, DetailedActionButton> source) {
		return new DetailedActionReaction("\u2139", "Info", "Check battle queue or enemy stats.", t -> {
			var dam = new DetailedMenuMessage<>(new ActionMessage<>());
			DetailedActionReaction battleQueue = new DetailedActionReaction("Battle Queue",
					"Check the time until each creature's turn.",
					DetailedActionReaction.actionMessageAction(dam, t1 -> chan.sendMessage("Battle Queue:")
							.embed(GarmonUtils.printBattleQueue(battle).build()).queue()));
			DetailedActionReaction back = new DetailedActionReaction("\u2B05", "Back", "Go back to attack menu.",
					DetailedActionReaction.actionMessageAction(source));
			dam.getReactions().add(battleQueue);
			dam.getReactions().add(back);
			dam.send(clover, chan, player);
		});
	}

}
