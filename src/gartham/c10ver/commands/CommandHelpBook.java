package gartham.c10ver.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CommandHelpBook {

	public CommandHelpBook() {
	}

	public CommandHelpBook(int helpsPerPage) {
		this.helpsPerPage = helpsPerPage;
	}

	public abstract class CommandHelp {
		protected final String name, description, aliases[];

		public CommandHelp(String name, String description, String... aliases) {
			this.name = name;
			this.description = description;
			this.aliases = aliases;
		}

		public abstract void print(EmbedBuilder builder);

		public abstract void print(MessageChannel channel);

	}

	public final class SimpleCommandHelp extends CommandHelp {
		private final String usage;

		public SimpleCommandHelp(String name, String description, String usage, String... aliases) {
			super(name, description, aliases);
			this.usage = usage;
		}

		public void print(EmbedBuilder builder) {
			String desc = '*' + description + "*\nUsage: `" + usage + '`';
			if (aliases.length != 0)
				desc += "\nAliases: " + aliasesToString(true, aliases);
			builder.addField(name, desc, false);
		}

		public void print(MessageChannel channel) {
			final EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(new Color(255, 254, 255));
			builder.setAuthor(name, null, channel.getJDA().getSelfUser().getAvatarUrl())
					.appendDescription("Showing help for command, `" + name.replace("`", "\\`") + "`:");
			print(builder);
			channel.sendMessage(builder.build()).queue();
		}
	}

	public final class ParentCommandHelp extends CommandHelp {
		private final String[] subcommands;
		private final List<CommandHelp> subcmds = new ArrayList<>();

		public ParentCommandHelp(String name, String description, String[] aliases, String... subcommands) {
			super(name, description, aliases);
			this.subcommands = subcommands;
		}

		@Override
		public void print(EmbedBuilder builder) {
			String desc = '*' + description + '*';
			if (aliases.length != 0)
				desc += "\nAliases: " + aliasesToString(true, aliases);
			if (subcommands.length != 0)
				desc += "\nSubcommands: " + aliasesToString(true, subcommands);
			desc += "\nType: Category";
			builder.addField(name, desc, false);
		}

		@Override
		public void print(MessageChannel channel) {
			final EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(name, null, null);
			String desc = '*' + description + '*';
			if (aliases.length != 0)
				desc += "\nAliases: " + aliasesToString(true, aliases);
			if (subcommands.length != 0)
				desc += "\nSubcommands: " + aliasesToString(true, subcommands);
			desc += "\nType: `category`\nSubcommands...\n\u200B";
			builder.appendDescription(desc);
			for (CommandHelp ch : subcmds)
				ch.print(builder);
			channel.sendMessage(builder.build()).queue();
		}

		public CommandHelp addSubcommand(String name, String description, String usage, String... aliases) {
			final CommandHelp help = new SimpleCommandHelp(name, description, usage, aliases);
			subcmds.add(help);
			return help;
		}

	}

	private int helpsPerPage = 3;

	private final List<CommandHelp> helps = new ArrayList<>();

	public int getHelpsPerPage() {
		return helpsPerPage;
	}

	public void setHelpsPerPage(int helpsPerPage) {
		this.helpsPerPage = helpsPerPage;
	}

	public void addCommand(CommandHelp help) {
		helps.add(help);
	}

	public CommandHelp addCommand(String name, String description, String usage, String... aliases) {
		final CommandHelp help = new SimpleCommandHelp(name, description, usage, aliases);
		helps.add(help);
		return help;
	}

	public ParentCommandHelp addCommand(String name, String description, String[] aliases, String... subcommands) {
		final ParentCommandHelp pch = new ParentCommandHelp(name, description, aliases, subcommands);
		helps.add(pch);
		return pch;
	}

	/**
	 * Converts a list of aliases into a readable, comma-separated string. If
	 * <code>wrap</code> is true, this method will also wrap each alias in graves
	 * (<code>`</code>).
	 *
	 * @param wrap    Whether or not to wrap each printed alias in graves.
	 * @param aliases The array of aliases.
	 * @return A readable string representing the aliases.
	 */
	private String aliasesToString(boolean wrap, String... aliases) {
		if (aliases.length == 0)
			return "";
		final StringBuilder builder = new StringBuilder();
		builder.append(wrap ? '`' + aliases[0] + '`' : aliases[0]);
		for (int i = 1; i < aliases.length; i++)
			builder.append(", " + (wrap ? '`' + aliases[i] + '`' : aliases[i]));
		return builder.toString();
	}

	public void print(MessageChannel channel, int page) {
		final int item = (page - 1) * helpsPerPage;
		final int maxPage = (helps.size() + helpsPerPage - 1) / helpsPerPage;
		if (page < 1 || page > maxPage)
			channel.sendMessage(new EmbedBuilder()
					.appendDescription(
							"That page does not exist. The final page of help commands is page `" + maxPage + "`.")
					.build()).queue();
		else {
			final EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(new Color(255, 254, 255));
			builder.appendDescription("Showing page `" + page + "` out of `" + maxPage + "` of help.\n\u200B");
			for (int i = item; i < item + helpsPerPage;) {
				helps.get(i).print(builder);
				if (++i >= helps.size()) {
					builder.setFooter("End of help reached.");
					channel.sendMessage(builder.build()).queue();
					return;
				}
			}
			if (page < maxPage)
				builder.setFooter("Type `help " + (page + 1) + "` to view the next page.");
			channel.sendMessage(builder.build()).queue();
		}
	}

	public boolean print(MessageChannel channel, String command, boolean allowAliases, boolean ignoreCase) {
		if (allowAliases) {
			if (ignoreCase) {
				for (final CommandHelp ch : helps)
					if (ch.name.equalsIgnoreCase(command)) {
						ch.print(channel);
						return true;
					} else
						for (final String s : ch.aliases)
							if (s.equalsIgnoreCase(command)) {
								ch.print(channel);
								return true;
							}
			} else
				for (final CommandHelp ch : helps)
					if (ch.name.equals(command)) {
						ch.print(channel);
						return true;
					} else
						for (final String s : ch.aliases)
							if (s.equals(command)) {
								ch.print(channel);
								return true;
							}
		} else if (ignoreCase) {
			for (final CommandHelp ch : helps)
				if (ch.name.equalsIgnoreCase(command)) {
					ch.print(channel);
					return true;
				}
		} else
			for (final CommandHelp ch : helps)
				if (ch.name.equals(command)) {
					ch.print(channel);
					return true;
				}
		return false;
	}

	public boolean print(MessageChannel channel, boolean allowAliases, boolean ignoreCase, String... commands) {
		List<CommandHelp> helps = this.helps;
		CommandHelp hlp = null;
		ROOT: for (String s : commands) {
			if (hlp instanceof ParentCommandHelp)
				helps = ((ParentCommandHelp) hlp).subcmds;
			else if (hlp != null)
				return false;
			if (allowAliases) {
				if (ignoreCase) {
					for (final CommandHelp ch : helps)
						if (ch.name.equalsIgnoreCase(s)) {
							hlp = ch;
							continue ROOT;
						} else
							for (final String s1 : ch.aliases)
								if (s.equalsIgnoreCase(s1)) {
									hlp = ch;
									continue ROOT;
								}
				} else
					for (final CommandHelp ch : helps)
						if (ch.name.equals(s)) {
							hlp = ch;
							continue ROOT;
						} else
							for (final String s1 : ch.aliases)
								if (s.equals(s1)) {
									hlp = ch;
									continue ROOT;
								}
			} else if (ignoreCase) {
				for (final CommandHelp ch : helps)
					if (ch.name.equalsIgnoreCase(s)) {
						hlp = ch;
						continue ROOT;
					}
			} else
				for (final CommandHelp ch : helps)
					if (ch.name.equals(s)) {
						hlp = ch;
						continue ROOT;
					}
			return false;
		}

		hlp.print(channel);

		return true;
	}
}
