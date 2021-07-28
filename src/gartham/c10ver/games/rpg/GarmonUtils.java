package gartham.c10ver.games.rpg;

import java.util.Random;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.strings.StringTools;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import gartham.c10ver.games.rpg.creatures.Creature;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonBattle;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonFighter;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonTeam;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

public class GarmonUtils {
	private GarmonUtils() {
	}

	private static String getField(GarmonFighter f, GarmonBattle battle) {
		return "\\\u2764\uFE0F `" + f.getHealth() + "/" + f.getMaxHealth() + "` \u200b \u200b \\\u2694\uFE0F `"
				+ f.getAttack() + "` \u200b \u200b \\\uD83D\uDEE1\uFE0F `" + f.getDefense()
				+ "` \u200b \u200b \\\uD83D\uDCA8\uFE0F `" + f.getSpeed() + "`\nTeam: "
				+ battle.getFighterTeam(f).getName();
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

	/**
	 * Tries to provide a feasible webhook for use. This method iterates over all
	 * the webhooks it retrieves from the specified channel. If any one of them is
	 * created by the bot user (more specifically, if its owner is the bot user), it
	 * is returned. Otherwise, an attempt is made to create a new webhook. This
	 * function will throw exceptions if it does not have the appropriate
	 * permissions or if an error occurs during the retrieval or creation of a
	 * webhook.
	 * 
	 * @param channel The channel that the webhook will belong to.
	 * @return The {@link Webhook} that was found or newly created.
	 */
	public static Webhook getFeasibleWebhook(TextChannel channel) {
		for (Webhook wb : channel.retrieveWebhooks().complete())
			if (wb.getOwner().getId().equals(channel.getJDA().getSelfUser().getId()))
				return wb;
		byte[] b = new byte[5];
		new Random().nextBytes(b);// 1/2^40 collision chance.
		return channel.createWebhook(StringTools.toHexString(b)).complete();
	}

	public static void sendAsCreature(Creature creature, String message, EmbedBuilder embed, TextChannel channel) {
		if (message == null && embed == null)
			throw null;
		var wb = getFeasibleWebhook(channel);
		WebhookClientBuilder wcb = WebhookClientBuilder.fromJDA(wb);
		var hook = wcb.buildJDA();

		var wmb = new WebhookMessageBuilder();
		wmb.setAvatarUrl(creature.getPFP());
		wmb.setUsername(creature.getName());
		if (message != null)
			wmb.setContent(message);
		if (embed != null)
			wmb.addEmbeds(WebhookEmbedBuilder.fromJDA(embed.build()).build());
		hook.send(wmb.build());
	}

	public static void sendAsCreature(Creature creature, EmbedBuilder embed, TextChannel channel) {
		sendAsCreature(creature, null, embed, channel);
	}

	public static void sendAsCreature(Creature creature, String message, TextChannel channel) {
		sendAsCreature(creature, message, null, channel);
	}

}
