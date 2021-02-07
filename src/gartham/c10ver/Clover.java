package gartham.c10ver;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.commands.Command;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.CommandParser;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.economy.Economy;
import gartham.c10ver.events.EventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Clover {

	private final JDA bot;
	private final CommandParser commandParser;
	private final CommandProcessor commandProcessor = new CommandProcessor();
	private final EventHandler eventHandler = new EventHandler(this);
	private final Economy economy = new Economy();

	{
		commandProcessor.register(new Command() {
			@Override
			public boolean match(CommandInvocation inv) {
				return "get-cash".equalsIgnoreCase(inv.cmdName);
			}

			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				economy.getAccount(userid).pay(50);

				inv.event.getChannel().sendMessage("Your balance has increased by `" + 50
						+ "` credits. Your balance is now: " + economy.getAccount(userid).getBalance().toPlainString())
						.queue();
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
