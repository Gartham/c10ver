package gartham.c10ver.games.math.simple;

import java.math.BigDecimal;
import java.util.Random;

import gartham.c10ver.games.math.MathProblemGenerator;
import gartham.c10ver.games.math.building.MathExpression;
import gartham.c10ver.games.math.building.Term;

public class SimpleMathProblemGenerator implements MathProblemGenerator {
	private Random rand;

	public SimpleMathProblemGenerator(Random random) {
		this.rand = random;
	}

	public SimpleMathProblemGenerator() {
		this(new Random());
	}

	public Random getRandom() {
		return rand;
	}

	public void setRandom(Random random) {
		this.rand = random;
	}

	@Override
	public MathExpression generate(double difficulty) {
		var sqrt = Math.sqrt(difficulty);
		var tc = Math.max(0, (int) Math.min(3, sqrt)) + 1;
		var dis = sqrt * sqrt / 2;
		var range = (int) (3.5 * (difficulty + 1 - dis));

		// TODO reduce diff when multiplication is picked.
		MathExpression expr = Term.of(rand.nextInt(3 * range));
		while (tc-- > 0) {

			boolean shouldFlip = difficulty > 6 && rand.nextBoolean();
			expr = switch (rand.nextInt(difficulty > 2 ? 3 : 2)) {
			case 0:
				Term n = Term.of(rand.nextInt(3 * range));
				yield shouldFlip ? n.add(expr) : expr.add(n);
			case 1:
				n = Term.of(rand.nextInt(3 * range));
				yield shouldFlip ? n.subtract(expr) : expr.subtract(n);
			case 2:
				n = Term.of(rand.nextInt((int) (5 * sqrt) + 1));
				yield shouldFlip ? n.multiply(cap(expr, (long) difficulty)) : cap(expr, (long) difficulty).multiply(n);
			case 3:
				n = Term.of(rand.nextInt((int) (5 * sqrt) + 1));
				yield shouldFlip ? n.divide(expr) : expr.divide(n);
			default:
				throw new IllegalArgumentException("Unexpected value: " + rand.nextInt(difficulty > 2 ? 4 : 2));
			};
		}
		return expr;
	}

	private static MathExpression cap(MathExpression expr, long value) {
		BigDecimal x = expr.eval(), valbd = BigDecimal.valueOf(value);
		if (x.abs().compareTo(valbd) > 0) {

			BigDecimal rand = new BigDecimal(
					BigDecimal.valueOf(Math.random()).multiply(valbd).add(expr.eval().subtract(valbd)).toBigInteger());

			// Range - expr.eval() - value, expr.eval();

			return x.compareTo(BigDecimal.ZERO) < 0 ? expr.add(Term.of(rand)) : expr.subtract(Term.of(rand));
		} else
			return expr;
	}

	@Override
	public MathExpression next() {
		return (MathExpression) MathProblemGenerator.super.next();
	}

}
