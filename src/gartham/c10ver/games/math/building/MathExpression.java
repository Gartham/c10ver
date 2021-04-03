package gartham.c10ver.games.math.building;

import java.math.BigDecimal;

import gartham.c10ver.games.math.MathProblem;
import gartham.c10ver.games.math.MathUtils;

public interface MathExpression extends MathProblem {
	BigDecimal eval();

	default int ord() {
		return -1;
	}

	@Override
	default boolean check(String result) {
		return MathUtils.check(eval(), result);
	}
}
