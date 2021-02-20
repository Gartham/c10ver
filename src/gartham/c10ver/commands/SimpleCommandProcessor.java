package gartham.c10ver.commands;

import gartham.c10ver.commands.CommandHelpBook.CommandHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class SimpleCommandProcessor extends CommandProcessor {
	protected final CommandHelpBook help = new CommandHelpBook(3);

	public void printHelp(EmbedBuilder builder, CommandHelp help) {
		this.help.print(builder, help);
	}

	public void printHelp(MessageChannel channel, CommandHelp help) {
		this.help.print(channel, help);
	}

	public void printHelp(MessageChannel channel, int page) {
		help.print(channel, page);
	}

	public boolean printHelp(MessageChannel channel, String command, boolean allowAliases, boolean ignoreCase) {
		return help.print(channel, command, allowAliases, ignoreCase);
	}

	{
		final CommandHelp helpCommandHelp = help.addCommand("help", "Shows help for commands.",
				"help [page-number|command-name]", "?");
		register(new MatchBasedCommand("help", "?") {
			@Override
			public void exec(CommandInvocation inv) {
				int page = 1;
				FIRST_ARG: if (inv.args.length == 1) {
					String arg;
					if (!inv.args[0].startsWith("\\"))
						try {
							page = Integer.parseInt(inv.args[0]);
							break FIRST_ARG;
						} catch (final NumberFormatException e) {
							arg = inv.args[0];
						}
					else
						arg = inv.args[0].substring(1);
					if (!printHelp(inv.event.getChannel(), arg, true, true))
						inv.event.getChannel()
								.sendMessage("No command with the name or alias: \"" + arg + "\" was found.").queue();
					return;
				} else if (inv.args.length > 1) {
					inv.event.getChannel().sendMessage("Illegal number of arguments for command: " + inv.cmdName)
							.queue();
					printHelp(inv.event.getChannel(), helpCommandHelp);
					return;
				}
				printHelp(inv.event.getChannel(), page);

			}
		});
	}
}
