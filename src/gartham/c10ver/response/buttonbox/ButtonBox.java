package gartham.c10ver.response.buttonbox;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class ButtonBox {
	private final ButtonBoxButton[][] buttons = new ButtonBoxButton[5][5];
	{
		for (int i = 0; i < buttons.length; i++)
			for (int j = 0; j < buttons[i].length; j++)
				buttons[i][j] = new ButtonBoxButton(String.valueOf(i * 5 + j));
	}

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
