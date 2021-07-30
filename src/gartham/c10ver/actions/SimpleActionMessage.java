package gartham.c10ver.actions;

import java.util.Iterator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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

	protected void buildEmbed(EmbedBuilder builder) {
		for (int i = 0; i < getActions().size(); i++)
			builder.appendDescription(
					getActions().get(i).getEmoji() == null ? ActionMessage.EMOJIS[i] : getActions().get(i).getEmoji())
					.appendDescription(" ").appendDescription(getActions().get(i).getName()).appendDescription("\n");
	}

	@Override
	public final MessageEmbed embed() {
		var emb = new EmbedBuilder();
		buildEmbed(emb);
		return emb.build();
	}

}
