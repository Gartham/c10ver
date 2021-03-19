package gartham.c10ver.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alixia.javalibrary.strings.matching.Matching;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandParser {

	public CommandParser(Matching matching) {
		this.matching = matching;
	}

	private Matching matching;

	public Matching getMatching() {
		return matching;
	}

	public void setMatching(Matching matching) {
		this.matching = matching;
	}

	public CommandInvocation parse(Matching matching, String text, MessageReceivedEvent event) {
		String fullCommand = text;
		text = matching == null ? text : matching.match(text);
		String prefix = fullCommand.substring(0, fullCommand.length() - text.length());

		if (matching != null && prefix.isEmpty())
			return null;

		String cmdName = null;

		StringBuilder currentText = new StringBuilder();

		List<String> args = new ArrayList<>(2);

		for (int i = 0; i < text.length(); i++) {
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
			} else {
				currentText.append(text.charAt(i));
			}
		}

		if (cmdName != null)
			args.add(currentText.toString());
		else
			cmdName = currentText.toString();

		return new CommandInvocation(prefix, cmdName, event, args.toArray(new String[args.size()]));
	}

	public CommandInvocation parse(String text, MessageReceivedEvent event) {
		return parse(matching, text, event);
	}
}
