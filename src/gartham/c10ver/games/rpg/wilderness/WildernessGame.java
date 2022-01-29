package gartham.c10ver.games.rpg.wilderness;

import java.time.Instant;

import gartham.c10ver.Clover;
import gartham.c10ver.games.rpg.wilderness.CloverWildernessMap.CloverWildernessTile;
import gartham.c10ver.response.utils.DirectionSelector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.User;

public class WildernessGame {

	private final Instant timestamp = Instant.now();

	private final User target;
	private final GuildMessageChannel channel;
	private final Clover clover;
	private final CloverWildernessMap cwm = new CloverWildernessMap();
	private CloverWildernessTile tile = cwm.getOrigin();
	private final DirectionSelector dirsel = new DirectionSelector();
	private boolean active;

	public boolean isActive() {
		return active;
	}

	public WildernessGame(User target, GuildMessageChannel channel, Clover clover) {
		this.target = target;
		this.channel = channel;
		this.clover = clover;
		channel.sendMessageEmbeds(new EmbedBuilder().setDescription("```" + tile.tilemapString() + "```")
				.setAuthor(target.getAsTag() + "'s Exploration").build()).setActionRows(dirsel.actionRows()).complete();
	}

	public Instant getTimestamp() {
		return timestamp;
	}

}
