package gartham.c10ver;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInvocation {
	public final String cmdName;
	public final String[] args;
	public final MessageReceivedEvent event;

	private final String prefix;

	public CommandInvocation(String prefix, String cmdName, MessageReceivedEvent event, String... args) {
		this.prefix = prefix;
		this.cmdName = cmdName;
		this.args = args;
		this.event = event;
	}

	public String[] getArgs() {
		return args;
	}

	public String getCmdName() {
		return cmdName;
	}

	public String getPrefix() {
		return prefix;
	}

}
