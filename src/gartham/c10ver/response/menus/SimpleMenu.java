package gartham.c10ver.response.menus;

import java.util.List;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.response.menus.SimpleMenu.SimpleMenuItem;
import gartham.c10ver.utils.MessageActionHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class SimpleMenu extends Menu<SimpleMenuItem> {

	public SimpleMenu(InputProcessor<ButtonClickEvent> processor) {
		super(processor, SimpleMenuItem.class);
	}

	public SimpleMenu(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor) {
		super(mah, processor, SimpleMenuItem.class);
	}

	public class SimpleMenuItem extends Menu<SimpleMenuItem>.MenuItem {

		private final String description;

		public SimpleMenuItem(String emoji, String label, String description, String id) {
			super(Button.secondary(id, emoji).withLabel(label));
			this.description = description;
		}

		public SimpleMenuItem(Button button, String description) {
			super(button);
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
	protected void process(MessageAction action, List<SimpleMenuItem> items) {
		action.setEmbeds(generateEmbed(items).build());
	}
}
