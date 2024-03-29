package gartham.c10ver.response.buttonbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.response.MutableButton;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class ButtonBox implements Iterable<ButtonBox.Button> {
	private final Button[][] buttons = new Button[5][5];
	{
		for (int i = 0; i < buttons.length; i++)
			for (int j = 0; j < buttons[i].length; j++)
				buttons[i][j] = new Button(String.valueOf(i * 5 + j));
	}

	public ButtonBox copy(Button[][] grid) {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				buttons[i][j].mimic(grid[i][j]);
		return this;
	}

	public ButtonBox copy(ButtonBox other) {
		return copy(other.buttons);
	}

	public ButtonBox update(Button[][] grid) {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (grid[i][j].present)
					buttons[i][j].mimic(grid[i][j]);
		return this;
	}

	public ButtonBox update(ButtonBox other) {
		return update(other.buttons);
	}

	public Button get(int x, int y) {
		return buttons[x][y];
	}

	public List<ActionRow> rows() {
		List<ActionRow> ar = new ArrayList<>();
		for (var bb : buttons) {
			List<net.dv8tion.jda.api.interactions.components.Button> buttons = new ArrayList<>();
			for (var b : bb)
				if (b.isPresent())
					buttons.add(b.getButton());
			if (!buttons.isEmpty())
				ar.add(ActionRow.of(buttons));
		}
		return ar;
	}

	public class Button extends MutableButton {

		private boolean present;
		{
			enable();
		}

		public boolean isPresent() {
			return present;
		}

		public Button setPresent(boolean present) {
			this.present = present;
			return this;
		}

		public Button(net.dv8tion.jda.api.interactions.components.Button button) {
			super(button);
		}

		public Button(String id) {
			super(id);
		}

		public Button mimic(Button other) {
			present = other.present;
			super.mimic(other);
			return this;
		}

	}

	@Override
	public Iterator<Button> iterator() {
		return JavaTools.iterator(buttons);
	}

}
