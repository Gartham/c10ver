package gartham.c10ver.response.buttonbox;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class ButtonBox {
	private final ButtonBoxButton[][] buttons = new ButtonBoxButton[5][5];

	public ButtonBoxButton get(int x, int y) {
		return buttons[x][y];
	}

	public List<ActionRow> rows() {
		List<ActionRow> ar = new ArrayList<>();
		for (var bb : buttons) {
			List<Button> buttons = new ArrayList<>();
			for (var b : bb)
				if (b.isPresent())
					buttons.add(b.getButton());
			if (!buttons.isEmpty())
				ar.add(ActionRow.of(buttons));
		}
		return ar;
	}

}
