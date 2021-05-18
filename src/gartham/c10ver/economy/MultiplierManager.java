package gartham.c10ver.economy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A convenience class for calculating information about stackable multipliers.
 * Most classes that need this functionality implement a subset of these
 * functions in addition to a <code>hasMults()</code> method, or something
 * similar designed to determine whether there are any active multipliers.
 * 
 * @author Gartham
 *
 */
public class MultiplierManager {

	/**
	 * Iterates over the list of multipliers property and cleans out any expired
	 * multipliers.
	 */
	public static void cleanMults(List<Multiplier> mults) {
		Instant now = Instant.now();
		for (var iterator = mults.iterator(); iterator.hasNext();)
			if (now.isAfter(iterator.next().getExpiration()))
				iterator.remove();
	}

	/**
	 * Sums up the valid multipliers stored in the multipliers property and deletes
	 * the expired ones (like a call to {@link #cleanMults()} would).
	 * 
	 * @return The sum of the valid multipliers.
	 */
	public static BigDecimal getTotalMultiplier(List<Multiplier> mults) {
		if (mults.isEmpty())
			return BigDecimal.ZERO;
		BigDecimal res = BigDecimal.ZERO;
		Instant now = Instant.now();
		for (var iterator = mults.iterator(); iterator.hasNext();) {
			Multiplier m = iterator.next();
			if (now.isAfter(m.getExpiration()))
				iterator.remove();
			else
				res = res.add(m.getAmount());
		}

		return res;
	}

	public static List<Multiplier> getMultipliers(List<Multiplier> mults) {
		cleanMults(mults);
		return new ArrayList<>(mults);
	}

	/**
	 * <p>
	 * Gets the total multiplier applied to whatever entity these multipliers apply
	 * to. This is equivalent to <code>1 + mults</code> where <code>mults</code> is
	 * the {@link #getTotalMultiplier() sum of the multipliers managed by this
	 * object}.
	 * </p>
	 * <p>
	 * If an entity has 2 multipliers active, each with a value of 0.3x, then its
	 * total multiplier will be 1.6x, which is the sum of the two multipliers, plus
	 * their standard 1x multiplier. The value of 1.6x would be returned by this
	 * function, whereas the simple sum of the multipliers (0.6x) would be returned
	 * by {@link #getTotalMultiplier()}.
	 * </p>
	 * 
	 * @return <code>{@link BigDecimal#ONE}.{@link BigDecimal#add(BigDecimal)
	 *         add}({@link #getTotalMultiplier() this.getTotalMultiplier}())</code>
	 */
	public static BigDecimal getTotalValue(List<Multiplier> mults) {
		return BigDecimal.ONE.add(getTotalMultiplier(mults));
	}

}
