package gartham.c10ver.commands;

import org.alixia.javalibrary.strings.matching.Matching;

public abstract class MatchBasedCommand implements Command {

	protected final Matching matching;

	public MatchBasedCommand(Matching matching) {
		this.matching = matching;
	}

	public MatchBasedCommand(String... aliases) {
		this(Matching.ignoreCase(aliases));
	}

	@Override
	public final boolean match(CommandInvocation inv) {
		return matching.fullyMatches(inv.cmdName);
	}

}
