package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.actions.ActionMessage;
import gartham.c10ver.actions.DetailedAction;
import gartham.c10ver.actions.DetailedActionMessage;
import gartham.c10ver.games.rpg.RPGUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class GarmonActionMessage extends DetailedActionMessage<DetailedAction> {

	public GarmonActionMessage(GarmonFighter creature, DetailedAction... actions) {
		super(actions);
		eb.setTitle(creature.getName() + "'s Turn").setThumbnail(creature.getHeadshot());
		eb.setDescription("**" + creature.getName() + "**\nHealth: " + creature.getHealthString()
				+ RPGUtils.calcHealthbar(creature.getHealth(), creature.getMaxHealth()));

		for (var ga : actions)
			eb.addField(ga.getEmoji() + ' ' + ga.getName(), ga.getDescription(), true);
	}

	@Override
	public MessageEmbed embed() {
		return eb.build();
	}

}
