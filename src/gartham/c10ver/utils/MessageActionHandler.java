package gartham.c10ver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.Component.Type;

public class MessageActionHandler {

	private final List<Action> actions = new ArrayList<>();

	public void convert(Function<Action, Action> converter) {
		var li = actions.listIterator();
		while (li.hasNext()) {
			var res = converter.apply(li.next());
			if (res == null)
				li.remove();
			else
				li.set(res);
		}
	}

	public class Action {
		private Button button;

		public Action(Button button) {
			if (button == null)
				throw null;
			this.button = button;
			actions.add(this);
		}

		public Button getButton() {
			return button;
		}

		public Action setButton(Button button) {
			this.button = button;
			return this;
		}

		public Action reposition(int newPos) {
			actions.remove(this);
			actions.add(newPos, this);
			return this;
		}

		public Action swap(int newPos) {
			int in = actions.indexOf(this);
			if (in == -1)
				throw new IllegalStateException("Can't swap an Action not in the handler.");
			Collections.swap(actions, in, newPos);
			return this;
		}

		public Action remove() {
			actions.remove(this);
			return this;
		}

		public Action add() {
			actions.add(this);
			return this;
		}

		public Action add(int pos) {
			actions.add(pos, this);
			return this;
		}

		public int row() {
			return actions.indexOf(this) / 5;
		}

		public int pos() {
			return actions.indexOf(this) % 5;
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

		public String getUrl() {
			return button.getUrl();
		}

		public Emoji getEmoji() {
			return button.getEmoji();
		}

		public boolean isDisabled() {
			return button.isDisabled();
		}

		public Action disable() {
			button = button.asDisabled();
			return this;
		}

		public Action enable() {
			button = button.asEnabled();
			return this;
		}

		public Action setDisabled(boolean disabled) {
			button = button.withDisabled(disabled);
			return this;
		}

		public Action setEmoji(Emoji emoji) {
			button = button.withEmoji(emoji);
			return this;
		}

		public Action setLabel(String label) {
			button = button.withLabel(label);
			return this;
		}

		public Action setId(String id) {
			button = button.withId(id);
			return this;
		}

		public Action setUrl(String url) {
			button = button.withUrl(url);
			return this;
		}

		public Action setStyle(ButtonStyle style) {
			button = button.withStyle(style);
			return this;
		}

	}

	public List<ActionRow> generate() {
		if (actions.size() > 25)
			throw new IllegalStateException("Can't have more than 25 buttons on a MessageActionHandler at once.");
		List<ActionRow> ar = new ArrayList<>(actions.size() / 5);
		int i = 0;
		var itr = actions.iterator();
		List<Button> b = new ArrayList<>(5);
		while (itr.hasNext()) {
			b.add(itr.next().getButton());
			if (++i == 5) {
				i = 0;
				ar.add(ActionRow.of(b));
				b = new ArrayList<>(5);
			}
		}
		if (!b.isEmpty())
			ar.add(ActionRow.of(b));

		return ar;
	}
}