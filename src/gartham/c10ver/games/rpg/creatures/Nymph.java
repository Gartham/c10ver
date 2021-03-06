package gartham.c10ver.games.rpg.creatures;

import org.alixia.javalibrary.json.JSONObject;

public class Nymph extends SimpleCreature {

	public static final String TYPE = "nymph", NAME = "Nymph";
	private static final double HPF = .8, ATTACKF = 1.1, SPEEDF = .7, DEFF = .9;

	public Nymph() {
		super(TYPE,
				"https://media.discordapp.net/attachments/807401695688261639/862522787319382046/nymph.png?width=632&height=676",
				"https://media.discordapp.net/attachments/807401695688261639/862522787519528960/nymph_headshot.png",
				"<:nymph_emoji:854622804514832384>", "Nymph", HPF, ATTACKF, SPEEDF, DEFF);
	}

	public Nymph(JSONObject data) {
		super(data, TYPE, HPF, ATTACKF, SPEEDF, DEFF);
	}

}
