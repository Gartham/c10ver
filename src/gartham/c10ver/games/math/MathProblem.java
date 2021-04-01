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
	 * Checks if the provided text is the answer to this math problem.
	 * 
	 * @param result The {@link String} representation of the result.
	 * @return <code>true</code> if the provided result is correct,
	 *         <code>false</code> otherwise.
	 */
	boolean check(String result);
}
