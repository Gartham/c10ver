package gartham.c10ver;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.changelog.Changelog;
import gartham.c10ver.commands.CommandParser;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.economy.Economy;
import gartham.c10ver.events.EventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Clover {

	private final JDA bot;
	private final CommandParser commandParser;
	private final CommandProcessor commandProcessor = new CloverCommandProcessor(this);
	private final EventHandler eventHandler = new EventHandler(this);
	private final Economy economy = new Economy(new File("data/economy"), this);
	private final Changelog changelog;
	private final Set<String> devlist;
	private final List<String> wordlist;
	{
		Set<String> devlist = new HashSet<>();
		InputStream dl = Clover.class.getResourceAsStream("devlist.txt");
		if (dl == null)
			System.err.println("Couldn't find the devlist...");
		else
			try (var s = new Scanner(dl)) {
				while (s.hasNextLine())
					devlist.add(s.nextLine());
			}
		this.devlist = Collections.unmodifiableSet(devlist);

		Changelog changelog;
		try {
			changelog = Changelog.from(Clover.class.getResourceAsStream("changelog.txt"));
		} catch (Exception e) {
			System.err.println("FAILED TO LOAD THE CHANGELOG.");
			changelog = null;
		}
		this.changelog = changelog;

		List<String> wordlist = new ArrayList<>();
		InputStream wl = Clover.class.getResourceAsStream("words/wordlist.txt");
		if (wl == null)
			System.err.println("Couldn't find the worldlist...");
		else
			try (var sc = new Scanner(wl)) {
				while (sc.hasNextLine())
					wordlist.add(sc.nextLine());
			}
		this.wordlist = Collections.unmodifiableList(wordlist);
	}

	public Changelog getChangelog() {
		return changelog;
	}

	public boolean hasLoadedChangelog() {
		return changelog != null;
	}

	public Set<String> getDevlist() {
		return devlist;
	}

	public boolean isDev(String id) {
		return devlist.contains(id);
	}

	public boolean isDev(long id) {
		return devlist.contains(Long.toString(id));
	}

	public boolean isDev(User user) {
		return devlist.contains(user.getId());
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
		eventHandler.initialize();
	}

	public static void main(String[] args) throws LoginException {
		try (var s = new Scanner(Clover.class.getResourceAsStream("token.txt"))) {
			new Clover(s.nextLine());
		}
	}
}
