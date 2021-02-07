package gartham.c10ver;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

	public CommandInvocation parse(String text) {
		boolean haveParsedCmdName = false;
		String cmdName = "";

		StringBuilder currentText = new StringBuilder();

		List<String> args = new ArrayList<>(2);

		for (int i = 0; i < text.length(); i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				if (haveParsedCmdName) {
					args.add(currentText.toString());
					currentText = new StringBuilder();
				} else {
					haveParsedCmdName = true;
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

		if (haveParsedCmdName)
			args.add(currentText.toString());
		else
			cmdName = currentText.toString();

		return new CommandInvocation(cmdName, args.toArray(new String[args.size()]));
	}
}
