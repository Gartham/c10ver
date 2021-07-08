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
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < actions.length; i++)
			sb.append(ActionMessage.EMOJIS[i]).append(' ').append(actions[i].getDescription());
		builder.setDescription(sb);
	}

	public SimpleActionMessage(Action... actions) {
		this(new EmbedBuilder(), actions);
	}

	@Override
	public MessageEmbed embed() {
		return builder.build();
	}

}
