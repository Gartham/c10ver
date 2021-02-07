package gartham.c10ver;

public class CommandInvocation {
	public final String cmdName;
	public final String[] args;

	public CommandInvocation(String cmdName, String... args) {
		this.cmdName = cmdName;
		this.args = args;
	}

	public String[] getArgs() {
		return args;
	}

	public String getCmdName() {
		return cmdName;
	}

}
