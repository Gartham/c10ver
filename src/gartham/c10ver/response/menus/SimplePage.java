package gartham.c10ver.response.menus;

import java.util.Collection;
import java.util.List;

import gartham.c10ver.response.menus.Menu.Page;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public class SimplePage extends Page<SimplePage.MenuItem> {

	public SimplePage(Menu menu) {
		menu.super();
	}

	public class MenuItem extends Page<MenuItem>.MenuItem {
		private final String description;

		public MenuItem(Button button, String description) {
			super(button);
			this.description = description;
		}

		public MenuItem(String emoji, String label, String id, ButtonStyle style, String description) {
			super(emoji, label, id, style);
			this.description = description;
		}

		public MenuItem(String emoji, String label, String id, String description) {
			super(emoji, label, id, ButtonStyle.SECONDARY);
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	@Override
	protected Collection<MessageEmbed> generateEmbeds() {
		EmbedBuilder eb = new EmbedBuilder();
		for (var s : getItems())
			eb.addField(s.getEmoji().getAsMention() + " | " + s.getLabel(),
					s instanceof MenuItem ? ((MenuItem) s).getDescription() : "\u200b", false);
		return List.of(eb.build());
	}
}
