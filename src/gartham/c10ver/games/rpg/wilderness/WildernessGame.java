package gartham.c10ver.games.rpg.wilderness;

import static gartham.c10ver.response.utils.DirectionSelector.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.Clover;
import gartham.c10ver.games.rpg.wilderness.CloverWildernessMap.CloverWildernessTile;
import gartham.c10ver.response.menus.ButtonBook;
import gartham.c10ver.response.menus.ButtonPaginator;
import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import gartham.c10ver.utils.MessageActionHandler.Group;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

public class WildernessGame {

	private final Instant timestamp = Instant.now();

	private final User target;
	private final GuildMessageChannel channel;
	private final Clover clover;
	private final CloverWildernessMap cwm = new CloverWildernessMap();
	private CloverWildernessTile tile = cwm.getOrigin();
	private boolean active;

	public boolean isActive() {
		return active;
	}

	public WildernessGame(User target, GuildMessageChannel channel, Clover clover) {
		this.target = target;
		this.channel = channel;
		this.clover = clover;
		channel.sendMessageEmbeds(new EmbedBuilder().setDescription("```" + tile.tilemapString() + "```")
				.setAuthor(target.getAsTag() + "'s Exploration").build()).setActionRows(buttonpad.generate())
				.complete();
	}

	public Instant getTimestamp() {
		return timestamp;
	}

}
