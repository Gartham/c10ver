package gartham.c10ver;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.commands.Command;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.CommandParser;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.economy.Economy;
import gartham.c10ver.events.EventHandler;
import gartham.c10ver.users.User;
import gartham.c10ver.utils.FormattingUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Clover {

	private final JDA bot;
	private final CommandParser commandParser;
	private final CommandProcessor commandProcessor = new CommandProcessor();
	private final EventHandler eventHandler = new EventHandler(this);
	private final Economy economy = new Economy(new File("data/economy"));

	{
		commandProcessor.register(new Command() {
			@Override
			public boolean match(CommandInvocation inv) {
				return ("daily".equalsIgnoreCase(inv.cmdName));
			}

			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();

				User u = economy.getUser(userid);
				if (u.timeSinceLastDaily().toDays() < 1)
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ FormattingUtils.formatLargest(u.timeSinceLastDaily(), 3)
									+ "` before running that command.")
							.queue();
				else {
					u.dailyInvoked();
					long amt = (long) (Math.random() * 25 + 10);
					u.getAccount().deposit(amt);
					inv.event.getChannel().sendMessage("You received `" + amt + "` garthcoins. You now have `"
							+ u.getAccount().getBalance().toPlainString() + "` garthcoins.").queue();
				}

			}
		});

		commandProcessor.register(new Command() {

			@Override
			public boolean match(CommandInvocation inv) {
				return "weekly".equalsIgnoreCase(inv.cmdName);

			}

			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = economy.getUser(userid);
				if (u.timeSinceLastWeekly().toDays() < 7) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ FormattingUtils.formatLargest(u.timeSinceLastWeekly(), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.weeklyInvoked();
					long amt = (long) (Math.random() * 250 + 100);
					u.getAccount().deposit(amt);
					inv.event.getChannel().sendMessage("You received `" + amt + "` garthcoins. You now have `"
							+ u.getAccount().getBalance().toPlainString() + "` garthcoins.").queue();
				}
			}
		});

		commandProcessor.register(new Command() {

			@Override
			public boolean match(CommandInvocation inv) {
				return "monthly".equalsIgnoreCase(inv.cmdName);

			}

			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = economy.getUser(userid);
				if (u.timeSinceLastWeekly().toDays() < 7) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ FormattingUtils.formatLargest(u.timeSinceLastWeekly(), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.weeklyInvoked();
					long amt = (long) (Math.random() * 10000 + 4000);
					u.getAccount().deposit(amt);
					inv.event.getChannel().sendMessage("You received `" + amt + "` garthcoins. You now have `"
							+ u.getAccount().getBalance().toPlainString() + "` garthcoins.").queue();
				}
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
