package gartham.c10ver.response.buttonbox;

import gartham.c10ver.response.MutableButton;
import net.dv8tion.jda.api.interactions.components.Button;

public class ButtonBoxButton extends MutableButton {

	private boolean present;

	public boolean isPresent() {
		return present;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	public ButtonBoxButton(Button button) {
		super(button);
	}

	public ButtonBoxButton(String id) {
		super(id);
	}

}
