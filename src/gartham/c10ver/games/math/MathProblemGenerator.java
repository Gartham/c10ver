package gartham.c10ver.games.math;

import zeale.applicationss.notesss.utilities.generators.Generator;

public interface MathProblemGenerator extends Generator<MathProblem> {
	MathProblem generate(double difficulty);

	/**
	 * Generates a random {@link MathProblem} with a random difficulty ranging from
	 * 0 to 10, inclusive, exclusive.
	 */
	@Override
	default MathProblem next() {
		return generate(Math.random() * 10);
	}
}
