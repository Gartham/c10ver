package gartham.c10ver;

import java.io.File;
import java.util.EnumSet;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.commands.CommandParser;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.economy.Economy;
import gartham.c10ver.events.EventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Clover {

	private final JDA bot;
	private final CommandParser commandParser;
	private final CommandProcessor commandProcessor = new CloverCommandProcessor(this);
	private final EventHandler eventHandler = new EventHandler(this);
	private final Economy economy = new Economy(new File("data/economy"));

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

	public Economy getEconomy() {
		return economy;
	}

	public Clover(String token) throws LoginException {
		this(JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class)).build());
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
