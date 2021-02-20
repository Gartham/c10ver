package gartham.c10ver.commands.subcommands;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.utils.Unimplemented;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SubcommandInvocation extends CommandInvocation {

	public final String[] preargs;

	public SubcommandInvocation(String prefix, String cmdName, MessageReceivedEvent event, String[] preargs,
			String... args) {
		super(prefix, cmdName, event, args);
		this.preargs = preargs;
	}

	public String[] getPreargs() {
		return preargs;
	}

}
