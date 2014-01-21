package de.uniwue.smooth.util.tuples;

public abstract interface SEPair<T> extends Pair<T> {
	public abstract T getStart();
	public abstract T getEnd();
	public abstract SEPair<T> setStart(T start);
	public abstract SEPair<T> setEnd(T end);
}
