package gartham.c10ver.games.rpg.creatures;

import org.alixia.javalibrary.json.JSONObject;

public class NymphCreature extends SimpleCreature {

	public static final String TYPE = "nymph";
	private static final double HPF = .8, ATTACKF = 1.1, SPEEDF = .7, DEFF = .9;

	public NymphCreature() {
		super(TYPE,
				"https://media.discordapp.net/attachments/807401695688261639/862522787319382046/nymph.png?width=632&height=676",
				"https://media.discordapp.net/attachments/807401695688261639/862522787519528960/nymph_headshot.png",
				"<:nymph_emoji:854622804514832384>", HPF, ATTACKF, SPEEDF, DEFF);
	}

	public NymphCreature(JSONObject data) {
		super(data, TYPE, HPF, ATTACKF, SPEEDF, DEFF);
	}

}
