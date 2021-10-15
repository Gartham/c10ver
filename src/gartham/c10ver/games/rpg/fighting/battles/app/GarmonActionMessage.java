package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.games.rpg.RPGUtils;
import gartham.c10ver.response.actions.DetailedActionReaction;
import gartham.c10ver.response.menus.DetailedMenuMessage;
import net.dv8tion.jda.api.EmbedBuilder;

public class GarmonActionMessage extends DetailedMenuMessage<DetailedActionReaction> {
	private final GarmonFighter creature;

	public GarmonActionMessage(GarmonFighter creature, DetailedActionReaction... actions) {
		super(actions);
		this.creature = creature;
	}

	@Override
	protected void buildEmbed(EmbedBuilder eb) {
		super.buildEmbed(eb);
		eb.setTitle(creature.getName() + "'s Turn").setThumbnail(creature.getHeadshot());
		eb.setDescription("**" + creature.getName() + "**\nHealth: " + creature.getHealthString()
				+ RPGUtils.calcHealthbar(creature.getHealth(), creature.getMaxHealth()));
	}

}
