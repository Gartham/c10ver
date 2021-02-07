package gartham.c10ver;

import java.util.ArrayList;
import java.util.List;

public class CommandProcessor {
	private List<Command> commands = new ArrayList<>();

	public void run(CommandInvocation inv) {
		for (Command c : commands)
			if (c.match(inv)) {
				c.exec(inv);
				return;
			}
	}

	public void register(Command comm) {
		commands.add(comm);
	}
}
