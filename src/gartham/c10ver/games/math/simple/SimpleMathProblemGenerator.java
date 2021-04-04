package gartham.c10ver.games.math.simple;

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

	public Random getRand() {
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

		MathExpression expr = Term.of(rand.nextInt(3 * range));
		while (tc-- > 0) {
			Term n = Term.of(rand.nextInt(3 * range));
			boolean shouldFlip = difficulty > 6 && rand.nextBoolean();
			expr = switch (rand.nextInt(difficulty > 2 ? 3 : 2)) {
			case 0 -> shouldFlip ? n.add(expr) : expr.add(n);
			case 1 -> shouldFlip ? n.subtract(expr) : expr.subtract(n);
			case 2 -> shouldFlip ? n.multiply(expr) : expr.multiply(n);
			case 3 -> shouldFlip ? n.divide(expr) : expr.divide(n);
			default -> throw new IllegalArgumentException("Unexpected value: " + rand.nextInt(difficulty > 2 ? 4 : 2));
			};
		}
		return expr;
	}

	@Override
	public MathExpression next() {
		return (MathExpression) MathProblemGenerator.super.next();
	}

}
