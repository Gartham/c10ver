package gartham.c10ver.games.rpg.fighting.battles;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import gartham.c10ver.actions.ActionMessage;
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
						+ calcHealthbar(health, maxHealth) + "\n\u200b")
				.setThumbnail(creatureIcon);
		List<AttackAction> actions = getActions();
		for (int i = 0; i < actions.size(); i++) {
			var a = actions.get(i);
			String emoji = a.getEmoji() == null ? getNumericEmoji(i) : a.getEmoji();
			e.addField(emoji + ' ' + a.getDescription(), a.getOptionDescription(), true);
		}
		return e.build();
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

	private static String calcHealthbar(BigInteger health, BigInteger maxHealth) {
		StringBuilder bar = new StringBuilder(LARGE_BARS[0][0]);
		if (health.equals(maxHealth)) {
			bar.append(LARGE_BARS[1][LARGE_BARS[1].length - 1]);
			bar.append(LARGE_BARS[2][1]);
		} else {
			bar.append(LARGE_BARS[1][health.equals(BigInteger.ZERO) ? 0
					: new BigDecimal(health.multiply(BigInteger.valueOf(LARGE_BARS[1].length - 1)))
							.divide(new BigDecimal(maxHealth)).setScale(0, RoundingMode.HALF_UP).intValue()]);
			bar.append(LARGE_BARS[2][0]);
		}

		return bar.toString();
	}

}
