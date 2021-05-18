package gartham.c10ver.economy.items.utility.multickets;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.User;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.entities.Guild;

public class MultiplierTicket extends Item {

	public static final String ITEM_TYPE = "multiplier-ticket", ITEM_NAME = "Multiplier Ticket";
	/**
	 * {@code MULT_CTYPE_PK} - The "color type" of the multiplier ticket. This is
	 * "red" for any of the red tickets, "gold" for any of the golds, etc.
	 */
	private static final String MULT_VALUE_PK = "value", MULT_TTL_PK = "ttl";

	{
		bigDecimalProperty(MULT_VALUE_PK);
		durationProperty(MULT_TTL_PK);
		setItemName(ITEM_NAME);
	}

	public Property<BigDecimal> valueProperty() {
		return getProperty(MULT_VALUE_PK);
	}

	public Property<Duration> ttlProperty() {
		return getProperty(MULT_TTL_PK);
	}

	public BigDecimal getValue() {
		return valueProperty().get();
	}

	public Duration getTTL() {
		return ttlProperty().get();
	}

	public void use(Clover clover, Guild guild, User user) {
		var serv = clover.getEconomy().getServer(guild.getId());
		var chn = guild.getTextChannelById(serv.getGeneralChannel());
		chn.sendMessage(user.getUser().getAsMention() + " is using a **" + Utilities.multiplier(getValue())
				+ "x** multiplier that lasts for **" + Utilities.formatLargest(getTTL(), 2) + "**.").queue();

		serv.addMultiplier(new Multiplier(Instant.now().plus(getTTL()), getValue()));
	}

	public MultiplierTicket(JSONObject properties) {
		super(ITEM_TYPE, properties);
		load(iconProperty(), properties);
		load(valueProperty(), properties);
		load(ttlProperty(), properties);
		setCustomName("Mlt (" + Utilities.multiplier(getValue()) + "x/" + Utilities.formatLargest(getTTL(), 2) + ")");
	}

	{
		iconProperty().setTransient(false);
	}

	public MultiplierTicket(String icon, BigDecimal value, Duration ttl) {
		super(ITEM_TYPE);
		setIcon(icon);
		valueProperty().set(value);
		ttlProperty().set(ttl);
		setCustomName("Mlt (" + Utilities.multiplier(value) + "x/" + Utilities.formatLargest(getTTL(), 2) + ")");
	}

}
