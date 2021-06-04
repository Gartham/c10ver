package gartham.c10ver.games.math;

import java.math.BigDecimal;

import gartham.c10ver.games.math.MathProblem.AttemptResult;

public class MathUtils {
	public static AttemptResult check(BigDecimal val, String other) {
		try {
			return val.equals(new BigDecimal(other)) ? AttemptResult.CORRECT : AttemptResult.INCORRECT;
		} catch (NumberFormatException e) {
			return AttemptResult.INVALID;
		}
	}
}
