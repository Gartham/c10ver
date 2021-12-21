package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import gartham.c10ver.Clover;
import gartham.c10ver.games.rpg.GarmonUtils;
import gartham.c10ver.games.rpg.fighting.battles.api.Controller;
import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.DetailedActionButton;
import gartham.c10ver.response.actions.DetailedActionReaction;
import gartham.c10ver.response.menus.DetailedMenuMessage;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PlayerController implements Controller<GarmonFighter> {
	private final static Random RANDOM = new Random();

	private final GarmonBattle battle;
	private final Clover clover;
	private final User player;
	private final TextChannel channel;

	private volatile int ticks;

	@Override
	public int move(GarmonFighter fighter) {
		ticks = 0;
		synchronized (this) {
			// Let player select an option.
			userTurnMessage(fighter).send(clover, channel, player);
			try {
				wait();// Wait until player does select an option.
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			// TODO Include timer.
		}
		// Return the number of ticks the action took.
		return ticks;
	}

	public PlayerController(GarmonBattle battle, Clover clover, User player, TextChannel channel) {
		this.battle = battle;
		this.clover = clover;
		this.player = player;
		this.channel = channel;
	}

	private GarmonActionMessage userTurnMessage(GarmonFighter fighter) {
		GarmonActionMessage m = new GarmonActionMessage(fighter, surrender(fighter), skipTurn(fighter));
		m.getReactions().add(1, attack(m, fighter));
		m.getReactions().add(info(m));
		return m;
	}

	private DetailedActionReaction surrender(GarmonFighter fighter) {
		return new DetailedActionReaction("\uD83C\uDFF3", "Surrender", "Give up and take the L.", t -> {
			synchronized (this) {
				battle.surrender(battle.getTeam(fighter));
				channel.sendMessage(player.getAsMention() + " surrendered and lost the battle! Better luck next time.")
						.queue();
				PlayerController.this.notify();
			}
		});
	}

	private DetailedActionReaction skipTurn(GarmonFighter fighter) {
		return new DetailedActionReaction("\uD83D\uDCA8", "Skip Turn",
				"Pass up " + (fighter instanceof PlayerFighter ? "your" : "this creature's") + " move.", t -> {
					synchronized (this) {
						battle.skipTurn();
						PlayerController.this.notify();
					}
				});

	}

	private static BigInteger max(BigInteger first, BigInteger second) {
		return first.compareTo(second) > 0 ? first : second;
	}

	private void attack(GarmonFighter attacker, GarmonFighter target) {
		BigInteger attack = attacker.getAttack();
		for (int i = 2; i < 13; i++)
			if (RANDOM.nextInt(i) == 0)
				attack = attack.add(attacker.getAttack().divide(BigInteger.valueOf(i + 1)));
		BigInteger dmg = max(BigInteger.ONE, attack.subtract(target.getDefense()));
		target.damage(dmg);
		var whm = new WebhookMessageBuilder();
		whm.setUsername(attacker.getName());
		whm.setAvatarUrl(attacker.getHeadshot());
		whm.setContent("*Attacks " + target.getName() + " for \u2694 `" + dmg + "`.*");
		GarmonUtils.queueWithClient(channel, t -> t.send(whm.build()));
		ticks = 50;
	}

	private DetailedActionReaction attack(DetailedMenuMessage<DetailedActionReaction, DetailedActionButton> source,
			GarmonFighter fighter) {
		return new DetailedActionReaction("\u2694", "Attack", "Pow pow pow!\nTakes: \uD83D\uDD50\uFE0F 50", t -> {

			synchronized (this) {
				List<GarmonFighter> opps = battle.getRemainingOpponents(battle.getTeam(fighter));
				if (opps.size() == 1) {
					attack(fighter, opps.get(0));
					notify();
				} else {
					var dam = new DetailedMenuMessage<>(new ActionMessage<>());
					for (var v : opps)
						dam.getReactions().add(new DetailedActionReaction(v.getName(),
								"\uD83D\uDD50\uFE0F " + battle.getTicksTillTurn().get(fighter), t1 -> {
									attack(fighter, v);
									notify();
								}));
					dam.getReactions().add(new DetailedActionReaction("Back", "Return to the previous menu.",
							DetailedActionReaction.actionMessageAction(source)));
					dam.send(t.getReactionProcessor(), t.getButtonClickProcessor(), t.getEvent().getChannel(),
							t.getEvent().getUser());
				}
			}
		});
	}

	private DetailedActionReaction info(DetailedMenuMessage<DetailedActionReaction, DetailedActionButton> source) {
		return new DetailedActionReaction("\u2139", "Info", "Check battle queue or enemy stats.", t -> {
			var dam = new DetailedMenuMessage<>(new ActionMessage<>());
			DetailedActionReaction battleQueue = new DetailedActionReaction("Battle Queue",
					"Check the time until each creature's turn.",
					DetailedActionReaction.actionMessageAction(dam, t1 -> channel.sendMessage("Battle Queue:")
							.embed(GarmonUtils.printBattleQueue(battle).build()).queue()));
			DetailedActionReaction back = new DetailedActionReaction("\u2B05", "Back", "Go back to attack menu.",
					DetailedActionReaction.actionMessageAction(source));
			dam.getReactions().add(battleQueue);
			dam.getReactions().add(back);
			dam.send(clover, channel, player);
		});
	}

}
