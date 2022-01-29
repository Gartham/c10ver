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

	private final MessageActionHandler buttonpad = new MessageActionHandler();
	private final List<Action> layoutActions = new ArrayList<>(16);
	private final Group main;
	{
		Action la1 = buttonpad.disabledButton(false), la2 = buttonpad.disabledButton(false),
				la3 = buttonpad.disabledButton(false), la4 = buttonpad.disabledButton(false),
				la5 = buttonpad.disabledButton(false), la6 = buttonpad.disabledButton(false);
		main = buttonpad.new Group(buttonpad.disabledButton(false), buttonpad.new Action(UP_ENABLED, false),
				buttonpad.disabledButton(false), la1, la2, buttonpad.new Action(LEFT_ENABLED, false),
				buttonpad.disabledButton(false), buttonpad.new Action(RIGHT_ENABLED, false), la3, la4,
				buttonpad.disabledButton(false), buttonpad.new Action(DOWN_ENABLED, false),
				buttonpad.disabledButton(false), la5, la6);
		layoutActions.add(la1);
		layoutActions.add(la2);
		layoutActions.add(la3);
		layoutActions.add(la4);
		layoutActions.add(la5);
		layoutActions.add(la6);
		for (int i = 0; i < 5; i++) {
			Action b = buttonpad.disabledButton(false);
			main.add(b);
			layoutActions.add(b);
		}

		main.add(buttonpad.new Action(ButtonBook.LEFT_ONE, false));

	}

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
