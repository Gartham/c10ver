package gartham.c10ver.commands;

import gartham.c10ver.commands.CommandHelpBook.CommandHelp;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class SimpleCommandProcessor extends CommandProcessor {
	protected final CommandHelpBook help = new CommandHelpBook();

	public void printHelp(EmbedBuilder builder, CommandHelp help) {
		help.print(builder);
	}

	public void printHelp(MessageChannel channel, CommandHelp help) {
		help.print(channel);
	}

	public void printHelp(MessageChannel channel, int page) {
		help.print(channel, page);
	}

	public boolean printHelp(MessageChannel channel, String command, boolean allowAliases, boolean ignoreCase) {
		return help.print(channel, command, allowAliases, ignoreCase);
	}

	{
		help.addCommand("help", "Shows help for commands.", "help [page-number|(command [subcommand...])]", "?");
		register(new MatchBasedCommand("help", "?") {
			@Override
			public void exec(CommandInvocation inv) {
				int page = 1;
				if (inv.args.length == 1) {
					String arg;
					if (!inv.args[0].startsWith("\\"))
						try {
							page = Integer.parseInt(inv.args[0]);
							printHelp(inv.event.getChannel(), page);
							return;
						} catch (final NumberFormatException e) {
							arg = inv.args[0];
						}
					else
						arg = inv.args[0].substring(1);
					if (!printHelp(inv.event.getChannel(), arg, true, true))
						inv.event.getChannel().sendMessage(
								"No command with the name or alias: \"" + Utilities.strip(arg) + "\" was found.")
								.queue();
				} else if (inv.args.length > 1) {
					if (!help.print(inv.event.getChannel(), true, true, inv.args))
						inv.event.getChannel().sendMessage("No (sub)commands found that matched that: `"
								+ Utilities.strip(String.join(" ", inv.args)) + "`.").queue();
				} else
					printHelp(inv.event.getChannel(), 1);

			}
		});
	}
}
