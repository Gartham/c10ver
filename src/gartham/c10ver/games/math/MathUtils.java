package gartham.c10ver.games.math;

import java.math.BigDecimal;

public class MathUtils {
	public static boolean check(BigDecimal val, String other) {
		try {
			return val.equals(new BigDecimal(other));
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
