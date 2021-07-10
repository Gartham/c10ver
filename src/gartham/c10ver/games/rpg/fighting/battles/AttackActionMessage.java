package gartham.c10ver.games.rpg.fighting.battles;

import gartham.c10ver.actions.Action;
import gartham.c10ver.actions.ActionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class AttackActionMessage extends ActionMessage {

	private final String attackerTeam, opponentTeam, currentlyAttackingCreature, creatureIcon;
	private final int health, maxHealth;

	public AttackActionMessage(String attackerTeam, String opponentTeam, String currentlyAttackingCreature,
			String creatureIcon, int health, int maxHealth, Action... actions) {
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
		return new EmbedBuilder().setTitle('`' + attackerTeam + "` vs `" + opponentTeam + '`')
				.setDescription("**" + currentlyAttackingCreature + "**\nHealth: " + health + " / " + maxHealth
						+ calcHealthbar(health, maxHealth))
				.setImage(creatureIcon).build();
	}

	private static final String[][] BARS = { { "<:HealthFront:856774887379959818>" },
			{ "<:HealthSectionEmpty:856778113206452254>", "<:HealthSection12_5p:856774887296991253>",
					"<:HealthSection25p:856774887423344660>", "<:HealthSection37_5p:856774887388610561>",
					"<:HealthSection50p:856774886998409268>", "<:HealthSection62_5p:856774887103266847>",
					"<:HealthSection75p:856774887179943937>", "<:HealthSection87_5p:856774887565033482>",
					"<:HealthSectionFull:856774887439990834>" },
			{ "<:HealthBackEmpty:856774887377076274>", "<:HealthBackFull:856774887137345547>" } },
			LARGE_BARS = { { "<:HealthFrontMedium:863306492967256104>" }, {
					"<:HealthSectionEmptyMedium:863306492958081044>", "<:HealthSection12_5pMedium:863306492936716289>",
					"<:HealthSection25pMedium:863306492673392671>", "<:HealthSection37_5pMedium:863306492941172756>",
					"<:HealthSection50pMedium:863306492592914454>", "<:HealthSection62_5pMedium:863306492828319745>",
					"<:HealthSection75pMedium:863306492961488896>", "<:HealthSection87_5pMedium:863306493120872479>",
					"<:HealthSectionFullMedium:863306492949823488>" },
					{ "<:HealthBackEmptyMedium:863306492941565982>", "<:HealthBackFullMedium:863306492714156053>" } };

	private static String calcHealthbar(int health, int maxHealth) {
		StringBuilder bar = new StringBuilder(LARGE_BARS[0][0]);
		if (health == maxHealth) {
			bar.append(LARGE_BARS[1][LARGE_BARS[1].length - 1]);
			bar.append(LARGE_BARS[2][1]);
		} else {
			bar.append(LARGE_BARS[1][health == 0 ? 0
					: Math.round(health * (LARGE_BARS[1].length - 1) / (float) maxHealth)]);
			bar.append(LARGE_BARS[2][0]);
		}

		return bar.toString();
	}

}
