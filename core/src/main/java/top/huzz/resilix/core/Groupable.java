package top.huzz.resilix.core;

/**
 * @param <I> the type of the identifier
 * @author huzz
 * @since 1.0.2
 */
public interface Groupable<I> {
	/**
	 * Gets the group identifier.
	 *
	 * @return the group identifier
	 */
	I getGroupId();
}
