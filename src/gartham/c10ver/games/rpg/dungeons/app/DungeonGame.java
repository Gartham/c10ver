package gartham.c10ver.games.rpg.dungeons.app;

import java.awt.Color;
import java.math.BigInteger;
import java.util.List;

import gartham.apps.garthchat.api.execution.Action;
import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.valuables.VoteToken;
import gartham.c10ver.economy.users.EconomyUser;
import gartham.c10ver.economy.users.EconomyUser.Receipt;
import gartham.c10ver.games.rpg.dungeons.Dungeon;
import gartham.c10ver.games.rpg.dungeons.DungeonRoom;
import gartham.c10ver.games.rpg.dungeons.EnemyRoom;
import gartham.c10ver.games.rpg.dungeons.LootRoom;
import gartham.c10ver.games.rpg.fighting.battles.app.CreatureAI;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonBattle;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonTeam;
import gartham.c10ver.games.rpg.fighting.battles.app.PlayerController;
import gartham.c10ver.games.rpg.fighting.battles.app.PlayerFighter;
import gartham.c10ver.response.menus.ButtonBook;
import gartham.c10ver.response.utils.DirectionSelector;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class DungeonGame {

	public static class DungeonCover {
		private final String graphic;
		private final String name;

		public DungeonCover(String name, String graphic) {
			this.graphic = graphic;
			this.name = name;
		}

		public String getGraphic() {
			return graphic;
		}

		public String getName() {
			return name;
		}

	}

	private final User target;
	private final TextChannel channel;
	private final InputProcessor<ButtonClickEvent> buttonProcessor;
	private final Clover clover;

	/**
	 * @param target          The target {@link User} to prompt. (This {@link User}
	 *                        will be able to make a selection.)
	 * @param channel         The {@link MessageChannel} to prompt the user in.
	 * @param buttonProcessor The {@link InputProcessor} on which the button clicks
	 *                        for the {@link ButtonBook} prompt will be received.
	 *                        The {@link Action} will wait until the
	 *                        {@link InputProcessor} receives the clicks, and will
	 *                        then result in a page number.
	 */
	public DungeonGame(User target, TextChannel channel, InputProcessor<ButtonClickEvent> buttonProcessor,
			Clover clover) {
		this.target = target;
		this.channel = channel;
		this.buttonProcessor = buttonProcessor;
		this.clover = clover;
	}

	public void start(DungeonCover... dungeons) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(new Color(0x5E2A03));
		eb.setTitle("World Selector").setDescription("Select a world to explore " + target.getAsMention() + ":")
				.setImage(dungeons[0].graphic).setFooter("Page 1 of " + dungeons.length);

		var bb = new ButtonBook(buttonProcessor);
		bb.add("\u2705");
		bb.setTarget(target);
		bb.setMaxPage(dungeons.length - 1);
		bb.setEdgeButtons(true);
		bb.setPageHandler((t, e) -> e.getHook().editOriginalEmbeds(
				eb.setImage(dungeons[t].graphic).setFooter("Page " + (t + 1) + " of " + (bb.getMaxPage() + 1)).build())
				.queue());

		var abb = bb.attachAndSend(channel.sendMessageEmbeds(eb.build()));
		abb.setHandler(t -> {
			t.deferEdit().complete();
			abb.unregister();
			startDungeon(t);
		});
	}

	Message t;

	private void startDungeon(ButtonClickEvent e) {
		t = e.getMessage();// This is broken lmao.
//		try {
//			Thread.sleep((long) (Math.random() * 3500 + 1500));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		var dungeon = Dungeon.simpleEasyDungeon();
		var initialRoom = dungeon.getInitialRoom();

		var dirsel = new CustomDirsel();
		dirsel.disableManaged();
		for (var d : initialRoom.getConnectionDirectionsUnmodifiable())
			dirsel.enable(d);

		var inp = new InputConsumer<ButtonClickEvent>() {

			DungeonRoom currentRoom = initialRoom;

			@Override
			public boolean consume(ButtonClickEvent event, InputProcessor<? extends ButtonClickEvent> processor,
					InputConsumer<ButtonClickEvent> consumer) {
				if (event.getMessageIdLong() != t.getIdLong())
					return false;
				if (event.getUser().getIdLong() != target.getIdLong()) {
					event.reply("That's not for you. >:(").setEphemeral(true).queue();
					return true;
				}

				if (event.getComponentId().equals("act")) {
					if (currentRoom.isClaimed()) {
						event.reply(
								"You already collected the loot from this room... (You're not supposed to be able to click that button again!)")
								.setEphemeral(true).queue();
						return true;
					}
					currentRoom.setClaimed(true);
					var user = clover.getEconomy().getUser(target.getId());
					Receipt receipt;

					var lr = (LootRoom) currentRoom;
					receipt = user.reward(lr.getRewards().autoSetMultipliers(user,
							channel.getType().isGuild() ? ((GuildChannel) channel).getGuild() : null));

					currentRoom.prepare(dirsel, "act");
					event.editComponents(dirsel.actionRows()).queue();
					t.reply(target.getAsMention() + ", you earned:\n\n" + Utilities.listRewards(receipt)).queue();

					// Resend message? Send ephemeral rewards?
				} else if (event.getComponentId().equals("repeat")) {
					var dsn = new CustomDirsel();
					dsn.disableManaged();
					var s = channel.sendMessageEmbeds(event.getMessage().getEmbeds())
							.setActionRows(event.getMessage().getActionRows());
					event.editComponents(dsn.actionRows()).queue(x -> s.queue(q -> t = q));
				} else {
					var dir = DirectionSelector.getDirectionSelected(event);
					currentRoom = currentRoom.getRoom(dir);
					if (currentRoom == null) {// Player moved in the wrong direction.
						event.reply("You are not supposed to be able to click that!").setEphemeral(true).queue();
						processor.removeInputConsumer(consumer);
					} else if (!currentRoom.isClaimed() && currentRoom instanceof EnemyRoom) {
						// TODO Player has encountered a fight. Force them into a
						// battle before they can continue.

						var room = (EnemyRoom) currentRoom;

						currentRoom.setClaimed(true);

						GarmonTeam player = new GarmonTeam(target.getAsTag(),
								new PlayerFighter(target.getName(), target.getEffectiveAvatarUrl(),
										BigInteger.valueOf(25), BigInteger.valueOf(100), BigInteger.valueOf(100),
										BigInteger.valueOf(25), BigInteger.valueOf(5)));
						var team = room.getEnemies();
						GarmonBattle battle = new GarmonBattle(player, team);
						player.setController(new PlayerController(battle, clover, target, channel));
						team.setController(new CreatureAI(battle, channel));

						dirsel.reset();
						dirsel.disableManaged();
						event.editComponents(dirsel.actionRows()).setContent("**You got into a fight!!!**")
								.setEmbeds(new EmbedBuilder().setColor(new Color(0xFF0000))
										.setTitle("Room #" + (dungeon.index(currentRoom) + 1))
										.setDescription("```" + currentRoom.getRoom().tilemapString() + "```")
										.setFooter("Choose a path.").build())
								.queue();
						battle.startAsync(true, winner -> {

							if (winner == player) {
								EconomyUser user = clover.getEconomy().getUser(target.getId());
								RewardsOperation rewop = RewardsOperation.build(user, channel.getGuild(),
										BigInteger.valueOf((long) (Math.random() * 142 + 25)),
										new ItemBunch<>(new VoteToken(
												gartham.c10ver.economy.items.valuables.VoteToken.Type.NORMAL)));
								currentRoom.prepare(dirsel, "act");
								channel.sendMessage(target.getAsMention() + ", you won the fight!\nYou earned:\n\n"
										+ Utilities.listRewards(user.reward(rewop)))
										.setEmbeds(new EmbedBuilder().setColor(new Color(0xFF00))
												.setTitle("Room #" + (dungeon.index(currentRoom) + 1))
												.setDescription("```" + currentRoom.getRoom().tilemapString() + "```")
												.setFooter("Choose a path.").build())
										.setActionRows(dirsel.actionRows()).queue(x -> t = x);
							}
						});

					} else {
						currentRoom.prepare(dirsel, "act");
						event.editMessageEmbeds(new EmbedBuilder().setTitle("Room #" + (dungeon.index(currentRoom) + 1))
								.setDescription("```" + currentRoom.getRoom().tilemapString() + "```")
								.setFooter("Choose a path.").build()).setActionRows(dirsel.actionRows()).setContent("")
								.queue();
					}
				}
				return true;

			}
		};

		clover.getEventHandler().getButtonClickProcessor().registerInputConsumer(inp);

		t.editMessage(new MessageBuilder().setEmbeds(new EmbedBuilder().setTitle("W1-1")
				.setDescription("```" + initialRoom.getRoom().tilemapString() + "```").setFooter("Choose a path.")
				.build()).setActionRows(dirsel.actionRows()).build()).queue();
	}

	private static class CustomDirsel extends DirectionSelector {

		private boolean repeatDisabled;

		public void disableRepeat() {
			repeatDisabled = true;
		}

		public void enableRepeat() {
			repeatDisabled = false;
		}

		/**
		 * Disables all the {@link Button}s that are managed by this
		 * {@link CustomDirsel}.
		 */
		public void disableManaged() {
			disableDirections();
			disableRepeat();
		}

		private Button repeatButton() {
			var rpb = Button.primary("repeat", Emoji.fromMarkdown("\uD83D\uDD01")).withLabel("Resend");
			if (repeatDisabled)
				rpb = rpb.asDisabled();
			return rpb;
		}

		@Override
		public List<ActionRow> actionRows() {
			List<ActionRow> rows = super.actionRows();
			rows.add(ActionRow.of(repeatButton()));
			return rows;
		}
	}

	/**
	 * Returns an {@link Action} of prompting the target user in the specified
	 * channel for what dungeon they would like to play. The returned {@link Action}
	 * sends the built prompt to the user and waits for a reply from the user,
	 * returning the selected page.
	 * 
	 * @param dungeons The {@link DungeonCover}s to show to the user in the prompt.
	 * @return A new {@link Action} that, when invoked, prompts the user and waits
	 *         until the user has answered. The answer is the page number (dungeon
	 *         number) that the user has selected).
	 */
	public Action<Integer> promptDungeon(DungeonCover... dungeons) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(new Color(0x5E2A03));
		eb.setTitle("World Selector").setDescription("Select a world to explore " + target.getAsMention() + ":")
				.setImage(dungeons[0].graphic).setFooter("Page 1 of " + dungeons.length);

		var bb = new ButtonBook(buttonProcessor);
		bb.add("\u2705");
		bb.setTarget(target);
		bb.setMaxPage(dungeons.length - 1);
		bb.setEdgeButtons(true);
		bb.setPageHandler((t, e) -> e.getHook().editOriginalEmbeds(
				eb.setImage(dungeons[t].graphic).setFooter("Page " + (t + 1) + " of " + (bb.getMaxPage() + 1)).build())
				.queue());

		return new Action<>() {

			private int res;

			@Override
			public Integer perform() {

				synchronized (this) {
					var abb = bb.attachAndSend(channel.sendMessageEmbeds(eb.build()));
					abb.setHandler(t -> {
						synchronized (this) {
							res = abb.getPage();
							notify();
							t.deferEdit().complete();
							abb.unregister();
						}
					});
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}

				return res;
			}

		};

//		abb.setHandler(t -> {
//
//			t.getInteraction().editComponents(abb.disabledButtonView()).queue();
//
//		});

	}

}
