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
	}

	public SimpleActionMessage(Action... actions) {
		this(new EmbedBuilder(), actions);
	}

	@Override
	public MessageEmbed embed() {
		return builder.build();
	}

}
