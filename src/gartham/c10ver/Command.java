package gartham.c10ver;

public interface Command {
	
	// pay @User 40
	// transfer @User 40
	
	boolean match(CommandInvocation inv);
	
	void exec(CommandInvocation inv);
}
