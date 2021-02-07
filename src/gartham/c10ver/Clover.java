package gartham.c10ver;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Clover {

	private final JDA bot;
	private final CommandParser commandParser = new CommandParser();
	private final CommandProcessor commandProcessor = new CommandProcessor();
	private final EventHandler eventHandler = new EventHandler(this);

	{
		commandProcessor.register(new Command() {

			@Override
			public boolean match(CommandInvocation inv) {
				return true;
			}

			@Override
			public void exec(CommandInvocation inv) {
				System.out.println("ABC");
			}
		});
	}

	public JDA getBot() {
		return bot;
	}

	public CommandParser getCommandParser() {
		return commandParser;
	}

	public CommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	private void prepare() {
		bot.addEventListener(eventHandler);
	}

	public Clover(String token) throws LoginException {
		bot = JDABuilder.createLight(token).build();
		prepare();
	}

	public Clover(JDA jda) {
		bot = jda;
		prepare();
	}

	public static void main(String[] args) throws LoginException {
		try (var s = new Scanner(Clover.class.getResourceAsStream("token.txt"))) {
			new Clover(s.nextLine());
		}
	}
}
