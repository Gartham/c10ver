package gartham.c10ver.response.actions;

import java.util.Iterator;

import gartham.c10ver.response.menus.MenuMessage;
import net.dv8tion.jda.api.EmbedBuilder;

public class SimpleActionMessage<A extends Action> extends MenuMessage<A> {

	@SafeVarargs
	public SimpleActionMessage(A... actions) {
		super(new ActionMessage<>(actions));
	}

	public SimpleActionMessage(Iterable<A> actions) {
		super(new ActionMessage<>(actions));
	}

	public SimpleActionMessage(Iterator<A> actions) {
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
