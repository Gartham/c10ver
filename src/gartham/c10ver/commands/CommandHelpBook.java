package gartham.c10ver.commands;

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

	public final class CommandHelp {
		private final String name, description, usage, aliases[];

		public CommandHelp(String name, String description, String usage, String... aliases) {
			this.name = name;
			this.description = description;
			this.usage = usage;
			this.aliases = aliases;
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
		final CommandHelp help = new CommandHelp(name, description, usage, aliases);
		helps.add(help);
		return help;
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

	public void print(EmbedBuilder builder, CommandHelp help) {
		builder.addField(help.name, "Description: " + help.description + "\n__Usage__: `" + help.usage + "`\nAliases: "
				+ aliasesToString(true, help.aliases) + "\n\u200B", false);
	}

	public void print(MessageChannel channel, CommandHelp help) {
		final EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(null, null, channel.getJDA().getSelfUser().getAvatarUrl())
				.appendDescription("Showing help for command, `" + help.name.replace("`", "\\`") + "`:");
		print(builder, help);
		channel.sendMessage(builder.build()).queue();
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
			builder.appendDescription("Showing page `" + page + "` out of `" + maxPage + "` of help.\n\u200B");
			for (int i = item; i < item + helpsPerPage;) {
				print(builder, helps.get(i));
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
						print(channel, ch);
						return true;
					} else
						for (final String s : ch.aliases)
							if (s.equalsIgnoreCase(command)) {
								print(channel, ch);
								return true;
							}
			} else
				for (final CommandHelp ch : helps)
					if (ch.name.equals(command)) {
						print(channel, ch);
						return true;
					} else
						for (final String s : ch.aliases)
							if (s.equals(command)) {
								print(channel, ch);
								return true;
							}
		} else if (ignoreCase) {
			for (final CommandHelp ch : helps)
				if (ch.name.equalsIgnoreCase(command)) {
					print(channel, ch);
					return true;
				}
		} else
			for (final CommandHelp ch : helps)
				if (ch.name.equals(command)) {
					print(channel, ch);
					return true;
				}
		return false;
	}

}
