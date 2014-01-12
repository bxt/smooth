package de.uniwue.smooth.util.tuples;

public abstract interface HLPair<T> extends Pair<T> {
	public abstract T getLow();
	public abstract T getHigh();
	public abstract HLPair<T> setLow(T low);
	public abstract HLPair<T> setHigh(T high);
}
