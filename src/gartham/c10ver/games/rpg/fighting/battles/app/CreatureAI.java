package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;
import java.util.Random;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import gartham.c10ver.games.rpg.GarmonUtils;
import gartham.c10ver.games.rpg.fighting.battles.api.Controller;
import net.dv8tion.jda.api.entities.TextChannel;

public class CreatureAI implements Controller<GarmonFighter> {
	private final static Random RANDOM = new Random();

	private final GarmonBattle battle;
	private final TextChannel channel;

	public CreatureAI(GarmonBattle battle, TextChannel channel) {
		this.battle = battle;
		this.channel = channel;
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
	}

	/**
	 * An event handler that gets called immediately before this creature
	 * automatically attacks.
	 */
	protected void preattack() {

	}

	/**
	 * An event handler that gets called immediately after this creature attacks,
	 * but before the completion of the move.
	 */
	protected void postattack() {

	}

	@Override
	public int move(GarmonFighter fighter) {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		var opps = battle.getRemainingOpponents(battle.getTeam(fighter));
		attack(fighter, opps.get((int) (opps.size() * Math.random())));

		return 0;
	}

}
