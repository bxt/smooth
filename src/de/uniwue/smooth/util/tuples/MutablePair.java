package de.uniwue.smooth.util.tuples;


public class MutablePair<T> extends MutableTuple<T, T> implements Pair<T>, HLPair<T>, LRPair<T> {

	private static final long serialVersionUID = 1L;

	public MutablePair() {
		super();
	}
	
	public MutablePair(T first, T second) {
		super(first, second);
	}

	@Override
	public T getLeft() {
		return getFirst();
	}

	@Override
	public T getRight() {
		return getSecond();
	}

	@Override
	public LRPair<T> setLeft(T left) {
		setFirst(left);
		return this;
	}

	@Override
	public LRPair<T> setRight(T right) {
		setSecond(right);
		return this;
	}

	@Override
	public T getLow() {
		return getFirst();
	}

	@Override
	public T getHigh() {
		return getSecond();
	}

	@Override
	public HLPair<T> setLow(T low) {
		setFirst(low);
		return this;
	}

	@Override
	public HLPair<T> setHigh(T high) {
		setSecond(high);
		return this;
	}
	
}
