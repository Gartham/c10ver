package gartham.c10ver.games.math.building;

import java.math.BigDecimal;

import gartham.c10ver.games.math.MathProblem;
import gartham.c10ver.games.math.MathUtils;

public interface MathExpression extends MathProblem {
	BigDecimal eval();

	default int lo() {
		return -1;
	}

	default int ro() {
		return -1;
	}

	@Override
	default AttemptResult check(String result) {
		return MathUtils.check(eval(), result);
	}

	default CompoundMathExpression add(MathExpression second) {
		return CompoundMathExpression.add(this, second);
	}

	default CompoundMathExpression subtract(MathExpression second) {
		return CompoundMathExpression.subtract(this, second);
	}

	default CompoundMathExpression multiply(MathExpression second) {
		return CompoundMathExpression.multiply(this, second);
	}

	default CompoundMathExpression divide(MathExpression second) {
		return CompoundMathExpression.divide(this, second);
	}
}
