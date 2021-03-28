package gartham.c10ver.processing.trading;

import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.CommandParser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TradeCommandParser extends CommandParser {

	public TradeCommandParser() {
		super(null);
	}

	@Override
	public CommandInvocation parse(Matching matching, String text, MessageReceivedEvent event) {
		String fullCommand = text;
		text = matching == null ? text : matching.match(text);
		String prefix = fullCommand.substring(0, fullCommand.length() - text.length());

		if (matching != null && prefix.isEmpty())
			return null;

		String cmdName = null;
		StringBuilder currentText = new StringBuilder();
		List<String> args = new ArrayList<>(2);
		int i = 0;

		if (text.length() != 0 && !isNormalChar(text.charAt(i))) {
			do
				currentText.append(text.charAt(i++));
			while (i < text.length() && !isNormalChar(text.charAt(i)));
			cmdName = currentText.toString();
			currentText = new StringBuilder();
		}

		for (; i < text.length(); i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				if (cmdName != null) {
					args.add(currentText.toString());
					currentText = new StringBuilder();
				} else {
					cmdName = currentText.toString();
					currentText = new StringBuilder();
				}
				for (i++; Character.isWhitespace(text.charAt(i)); i++)
					;
				i--;
			} else
				currentText.append(text.charAt(i));
		}

		if (cmdName != null)
			args.add(currentText.toString());
		else
			cmdName = currentText.toString();

		return new CommandInvocation(prefix, cmdName, event, args.toArray(new String[args.size()]));
	}

	protected boolean isNormalChar(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c);
	}

}
