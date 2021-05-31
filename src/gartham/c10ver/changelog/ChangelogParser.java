package gartham.c10ver.changelog;

import zeale.mouse.utils.BufferedParser;
import zeale.mouse.utils.CharacterParser;
import zeale.mouse.utils.Parser;

public class ChangelogParser extends BufferedParser<String> {

	private final CharacterParser charpar;

	public ChangelogParser(Parser<Character> charpar) {
		this.charpar = CharacterParser.from(charpar);
	}

	@Override
	protected String read() {
		boolean esc = false;
		var strbr = new StringBuilder();
		int nxt = charpar.nxt();
		if (nxt == -1)
			return null;
		while (true) {
			switch (nxt) {
			case -1:
				return strbr.toString();
			case '\\':
				if (!(esc ^= true))
					strbr.append('\\');
				break;
			case '\n':
			case '\r':
				if (esc) {
					strbr.append('\n');
					esc = false;
				} else
					return strbr.toString();
			default:
				if (esc) {
					strbr.append('\\');
					esc = false;
				}
				strbr.append((char) nxt);
			}
			nxt = charpar.nxt();
		}
	}

}
