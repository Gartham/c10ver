package gartham.c10ver.response;

import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.Component.Type;

public class MutableButton {
	private Button button;

	public MutableButton(Button button) {
		this.button = button;
	}

	public MutableButton(String id) {
		this(Button.secondary(id, Utilities.ZERO_WIDTH_SPACE));
	}

	public MutableButton setID(String id) {
		button = button.withId(id);
		return this;
	}

	public MutableButton setStyle(ButtonStyle style) {
		button = button.withStyle(style);
		return this;
	}

	public MutableButton setLabel(String label) {
		button = button.withLabel(label);
		return this;
	}

	public Type getType() {
		return button.getType();
	}

	public String getId() {
		return button.getId();
	}

	public String getLabel() {
		return button.getLabel();
	}

	public ButtonStyle getStyle() {
		return button.getStyle();
	}

	public Emoji getEmoji() {
		return button.getEmoji();
	}

	public boolean isDisabled() {
		return button.isDisabled();
	}

	public MutableButton disable() {
		button = button.asDisabled();
		return this;
	}

	public MutableButton enable() {
		button = button.asEnabled();
		return this;
	}

	public MutableButton setDisabled(boolean disabled) {
		button = button.withDisabled(disabled);
		return this;
	}

	public MutableButton setEmoji(Emoji emoji) {
		button = button.withEmoji(emoji);
		return this;
	}

	public MutableButton setButton(Button button) {
		this.button = button;
		return this;
	}

	public Button getButton() {
		return button;
	}

}
