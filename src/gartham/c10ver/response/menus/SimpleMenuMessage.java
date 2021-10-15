package gartham.c10ver.response.menus;

import java.util.Iterator;

import gartham.c10ver.response.actions.ActionButton;
import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.ActionReaction;
import net.dv8tion.jda.api.EmbedBuilder;

public class SimpleMenuMessage<R extends ActionReaction, B extends ActionButton> extends MenuMessage<R, B> {

	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		for (int i = 0; i < getActions().size(); i++)
			builder.appendDescription(
					getActions().get(i).getEmoji() == null ? ActionMessage.EMOJIS[i] : getActions().get(i).getEmoji())
					.appendDescription(" ").appendDescription(getActions().get(i).getName()).appendDescription("\n");
	}

}
