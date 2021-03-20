package gartham.c10ver.processing.trading;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;

public class RemoveCommand extends MatchBasedCommand {

	private final Trade trade;

	public RemoveCommand(Trade trade) {
		super("-");
		this.trade = trade;
	}

	@Override
	public void exec(CommandInvocation inv) {

	}

}
