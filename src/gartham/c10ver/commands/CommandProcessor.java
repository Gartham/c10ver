package gartham.c10ver.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandProcessor {
	private List<Command> commands = new ArrayList<>();

	public boolean run(CommandInvocation inv) {
		for (Command c : commands)
			if (c.match(inv)) {
				c.exec(inv);
				return true;
			}
		return false;
	}

	public void register(Command comm) {
		commands.add(comm);
	}
}
