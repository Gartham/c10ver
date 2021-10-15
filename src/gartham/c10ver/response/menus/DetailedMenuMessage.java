package gartham.c10ver.response.menus;

import java.util.Iterator;

import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.DetailedActionReaction;
import net.dv8tion.jda.api.EmbedBuilder;

public class DetailedMenuMessage<D extends DetailedActionReaction> extends MenuMessage<D> {


	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		int i = 0;
		for (D d : getAm().getReactions()) {
			builder.addField(
					(d.getEmoji() == null ? ActionMessage.getNumericEmoji(i) : d.getEmoji()) + ' ' + d.getName(),
					d.getDetails(), true);
			i++;
		}
	}

}
