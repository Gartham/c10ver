package gartham.c10ver.games.rpg.fighting.battles;

import java.math.BigInteger;
import java.util.List;

import gartham.c10ver.actions.ActionMessage;
import gartham.c10ver.games.rpg.RPGUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class AttackActionMessage extends ActionMessage<AttackAction> {

	private final String attackerTeam, opponentTeam, currentlyAttackingCreature, creatureIcon;
	private final BigInteger health, maxHealth;

	public AttackActionMessage(String attackerTeam, String opponentTeam, String currentlyAttackingCreature,
			String creatureIcon, BigInteger health, BigInteger maxHealth, AttackAction... actions) {
		super(actions);
		this.attackerTeam = attackerTeam;
		this.opponentTeam = opponentTeam;
		this.currentlyAttackingCreature = currentlyAttackingCreature;
		this.creatureIcon = creatureIcon;
		this.health = health;
		this.maxHealth = maxHealth;
	}

	@Override
	public MessageEmbed embed() {
		EmbedBuilder e = new EmbedBuilder().setTitle('`' + attackerTeam + "` vs `" + opponentTeam + '`')
				.setDescription("**" + currentlyAttackingCreature + "**\nHealth: " + health + " / " + maxHealth
						+ RPGUtils.calcHealthbar(health, maxHealth) + "\n\u200b")
				.setThumbnail(creatureIcon);
		List<AttackAction> actions = getActions();
		for (int i = 0; i < actions.size(); i++) {
			var a = actions.get(i);
			String emoji = a.getEmoji() == null ? getNumericEmoji(i) : a.getEmoji();
			e.addField(emoji + ' ' + a.getDescription(), a.getOptionDescription(), true);
		}
		return e.build();
	}

}
