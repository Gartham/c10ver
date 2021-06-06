package gartham.c10ver.games.math.building;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

import gartham.c10ver.games.math.MathProblem;

public class CompoundMathExpression implements MathProblem, MathExpression {

	public enum Operator {
		ADD(1, "+", BigDecimal::add), SUBTRACT(1, "-", BigDecimal::subtract, true),
		MULTIPLY(2, "*", BigDecimal::multiply), DIVIDE(2, "/", (a, b) -> a.divide(b, RoundingMode.HALF_UP), true);

		private final int ord;
		private final String chr;
		private final BiFunction<? super BigDecimal, ? super BigDecimal, ? extends BigDecimal> op;
		private final boolean wrapRight;

		private Operator(int ord, String chr,
				BiFunction<? super BigDecimal, ? super BigDecimal, ? extends BigDecimal> op) {
			this(ord, chr, op, false);
		}

		private Operator(int ord, String chr,
				BiFunction<? super BigDecimal, ? super BigDecimal, ? extends BigDecimal> op, boolean wrap) {
			this.ord = ord;
			this.chr = chr;
			this.op = op;
			wrapRight = wrap;
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
	private final int lo, ro;
	private final BigDecimal res;

	/**
	 * Wraps the provided {@link MathExpression} in parentheses. This makes its
	 * behavior the same as a {@link Term} (in ordinality) and makes its
	 * {@link MathExpression#problem()} have parentheses around it.
	 * 
	 * @param other The {@link MathExpression} to wrap.
	 * @return A {@link MathExpression} that is equivalent to the provided one, but
	 *         wrapped in parentheses.
	 */
	public static MathExpression wrap(MathExpression other) {
		return new MathExpression() {

			@Override
			public String problem() {
				return '(' + other.problem() + ')';
			}

			@Override
			public BigDecimal eval() {
				return other.eval();
			}
		};
	}

	public CompoundMathExpression(MathExpression first, Operator operator, MathExpression second) {
		StringBuilder sb = new StringBuilder();

//		System.out.println("FIRST: " + first.problem());
//		System.out.println("SECOND: " + second.problem());

		if (first.ro() > 0 && first.lo() < operator.ord)
			first = wrap(first);
		if (second.ro() > 0 && (second.lo() < operator.ord || operator.wrapRight))
			second = wrap(second);

		ro = first.ro() > operator.ord ? first.ro() > second.ro() ? first.ro() : second.ro()
				: operator.ord > second.ro() ? operator.ord : second.ro();
		lo = first.lo() < 0 ? second.lo() < 0 ? operator.ord : second.lo() < operator.ord ? second.lo() : operator.ord
				: second.lo() < 0 ? first.lo() < operator.ord ? first.lo() : operator.ord
						: first.lo() < operator.ord ? first.lo() < second.lo() ? first.lo() : second.lo()
								: operator.ord < second.lo() ? operator.ord : second.lo();

		sb.append(first.problem()).append(' ').append(operator.chr()).append(' ').append(second.problem());

		val = sb.toString();
		res = operator.operate(first.eval(), second.eval());
//		System.out.println("RESULT: " + val);
//		System.out.println("HO: " + ro);
//		System.out.println("LO: " + lo);
//		System.out.println("\n\n");
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

	@Override
	public int lo() {
		return lo;
	}

	@Override
	public int ro() {
		return ro;
	}

}
