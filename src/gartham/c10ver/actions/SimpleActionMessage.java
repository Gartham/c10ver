package gartham.c10ver.actions;

import java.util.Iterator;

import net.dv8tion.jda.api.EmbedBuilder;

public class SimpleActionMessage<A extends Action> extends ActionMessage<A> {

	@SafeVarargs
	public SimpleActionMessage(A... actions) {
		super(actions);
	}

	public SimpleActionMessage(Iterable<A> actions) {
		super(actions);
	}

	public SimpleActionMessage(Iterator<A> actions) {
		super(actions);
	}

	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		for (int i = 0; i < getActions().size(); i++)
			builder.appendDescription(
					getActions().get(i).getEmoji() == null ? ActionMessage.EMOJIS[i] : getActions().get(i).getEmoji())
					.appendDescription(" ").appendDescription(getActions().get(i).getName()).appendDescription("\n");
	}

}
