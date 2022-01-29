package gartham.c10ver.games.rpg.wilderness;

import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import gartham.c10ver.utils.MessageActionHandler.Group;
import net.dv8tion.jda.api.interactions.components.Button;

class WildernessGamepadGroup extends Group {
	private final MessageActionHandler mah;

	public WildernessGamepadGroup(MessageActionHandler mah) {
		mah.super(new Action[0]);
		this.mah = mah;
	}

	public enum Button {
		MOVE_UP, MOVE_LEFT, MOVE_RIGHT, MOVE_DOWN, RS1, RS2, RS3, RS4, RS5, RS6, B1, B2, B3, B4, B5, B6, B7, B8,
		PAGE_LEFT, PAGE_RIGHT;
	}

}
