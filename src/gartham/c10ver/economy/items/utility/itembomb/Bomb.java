package gartham.c10ver.economy.items.utility.itembomb;

import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.users.User;
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

		event.getGuild().findMembers(a -> !a.getUser().isBot()).onSuccess(t -> {
			BigInteger tot = BigInteger.ZERO;
			for (var x : t) {
				User user = clover.getEconomy().getUser(x.getId());
				tot = tot.add(
						user.rewardAndSave((int) (Math.random() * 200) + 50, user.calcMultiplier(event.getGuild())));
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
