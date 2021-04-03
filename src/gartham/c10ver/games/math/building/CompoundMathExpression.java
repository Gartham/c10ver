package gartham.c10ver.games.math.building;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import gartham.c10ver.games.math.MathProblem;

public class CompoundMathExpression implements MathProblem, MathExpression {

	public enum Operator {
		ADD(1, "+", BigDecimal::add), SUBTRACT(1, "-", BigDecimal::subtract), MULTIPLY(2, "*", BigDecimal::multiply),
		DIVIDE(2, "/", BigDecimal::divide);

		private final int ord;
		private final String chr;
		private final BiFunction<? super BigDecimal, ? super BigDecimal, ? extends BigDecimal> op;

		private Operator(int ord, String chr,
				BiFunction<? super BigDecimal, ? super BigDecimal, ? extends BigDecimal> op) {
			this.ord = ord;
			this.chr = chr;
			this.op = op;
		}

		public int ord() {
			return ord;
		}

		public String chr() {
			return chr;
		}

		public BigDecimal operate(BigDecimal first, BigDecimal second) {
			return op.apply(first, second);
		}
	}

	private final String val;
	private final int ord;
	private final BigDecimal res;

	public CompoundMathExpression(MathExpression first, Operator operator, MathExpression second) {
		StringBuilder sb = new StringBuilder();
		int maxord;
		if (first.ord() < operator.ord()) {
			sb.append('(').append(first.problem()).append(") ");
			maxord = operator.ord();
		} else {
			sb.append(first.problem()).append(' ');
			maxord = first.ord();
		}

		sb.append(operator.chr()).append(' ');

		if (second.ord() > maxord)
			maxord = second.ord();

		if (second.ord() < operator.ord())
			sb.append('(').append(second.problem()).append(')');
		else
			sb.append(second.problem());
		val = sb.toString();
		ord = maxord;
		res = operator.operate(first.eval(), second.eval());
	}

	public static final CompoundMathExpression add(MathExpression first, MathExpression second) {
		return new CompoundMathExpression(first, Operator.ADD, second);
	}

	public static final CompoundMathExpression subtract(MathExpression first, MathExpression second) {
		return new CompoundMathExpression(first, Operator.SUBTRACT, second);
	}

	public static final CompoundMathExpression multiply(MathExpression first, MathExpression second) {
		return new CompoundMathExpression(first, Operator.MULTIPLY, second);
	}

	public static final CompoundMathExpression divide(MathExpression first, MathExpression second) {
		return new CompoundMathExpression(first, Operator.DIVIDE, second);
	}

	@Override
	public BigDecimal eval() {
		return res;
	}

	@Override
	public String problem() {
		return val;
	}

}
