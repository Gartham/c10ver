package gartham.c10ver.response.actions;

import java.util.Iterator;

import gartham.c10ver.response.menus.MenuMessage;
import net.dv8tion.jda.api.EmbedBuilder;

public class SimpleMenuMessage<A extends ActionReaction> extends MenuMessage<A> {

	@SafeVarargs
	public SimpleMenuMessage(A... actions) {
		super(new ActionMessage<>(actions));
	}

	public SimpleMenuMessage(Iterable<A> actions) {
		super(new ActionMessage<>(actions));
	}

	public SimpleMenuMessage(Iterator<A> actions) {
		super(new ActionMessage<>(actions));
	}

	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		for (int i = 0; i < getActions().size(); i++)
			builder.appendDescription(
					getActions().get(i).getEmoji() == null ? ActionMessage.EMOJIS[i] : getActions().get(i).getEmoji())
					.appendDescription(" ").appendDescription(getActions().get(i).getName()).appendDescription("\n");
	}

}
