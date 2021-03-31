package gartham.c10ver.changelog;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alixia.chatroom.api.QuickList;

import gartham.c10ver.Clover;
import gartham.c10ver.changelog.Changelog.Version.Change.Type;
import zeale.mouse.utils.CharacterParser;
import zeale.mouse.utils.Parser;

public class Changelog {

	public static Changelog from(Parser<String> lines) {
		List<Version> vers = new ArrayList<>();
		while (lines.peek() != null)
			vers.add(Version.parse(lines));// Parse a whole version out.
		return new Changelog(vers);
	}

	public static Changelog from(InputStream is) {
		return from(new ChangelogParser(CharacterParser.from(
				new InputStreamReader(Clover.class.getResourceAsStream("changelog.txt"), StandardCharsets.UTF_8))));
	}

	private final List<Version> versions;

	private Changelog(List<Version> versions) {
		this.versions = versions;
	}

	public List<Version> getVersions() {
		return Collections.unmodifiableList(versions);
	}

	public static class Version {

		private static Version parse(Parser<String> lines) {
			String verstr = lines.next(), title = null;
			int i = verstr.indexOf(' ');
			if (i != -1) {// The optional substring " - " is expected after a version identifier to
							// include the version's name.
				title = verstr.substring(i + 3);
				verstr = verstr.substring(0, i);
			}
			List<Change> changes = new ArrayList<>(2);
			String n;
			LOOP: while ((n = lines.next()) != null) {
				if (!n.isEmpty()) {// Ignore empty lines.
					switch (n.charAt(0)) {
					case '+':
						changes.add(new Change(Type.ADDITION, n.substring(2)));
						continue;
					case '-':
						changes.add(new Change(Type.REMOVAL, n.substring(2)));
						continue;
					case '~':
						changes.add(new Change(Type.CHANGE, n.substring(2)));
						continue;
					case '*':
						changes.add(new Change(Type.FIX, n.substring(2)));
						continue;
					default:
						break LOOP;
					}
				}
			}
			return new Version(verstr, title, changes);
		}

		private final String verstr, title;

		public String getVerstr() {
			return verstr;
		}

		public String getTitle() {
			return title;
		}

		public List<Change> getChanges() {
			return Collections.unmodifiableList(changes);
		}

		private final List<Change> changes;

		private Version(String verstr, String title, List<Change> changes) {
			this.verstr = verstr;
			this.title = title;
			this.changes = changes;
		}

		private Version(String verstr, String title, Change... changes) {
			this.verstr = verstr;
			this.title = title;
			this.changes = new QuickList<>(changes);
		}

		public static class Change {
			private final Type type;
			private final String content;

			public Type getType() {
				return type;
			}

			public String getContent() {
				return content;
			}

			private Change(Type type, String content) {
				this.type = type;
				this.content = content;
			}

			public enum Type {
				ADDITION, REMOVAL, CHANGE, FIX;
			}

		}
	}
}
