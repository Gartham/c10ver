package gartham.c10ver.commands.subcommands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;

public abstract class ParentCommand extends MatchBasedCommand {

	protected abstract void tailed(CommandInvocation inv);

	private final Set<Subcommand> subcommands = new HashSet<>();

	public ParentCommand(Matching matching) {
		super(matching);
	}

	public ParentCommand(String... aliases) {
		super(aliases);
	}

	public abstract class Subcommand extends ParentCommand {

		{
			subcommands.add(this);
		}

		public Subcommand(Matching matching) {
			super(matching);
		}

		public Subcommand(String... aliases) {
			super(aliases);
		}

		@Override
		protected final void tailed(CommandInvocation inv) {
			tailed((SubcommandInvocation) inv);
		}

		protected abstract void tailed(SubcommandInvocation si);

	}

	@Override
	public final void exec(CommandInvocation inv) {
		if (inv.args.length == 0)
			tailed(inv);
		else {
			SubcommandInvocation si = new SubcommandInvocation(inv.getPrefix(), inv.args[0], inv.event,
					inv instanceof SubcommandInvocation
							? JavaTools.addToArray(((SubcommandInvocation) inv).preargs, inv.cmdName)
							: new String[] { inv.cmdName },
					Arrays.copyOfRange(inv.args, 1, inv.args.length));
			for (var s : subcommands)
				if (s.match(si)) {
					s.exec(si);
					return;
				}
		}
		tailed(inv);
	}

}
