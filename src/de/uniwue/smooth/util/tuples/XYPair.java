package de.uniwue.smooth.util.tuples;

public abstract interface XYPair<T> extends Pair<T> {
	public abstract T getX();
	public abstract T getY();
	public abstract XYPair<T> setX(T x);
	public abstract XYPair<T> setY(T y);
}
