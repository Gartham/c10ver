package gartham.c10ver.games.rpg.dungeons.app;

import java.awt.Color;

import gartham.apps.garthchat.api.execution.Action;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.response.menus.ButtonBook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

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

	/**
	 * Returns an {@link Action} of prompting the target user in the specified
	 * channel for what dungeon they would like to play. The returned {@link Action}
	 * sends the built prompt to the user and waits for a reply from the user,
	 * returning the selected page.
	 * 
	 * @param target    The target {@link User} to prompt. (This {@link User} will
	 *                  be able to make a selection.)
	 * @param channel   The {@link MessageChannel} to prompt the user in.
	 * @param processor The {@link InputProcessor} on which the button clicks for
	 *                  the {@link ButtonBook} prompt will be received. The
	 *                  {@link Action} will wait until the {@link InputProcessor}
	 *                  receives the clicks, and will then result in a page number.
	 * @param dungeons  The {@link DungeonCover}s to show to the user in the prompt.
	 * @return A new {@link Action} that, when invoked, prompts the user and waits
	 *         until the user has answered. The answer is the page number (dungeon
	 *         number) that the user has selected).
	 */
	public Action<Integer> promptDungeon(User target, MessageChannel channel,
			InputProcessor<ButtonClickEvent> processor, DungeonCover... dungeons) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(new Color(0x5E2A03));
		eb.setTitle("World Selector").setDescription("Select a world to explore " + target.getAsMention() + ":")
				.setImage(dungeons[0].graphic).setFooter("Page 1 of " + dungeons.length);

		var bb = new ButtonBook(processor);
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
							abb.complete();
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
//			t.getChannel().sendMessage("Generating a randomized dungeon...").queue(new Consumer<>() {
//
//				private Message t;
//
//				@Override
//				public void accept(Message msg) {
//					t = msg;
//					t.getChannel().sendTyping().queue(new Consumer<>() {
//
//						@Override
//						public void accept(Void v) {
//							try {
//								Thread.sleep((long) (Math.random() * 3500 + 1500));
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//
//							var dungeon = Dungeon.simpleEasyDungeon();
//							var initialRoom = dungeon.getInitialRoom();
//
//							var dirsel = DirectionSelector.directionSelector();
//							dirsel.disableDirections();
//							for (var d : initialRoom.getConnectionDirectionsUnmodifiable())
//								dirsel.enable(d);
//
//							var inp = new InputConsumer<ButtonClickEvent>() {
//
//								DungeonRoom currentRoom = initialRoom;
//
//								@Override
//								public boolean consume(ButtonClickEvent event,
//										InputProcessor<? extends ButtonClickEvent> processor,
//										InputConsumer<ButtonClickEvent> consumer) {
//									if (event.getMessageIdLong() != t.getIdLong())
//										return false;
//									if (event.getUser().getIdLong() != inv.event.getAuthor().getIdLong()) {
//										event.reply("That's not for you. >:(").setEphemeral(true).queue();
//										return true;
//									}
//
//									if (event.getComponentId().equals("act")) {
//										if (currentRoom.isClaimed()) {
//											event.reply(
//													"You already collected the loot from this room... (You're not supposed to be able to click that button again!)")
//													.setEphemeral(true).queue();
//											return true;
//										}
//										currentRoom.setClaimed(true);
//										var user = clover.getEconomy().getUser(inv.event.getAuthor().getId());
//										Receipt receipt;
//
//										var lr = (LootRoom) currentRoom;
//										receipt = user.reward(lr.getRewards().autoSetMultipliers(user,
//												inv.event.isFromGuild() ? inv.event.getGuild() : null));
//
//										currentRoom.prepare(dirsel, "act");
//										event.editComponents(dirsel.actionRows()).queue();
//										t.reply(inv.event.getAuthor().getAsMention() + ", you earned:\n\n"
//												+ Utilities.listRewards(receipt)).queue();
//
//										// Resend message? Send ephemeral rewards?
//									} else {
//										var dir = DirectionSelector.getDirectionSelected(event);
//										currentRoom = currentRoom.getRoom(dir);
//										if (currentRoom == null) {// Player moved in the wrong direction.
//											event.reply("You are not supposed to be able to click that!")
//													.setEphemeral(true).queue();
//											processor.removeInputConsumer(consumer);
//										} else if (!currentRoom.isClaimed() && currentRoom instanceof EnemyRoom) {
//											// TODO Player has encountered a fight. Force them into a
//											// battle before they can continue.
//
//											var room = (EnemyRoom) currentRoom;
//
//											currentRoom.setClaimed(true);
//
//											GarmonTeam player = new GarmonTeam(inv.event.getAuthor().getAsTag(),
//													new PlayerFighter(inv.event.getAuthor().getName(),
//															inv.event.getAuthor().getEffectiveAvatarUrl(),
//															BigInteger.valueOf(25), BigInteger.valueOf(100),
//															BigInteger.valueOf(100), BigInteger.valueOf(25),
//															BigInteger.valueOf(5)));
//											var team = room.getEnemies();
//											GarmonBattle battle = new GarmonBattle(player, team);
//											player.setController(new PlayerController(battle, clover,
//													inv.event.getAuthor(), inv.event.getTextChannel()));
//											team.setController(new CreatureAI(battle, inv.event.getTextChannel()));
//
//											dirsel.reset();
//											dirsel.disableDirections();
//											event.editComponents(dirsel.actionRows())
//													.setContent("**You got into a fight!!!**")
//													.setEmbeds(new EmbedBuilder().setColor(new Color(0xFF0000))
//															.setTitle("W" + (abb.getPage() + 1) + "-"
//																	+ (dungeon.index(currentRoom) + 1))
//															.setDescription("```"
//																	+ currentRoom.getRoom().tilemapString() + "```")
//															.setFooter("Choose a path.").build())
//													.queue();
//											battle.startAsync(true, new Consumer<GarmonTeam>() {
//
//												@Override
//												public void accept(GarmonTeam winner) {
//
//													if (winner == player) {
//														EconomyUser user = clover.getEconomy()
//																.getUser(inv.event.getAuthor().getId());
//														RewardsOperation rewop = RewardsOperation.build(user,
//																inv.event.getGuild(),
//																BigInteger.valueOf((long) (Math.random() * 142 + 25)),
//																new ItemBunch<>(new VoteToken(
//																		gartham.c10ver.economy.items.valuables.VoteToken.Type.NORMAL)));
//														currentRoom.prepare(dirsel, "act");
//														inv.event.getChannel()
//																.sendMessage(inv.event.getAuthor().getAsMention()
//																		+ ", you won the fight!\nYou earned:\n\n"
//																		+ Utilities.listRewards(user.reward(rewop)))
//																.setEmbeds(new EmbedBuilder()
//																		.setColor(new Color(0xFF00))
//																		.setTitle("W" + (abb.getPage() + 1) + "-"
//																				+ (dungeon.index(currentRoom) + 1))
//																		.setDescription("```"
//																				+ currentRoom.getRoom().tilemapString()
//																				+ "```")
//																		.setFooter("Choose a path.").build())
//																.setActionRows(dirsel.actionRows()).queue(x -> t = x);
//													}
//												}
//											});
//
//										} else {
//											currentRoom.prepare(dirsel, "act");
//											event.editMessageEmbeds(new EmbedBuilder()
//													.setTitle("W" + (abb.getPage() + 1) + "-"
//															+ (dungeon.index(currentRoom) + 1))
//													.setDescription(
//															"```" + currentRoom.getRoom().tilemapString() + "```")
//													.setFooter("Choose a path.").build())
//													.setActionRows(dirsel.actionRows()).setContent("").queue();
//										}
//									}
//									return true;
//
//								}
//							};
//
//							clover.getEventHandler().getButtonClickProcessor().registerInputConsumer(inp);
//
//							t.editMessage(
//									new MessageBuilder()
//											.setEmbeds(new EmbedBuilder().setTitle("W1-1")
//													.setDescription(
//															"```" + initialRoom.getRoom().tilemapString() + "```")
//													.setFooter("Choose a path.").build())
//											.setActionRows(dirsel.actionRows()).build())
//									.queue();
//						}
//					});
//				}
//			});
//
//		});

	}

	public void start(User target, MessageChannel channel, InputProcessor<ButtonClickEvent> processor) {
	}

}
