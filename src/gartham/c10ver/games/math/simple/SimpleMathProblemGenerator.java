package gartham.c10ver.games.math.simple;

import java.util.Random;

import gartham.c10ver.games.math.BasicMathProblem;
import gartham.c10ver.games.math.MathProblemGenerator;

public class SimpleMathProblemGenerator implements MathProblemGenerator {
	private Random random;

	public SimpleMathProblemGenerator(Random random) {
		this.random = random;
	}

	public SimpleMathProblemGenerator() {
		this(new Random());
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	@Override
	public BasicMathProblem generate(double difficulty) {
		var sqrt = Math.sqrt(difficulty);
		var tc = Math.max(0, Math.min(3, sqrt)) + 1;
		System.out.println("SQRT: " + sqrt);
		System.out.println("TERMCOUNT: " + tc);
		var dis = sqrt * sqrt / 2;
		System.out.println("DIS: " + dis);
		var range = (int) (3.5 * (difficulty + 1 - dis));

		StringBuilder sb = new StringBuilder();
		int term = random.nextInt(range), result = term;
		sb.append(term);

		for (; tc > 0; tc--) {
			switch ((int) (Math.random() * 4)) {
			case 0:
				
			default:
				throw new IllegalArgumentException("Unexpected value: " + (int) (Math.random() * 4));
			}
		}
		return null;
	}

	public static void main(String[] args) {
		var smpg = new SimpleMathProblemGenerator();
		System.out.println(smpg.next());
	}

}
