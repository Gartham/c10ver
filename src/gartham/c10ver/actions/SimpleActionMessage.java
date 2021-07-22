package gartham.c10ver.actions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SimpleActionMessage<A extends Action> extends ActionMessage<A> {

	private final EmbedBuilder builder;

	public EmbedBuilder getBuilder() {
		return builder;
	}

	@SafeVarargs
	public SimpleActionMessage(EmbedBuilder builder, A... actions) {
		super(actions);
		this.builder = builder;
		builder.appendDescription("\n");
		for (int i = 0; i < actions.length; i++)
			builder.appendDescription(actions[i].getEmoji() == null ? ActionMessage.EMOJIS[i] : actions[i].getEmoji())
					.appendDescription(" ").appendDescription(actions[i].getDescription()).appendDescription("\n");
	}

	@SafeVarargs
	public SimpleActionMessage(A... actions) {
		this(new EmbedBuilder(), actions);
	}

	@SafeVarargs
	public SimpleActionMessage(String desc, A... actions) {
		this(new EmbedBuilder().setAuthor(desc), actions);
	}

	@Override
	public MessageEmbed embed() {
		return builder.build();
	}

}
