package gartham.c10ver.commands;

public interface Command {
	boolean match(CommandInvocation inv);

	void exec(CommandInvocation inv);
}
