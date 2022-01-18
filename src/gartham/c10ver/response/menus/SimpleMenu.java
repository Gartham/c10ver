package gartham.c10ver.response.menus;

import java.util.Collection;
import java.util.List;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.response.menus.SimpleMenu.SimpleMenuItem;
import gartham.c10ver.utils.MessageActionHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class SimpleMenu extends Menu<SimpleMenuItem> {

	public SimpleMenu(InputProcessor<ButtonClickEvent> processor) {
		super(processor, SimpleMenuItem.class);
	}

	public SimpleMenu(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor) {
		super(mah, processor, SimpleMenuItem.class);
	}

	public class SimpleMenuItem extends Menu<SimpleMenuItem>.Page.MenuItem {

		private final String description;

		public SimpleMenuItem(Page owner, String emoji, String label, String description, String id) {
			owner.super(Button.secondary(id, emoji).withLabel(label));
			this.description = description;
		}

		public SimpleMenuItem(Page owner, Button button, String description) {
			owner.super(button);
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

	}

	protected EmbedBuilder generateEmbed(List<SimpleMenuItem> items) {
		EmbedBuilder eb = new EmbedBuilder();
		for (var s : items)
			eb.addField(s.getEmoji().getAsMention() + " | " + s.getLabel(), s.getDescription(), false);
		return eb;
	}

	@Override
	protected Collection<MessageEmbed> process(List<SimpleMenuItem> items) {
		return List.of(generateEmbed(items).build());
	}
}
