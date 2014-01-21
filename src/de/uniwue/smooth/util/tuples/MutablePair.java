package de.uniwue.smooth.util.tuples;


public class MutablePair<T> extends MutableTuple<T, T> implements Pair<T>, HLPair<T>, LRPair<T>, SEPair<T>, XYPair<T> {

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

	@Override
	public T getX() {
		return getFirst();
	}

	@Override
	public T getY() {
		return getSecond();
	}

	@Override
	public XYPair<T> setX(T x) {
		setFirst(x);
		return this;
	}

	@Override
	public XYPair<T> setY(T y) {
		setSecond(y);
		return this;
	}

	@Override
	public T getStart() {
		return getFirst();
	}

	@Override
	public T getEnd() {
		return getSecond();
	}

	@Override
	public SEPair<T> setStart(T start) {
		setFirst(start);
		return this;
	}

	@Override
	public SEPair<T> setEnd(T end) {
		setSecond(end);
		return this;
	}
	
}
