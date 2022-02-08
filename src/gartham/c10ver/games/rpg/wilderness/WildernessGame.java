package gartham.c10ver.games.rpg.wilderness;

import java.time.Instant;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.games.rpg.wilderness.CloverWildernessMap.CloverWildernessTile;
import gartham.c10ver.games.rpg.wilderness.LinkType.AdjacencyLink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class WildernessGame {

	private final Instant timestamp = Instant.now();

	private final User target;
	private final GuildMessageChannel channel;
	private final Clover clover;
	private final CloverWildernessMap cwm = new CloverWildernessMap();
	private CloverWildernessTile tile = cwm.getOrigin();
	private boolean active;

	private final WildernessGamepad gamepad = new WildernessGamepad();

	public boolean isActive() {
		return active;
	}

	public WildernessGame(User target, GuildMessageChannel channel, Clover clover) {
		this.target = target;
		this.channel = channel;
		this.clover = clover;
		var msg = channel.sendMessageEmbeds(genEmbed()).setActionRows(gamepad.actionRows()).complete();
		clover.getEventHandler().getButtonClickProcessor().registerInputConsumer(new InputConsumer<>() {

			@Override
			public boolean consume(ButtonClickEvent event, InputProcessor<? extends ButtonClickEvent> processor,
					InputConsumer<ButtonClickEvent> consumer) {
				if (event.getUser().getIdLong() != target.getIdLong()) {
					event.reply("That's not for you, that's for " + target.getAsMention() + ".").setEphemeral(true)
							.queue();
					return true;
				} else if (event.getMessageIdLong() != msg.getIdLong())
					return false;
				switch (event.getComponentId()) {
				case "left":
					tile = tile.traverse(AdjacencyLink.LEFT);
					break;
				case "right":
					tile = tile.traverse(AdjacencyLink.RIGHT);
					break;
				case "up":
					tile = tile.traverse(AdjacencyLink.TOP);
					break;
				case "down":
					tile = tile.traverse(AdjacencyLink.BOTTOM);
				default:
					break;
				}
				event.editMessageEmbeds(genEmbed()).complete();
				return true;
			}
		});
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	private MessageEmbed genEmbed() {
		return new EmbedBuilder().setDescription("\n\n```" + tile.tilemapString() + "```")
				.setAuthor(target.getAsTag() + "'s Exploration").setFooter("Chunk: " + tile.getX() + ", " + tile.getY())
				.build();
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
