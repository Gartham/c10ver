package gartham.c10ver.response.buttonbox.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import gartham.c10ver.response.buttonbox.ButtonBox;
import gartham.c10ver.response.buttonbox.ButtonBox.Button;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ButtonPage extends Page {

	/**
	 * Returns <code>true</code> if this {@link Page} contains a component with the
	 * specified ID.
	 * 
	 * @param buttonID The button ID of the component.
	 * @return <code>true</code> if contained, <code>false</code> otherwise.
	 */
	boolean containsComponent(String buttonID);

	/**
	 * Handles a {@link ButtonClickEvent} caused by one of the {@link Button}s
	 * tracked by this {@link ButtonPage} having been clicked.
	 * 
	 * @param event the {@link ButtonClickEvent}. This should be handled in full.
	 */
	void handle(ButtonClickEvent event);

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, String pageContents) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setContent(pageContents).queue(), consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, EmbedBuilder builder) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(builder.build()).queue(), consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, String pageContents,
			EmbedBuilder builder) {
		return new SimpleButtonPage(buttongrid,
				(event, buttonbox, menu) -> event.editComponents(buttonbox.update(buttongrid).rows())
						.setEmbeds(builder.build()).setContent(pageContents).queue(),
				consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, EmbedBuilder... builder) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> {
			MessageEmbed[] me = new MessageEmbed[builder.length];
			for (int i = 0; i < builder.length; i++)
				me[i] = builder[i].build();
			event.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(me).queue();
		}, consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, String pageContents,
			EmbedBuilder... builder) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> {
			MessageEmbed[] me = new MessageEmbed[builder.length];
			for (int i = 0; i < builder.length; i++)
				me[i] = builder[i].build();
			event.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(me).setContent(pageContents).queue();
		}, consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, MessageEmbed embed) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embed).queue(), consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, MessageEmbed... embeds) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embeds).queue(), consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, String pageContents,
			MessageEmbed embed) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embed).setContent(pageContents).queue(),
				consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, Button[][] buttongrid, String pageContents,
			MessageEmbed... embeds) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embeds).setContent(pageContents).queue(),
				consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, String pageContents) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> {
			event.editComponents(buttonbox.update(buttongrid).rows()).setContent(pageContents).queue();
		}, consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, EmbedBuilder builder) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(builder.build()).queue(), consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, String pageContents,
			EmbedBuilder builder) {
		return new SimpleButtonPage(buttongrid,
				(event, buttonbox, menu) -> event.editComponents(buttonbox.update(buttongrid).rows())
						.setEmbeds(builder.build()).setContent(pageContents).queue(),
				consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, EmbedBuilder... builder) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> {
			MessageEmbed[] me = new MessageEmbed[builder.length];
			for (int i = 0; i < builder.length; i++)
				me[i] = builder[i].build();
			event.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(me).queue();
		}, consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, String pageContents,
			EmbedBuilder... builder) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> {
			MessageEmbed[] me = new MessageEmbed[builder.length];
			for (int i = 0; i < builder.length; i++)
				me[i] = builder[i].build();
			event.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(me).setContent(pageContents).queue();
		}, consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, MessageEmbed embed) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embed).queue(), consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, MessageEmbed... embeds) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embeds).queue(), consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, String pageContents,
			MessageEmbed embed) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embed).setContent(pageContents).queue(),
				consumer);
	}

	static SimpleButtonPage page(Consumer<ButtonClickEvent> consumer, ButtonBox buttongrid, String pageContents,
			MessageEmbed... embeds) {
		return new SimpleButtonPage(buttongrid, (event, buttonbox, menu) -> event
				.editComponents(buttonbox.update(buttongrid).rows()).setEmbeds(embeds).setContent(pageContents).queue(),
				consumer);
	}

	class SimpleButtonPage implements ButtonPage {

		private final List<Button> buttons = new ArrayList<>();
		private final Page page;
		private final Consumer<ButtonClickEvent> consumer;

		public SimpleButtonPage(ButtonBox buttongrid, Page page, Consumer<ButtonClickEvent> consumer) {
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++) {
					var x = buttongrid.get(i, j);
					if (x != null)
						buttons.add(x);
				}
			this.page = page;
			this.consumer = consumer;
		}

		public SimpleButtonPage(Button[][] buttongrid, Page page, Consumer<ButtonClickEvent> consumer) {
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++) {
					var x = buttongrid[i][j];
					if (x != null)
						buttons.add(x);
				}
			this.page = page;
			this.consumer = consumer;
		}

		@Override
		public void update(ButtonClickEvent event, ButtonBox buttongrid, Menu<?> menu) {
			page.update(event, buttongrid, menu);
		}

		@Override
		public boolean containsComponent(String buttonID) {
			for (var b : buttons)
				if (b.getId().equals(buttonID))
					return true;
			return false;
		}

		@Override
		public void handle(ButtonClickEvent event) {
			consumer.accept(event);// Actually handles the button click.
		}

	}

}
