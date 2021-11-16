package gartham.c10ver.games.rpg;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;

public class GARPGCommand extends MatchBasedCommand {

	private final Clover clover;

	public GARPGCommand(Clover clover) {
		super("explore", "rpg");
		this.clover = clover;
	}

	@Override
	public void exec(CommandInvocation inv) {
		if (inv.event.isFromGuild() && clover.getEconomy().hasServer(inv.event.getGuild().getId())) {
			var serv = clover.getEconomy().getServer(inv.event.getGuild().getId());
			if (inv.event.getChannel().getId().equals(serv.getRPGChannel())) {
				handleSafariInvocation(inv);
				return;
			}
		}
		handleGeneralInvocation(inv);
	}

	private void handleSafariInvocation(CommandInvocation inv) {
		// TODO Handle command being used in the rpg channel.
	}

	private void handleGeneralInvocation(CommandInvocation inv) {
		// TODO Handle command being used in non-rpg channel.
	}

}
