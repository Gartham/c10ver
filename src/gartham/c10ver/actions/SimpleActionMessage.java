package gartham.c10ver.actions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SimpleActionMessage extends ActionMessage {

	private final EmbedBuilder builder;

	public EmbedBuilder getBuilder() {
		return builder;
	}

	public SimpleActionMessage(EmbedBuilder builder, Action... actions) {
		super(actions);
		this.builder = builder;
		builder.appendDescription("\n");
		for (int i = 0; i < actions.length; i++)
			builder.appendDescription(actions[i].getEmoji() == null ? ActionMessage.EMOJIS[i] : actions[i].getEmoji())
					.appendDescription(" ").appendDescription(actions[i].getDescription()).appendDescription("\n");
	}

	public SimpleActionMessage(Action... actions) {
		this(new EmbedBuilder(), actions);
	}

	public SimpleActionMessage(String desc, Action... actions) {
		this(new EmbedBuilder().setAuthor(desc), actions);
	}

	@Override
	public MessageEmbed embed() {
		return builder.build();
	}

}
