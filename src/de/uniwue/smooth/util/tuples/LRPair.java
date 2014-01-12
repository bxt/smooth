package de.uniwue.smooth.util.tuples;

public interface LRPair<T> extends Pair<T> {
	public abstract T getLeft();
	public abstract T getRight();
	public abstract LRPair<T> setLeft(T left);
	public abstract LRPair<T> setRight(T right);
}
