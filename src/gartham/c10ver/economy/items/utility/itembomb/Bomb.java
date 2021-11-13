package gartham.c10ver.economy.items.utility.itembomb;

import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.users.EconomyUser;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Bomb extends Item {
	public static final String ITEM_TYPE = "bomb", ITEM_ICON = ":bomb:", ITEM_NAME = "Bomb";
	// TODO Consumables should take a `ConsumedEvent` which provides all the state
	// applicable when the item is consumed. Currently, just a user object is not
	// enough to fulfill the requirements of this item.

	{
		setIcon(ITEM_ICON);
		setItemName(ITEM_NAME);
	}

	public void consume(MessageReceivedEvent event, Clover clover) {

		// TODO Link with a volatile boolean map (or the like) to assert that the same
		// bomb isn't "consumed" twice.

		event.getGuild().findMembers(a -> !a.getUser().isBot()).onSuccess(t -> {
			BigInteger tot = BigInteger.ZERO;
			for (var x : t) {
				EconomyUser user = clover.getEconomy().getUser(x.getId());
				var rec = user.reward(RewardsOperation.build(user, event.getGuild(),
						BigInteger.valueOf((int) (Math.random() * 200) + 50)));
				tot = tot.add(rec.getRewards().getRewardedCloves());
			}
			event.getChannel().sendMessage(event.getAuthor().getAsMention()
					+ " just used a bomb! It exploded into a total of " + Utilities.format(tot) + " cloves!").queue();
		});

	}

	public Bomb(JSONObject properties) {
		super(ITEM_TYPE, properties);
	}

	public Bomb() {
		super(ITEM_TYPE);
	}

}
