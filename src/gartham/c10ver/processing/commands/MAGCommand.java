package gartham.c10ver.processing.commands;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;

public class MAGCommand extends MatchBasedCommand {

	public MAGCommand() {
		super("mine", "mag", "mining");
	}

	@Override
	public void exec(CommandInvocation inv) {
		// Ask user where they want to play and set up a MAGSession.
		inv.event.getChannel().sendMessage("Where do you want to play?").queue();// TODO Merge

		// Now we need to list the various locations that the user can play at. We can
		// list the locations available to a player using the MAGController stored in
		// Clover.
	}

}
