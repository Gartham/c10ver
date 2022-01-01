package gartham.c10ver.games.rpg.dungeons;

import java.util.ArrayList;

import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.games.rpg.rooms.RectangularRoom;
import gartham.c10ver.response.utils.DirectionSelector;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;

public class LootRoom extends DungeonRoom {

	private final RewardsOperation rewards;
	private final RandLines lines;

	public RewardsOperation getRewards() {
		return rewards;
	}

	public LootRoom(RewardsOperation rewards) {
		super(RectangularRoom.discordSquare(15));
		this.rewards = rewards;
		ArrayList<String[]> l = new ArrayList<>();
		lines = new RandLines(l);

		if (!rewards.hasMults() && !rewards.hasItems())
			l.add(new String[] { "\uD83D\uDCB2", "", "", "", "\u3000", "\u3000", "\u3000" });
		else {
			assert rewards.hasItems() || rewards.hasMults()
					: "RewardsOperation object provided to LootRoom constructor should have *some* type of rewards.";
			l.add(new String[] { "\uD83D\uDCB0", "\u3000", "", "" });
			if (rewards.getMults().size() > 1)
				l.add(new String[] { "\uD83D\uDCB0", "\u3000", "", "" });
		}
		var room = getRoom();
		room.getGraphics().add(lines);
	}

	@Override
	public void prepare(DirectionSelector selector, String actionButtonID) {
		super.prepare(selector, actionButtonID);
		if (!isClaimed())
			selector.setMiddle(Button.success(actionButtonID, Emoji.fromMarkdown("\uD83D\uDCB0")).withLabel("Collect"));
	}

}
