package gartham.c10ver.games.math;

/**
 * Represents a mathematical problem that can be "solved" to obtain a result.
 * 
 * @author Gartham
 *
 */
public interface MathProblem {
	/**
	 * Returns a {@link String} representation of this {@link MathProblem}.
	 * 
	 * @return this math problem as a {@link String}.
	 */
	String problem();

	/**
	 * Denotes the result of an attempt made to solve the problem.
	 * 
	 * @author Gartham
	 *
	 */
	enum AttemptResult {
		/**
		 * Denotes that a checked attempt was both valid and correct.
		 */
		CORRECT,
		/**
		 * Denotes that an attempt was valid, but not correct. An example of this would
		 * be <code>17</code> as an attempt to solve the problem <code>1 + 1</code>.
		 * <code>17</code> is a valid response, but is not the correct response.
		 */
		INCORRECT,
		/**
		 * Denotes that an attempt was invalid. An example of this would be a
		 * <code>xyz</code> as an attempt at solving the problem <code>1 + 1</code>.
		 * Such would not even be a valid attempt, since it is not even a number.
		 */
		INVALID;
	}

	/**
	 * Checks if the provided text is the answer to this math problem.
	 * 
	 * @param result The {@link String} representation of the result.
	 * @return <code>true</code> if the provided result is correct,
	 *         <code>false</code> otherwise.
	 */
	AttemptResult check(String result);

}
