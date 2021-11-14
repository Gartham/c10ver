package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.games.rpg.RPGUtils;
import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.DetailedActionButton;
import gartham.c10ver.response.actions.DetailedActionReaction;
import gartham.c10ver.response.menus.DetailedMenuMessage;
import net.dv8tion.jda.api.EmbedBuilder;

public class GarmonActionMessage extends DetailedMenuMessage<DetailedActionReaction, DetailedActionButton> {
	private final GarmonFighter creature;

	public GarmonActionMessage(GarmonFighter creature, DetailedActionButton[] buttons,
			DetailedActionReaction... reactions) {
		super(new ActionMessage<>(buttons, reactions));
		this.creature = creature;
	}

	public GarmonActionMessage(GarmonFighter creature, DetailedActionReaction... reactions) {
		super(new ActionMessage<>(reactions));
		this.creature = creature;
	}

	public GarmonActionMessage(GarmonFighter creature, DetailedActionReaction[] reactions,
			DetailedActionButton... buttons) {
		super(new ActionMessage<>(reactions, buttons));
		this.creature = creature;
	}

	@Override
	protected void buildEmbed(EmbedBuilder eb) {
		super.buildEmbed(eb);
		eb.setTitle(creature.getName() + "'s Turn").setThumbnail(creature.getHeadshot());
		eb.setDescription("It's your team's turn. **" + creature.getName()
				+ "** is up! Choose one of the following options to make your move. (The `info` option does NOT count as a move.)\n\n**"
				+ creature.getName() + "**\nHealth: " + creature.getHealthString()
				+ RPGUtils.calcHealthbar(creature.getHealth(), creature.getMaxHealth()));
	}

}
