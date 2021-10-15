package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.games.rpg.RPGUtils;
import gartham.c10ver.response.actions.DetailedAction;
import gartham.c10ver.response.actions.DetailedActionMessage;
import net.dv8tion.jda.api.EmbedBuilder;

public class GarmonActionMessage extends DetailedActionMessage<DetailedAction> {
	private final GarmonFighter creature;

	public GarmonActionMessage(GarmonFighter creature, DetailedAction... actions) {
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
