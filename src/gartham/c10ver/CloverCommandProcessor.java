package gartham.c10ver;

import java.time.Duration;

import gartham.c10ver.commands.Command;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.users.User;
import gartham.c10ver.utils.FormattingUtils;

public class CloverCommandProcessor extends CommandProcessor {

	private final Clover clover;

	public CloverCommandProcessor(Clover clover) {
		this.clover = clover;
	}

	{
		register(new Command() {
			@Override
			public boolean match(CommandInvocation inv) {
				return ("daily".equalsIgnoreCase(inv.cmdName));
			}

			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();

				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastDaily().toDays() < 1)
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ FormattingUtils.formatLargest(Duration.ofDays(1).minus(u.timeSinceLastDaily()), 3)
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

		register(new Command() {

			@Override
			public boolean match(CommandInvocation inv) {
				return "weekly".equalsIgnoreCase(inv.cmdName);

			}

			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastWeekly().toDays() < 7) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention()
									+ ", you must wait `" + FormattingUtils
											.formatLargest(Duration.ofDays(7).minus(u.timeSinceLastWeekly()), 3)
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

		register(new Command() {

			@Override
			public boolean match(CommandInvocation inv) {
				return "monthly".equalsIgnoreCase(inv.cmdName);

			}

			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastWeekly().toDays() < 7) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention()
									+ ", you must wait `" + FormattingUtils
											.formatLargest(Duration.ofDays(30).minus(u.timeSinceLastWeekly()), 3)
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
}
