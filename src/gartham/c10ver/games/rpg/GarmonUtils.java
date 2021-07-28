package gartham.c10ver.games.rpg;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.fighting.battles.app.GarmonBattle;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonFighter;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonTeam;
import net.dv8tion.jda.api.EmbedBuilder;

public class GarmonUtils {
	private GarmonUtils() {
	}

	private static String getField(GarmonFighter f, GarmonBattle battle) {
		return "\\\u2764\uFE0F `" + f.getHealth() + "/" + f.getMaxHealth() + "` \u200b \u200b \\\u2694\uFE0F `" + f.getAttack()
				+ "` \u200b \u200b \\\uD83D\uDEE1\uFE0F `" + f.getDefense() + "` \u200b \u200b \\\uD83D\uDCA8\uFE0F `" + f.getSpeed()
				+ "`\nTeam: " + battle.getFighterTeam(f).getName();
	}

	public static EmbedBuilder printBattleQueue(GarmonBattle battle) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(String.join(" vs ", JavaTools.mask(battle.getTeamsUnmodifiable(), GarmonTeam::getName)));
		for (int i = 0; i < battle.getFighterCount() - 1; i++) {
			var f = battle.getBattleQueueUnmodifiable().get(i);
			builder.addField(
					"\uD83D\uDD50\uFE0F " + battle.getFighterTicks(f) + "   " + f.getEmoji() + ' ' + f.getName(),
					getField(f, battle) + "\n\u200b", false);
		}
		var f = battle.getBattleQueueUnmodifiable().get(battle.getFighterCount() - 1);
		builder.addField("\uD83D\uDD50\uFE0F " + battle.getFighterTicks(f) + "   " + f.getEmoji() + ' ' + f.getName(),
				getField(f, battle), false);
		return builder;
	}
}
