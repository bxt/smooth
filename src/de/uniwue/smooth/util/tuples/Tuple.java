package de.uniwue.smooth.util.tuples;

public interface Tuple<X, Y> {

	/**
	 * Returns the first element.
	 */
	public abstract X getFirst();

	/**
	 * Returns the second element.
	 */
	public abstract Y getSecond();

	/**
	 * Returns the same tuple but with the specified first element.
	 */
	public abstract Tuple<X, Y> setFirst(X first);

	/**
	 * Returns the same tuple but with the specified second element.
	 */
	public abstract Tuple<X, Y> setSecond(Y second);

}