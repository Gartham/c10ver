package gartham.c10ver;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.strings.matching.Matching;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Clover {

	private final JDA bot;
	private final CommandParser commandParser;
	private final CommandProcessor commandProcessor = new CommandProcessor();
	private final EventHandler eventHandler = new EventHandler(this);

	{
		commandProcessor.register(new Command() {
			@Override
			public boolean match(CommandInvocation inv) {
				return "pay".equalsIgnoreCase(inv.cmdName) || "transfer".equalsIgnoreCase(inv.cmdName);
			}

			@Override
			public void exec(CommandInvocation inv) {
				inv.event.getChannel().sendMessage("Sorry, that's not yet supported.").queue();
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

	public Clover(String token) throws LoginException {
		this(JDABuilder.createLight(token).build());
	}

	public Clover(JDA jda) {
		bot = jda;
		commandParser = new CommandParser(Matching.build("~").or(
				Matching.build("<@").possibly("!").then(bot.getSelfUser().getId() + ">").then(Matching.whitespace())));
		bot.addEventListener(eventHandler);
	}

	public static void main(String[] args) throws LoginException {
		try (var s = new Scanner(Clover.class.getResourceAsStream("token.txt"))) {
			new Clover(s.nextLine());
		}
	}
}
