package gartham.c10ver.economy.items.utility.multickets;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.users.User;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.entities.Guild;

public class MultiplierTicket extends Item {

	public static final String ITEM_TYPE = "multiplier-ticket", ITEM_NAME = "Multiplier Ticket";
	/**
	 * {@code MULT_CTYPE_PK} - The "color type" of the multiplier ticket. This is
	 * "red" for any of the red tickets, "gold" for any of the golds, etc.
	 */
	private static final String MULT_AMOUNT_PK = "amount", MULT_DURATION_PK = "duration";

	@Override
	protected String userFriendlyValue(String propertyKey, Object value) {
		return switch (propertyKey) {
		case MULT_AMOUNT_PK -> super.userFriendlyValue(propertyKey, value) + 'x';
		case MULT_DURATION_PK -> Utilities.formatLargest((Duration) value, 2);
		default -> super.userFriendlyValue(propertyKey, value);
		};
	}

	{
		bigDecimalProperty(MULT_AMOUNT_PK);
		durationProperty(MULT_DURATION_PK);
		setItemName(ITEM_NAME);
	}

	public Property<BigDecimal> amountProperty() {
		return getProperty(MULT_AMOUNT_PK);
	}

	public Property<Duration> durationProperty() {
		return getProperty(MULT_DURATION_PK);
	}

	public BigDecimal getAmount() {
		return amountProperty().get();
	}

	public Duration getDuration() {
		return durationProperty().get();
	}

	public void use(Clover clover, Guild guild, EconomyUser user) {
		clover.getEconomy().getServer(guild.getId())
				.addMultiplier(new Multiplier(Instant.now().plus(getDuration()), getAmount()));
	}

	public MultiplierTicket(JSONObject properties) {
		super(ITEM_TYPE, properties);
		load(iconProperty(), properties);
		load(amountProperty(), properties);
		load(durationProperty(), properties);
		setCustomName(
				"Mlt (" + Utilities.multiplier(getAmount()) + "x/" + Utilities.formatLargest(getDuration(), 2) + ")");
	}

	{
		iconProperty().setTransient(false);
	}

	public MultiplierTicket(String icon, BigDecimal amount, Duration duration) {
		super(ITEM_TYPE);
		setIcon(icon);
		amountProperty().set(amount);
		durationProperty().set(duration);
		setCustomName("Mlt (" + Utilities.multiplier(amount) + "x/" + Utilities.formatLargest(getDuration(), 2) + ")");
	}

}
