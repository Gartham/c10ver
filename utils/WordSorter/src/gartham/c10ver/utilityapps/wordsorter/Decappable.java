package gartham.c10ver.utilityapps.wordsorter;

/**
 * Represents an object that can be decapitated, like a hobo, or a fish, or
 * something else.
 * 
 * @author Gartham
 * @param <H> The type of the head resulting from the decapitation.
 * @param <T> The type of the tail resulting from the decapitation. This is
 *            usually an object that is a combination of <code>Head-Type</code>
 *            objects. For example, when you decapitate a {@link String}, the
 *            head will be a single character and the tail is the remainder of
 *            the string.
 *
 */
public interface Decappable<H, T> {
	/**
	 * Returns the head object that results from the decapitation.
	 * 
	 * @return The head object.
	 */
	H head();

	/**
	 * Returns the tail that results from a decapitation.
	 * 
	 * @return The tail object.
	 */
	T tail();
}
