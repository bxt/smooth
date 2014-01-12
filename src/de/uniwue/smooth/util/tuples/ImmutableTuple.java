package de.uniwue.smooth.util.tuples;

import java.io.Serializable;

public class ImmutableTuple<X, Y> implements Serializable, Tuple<X, Y> {
	
	private static final long serialVersionUID = 1L;

    private X first;
    private Y second;
    
    public ImmutableTuple(X first, Y second) {
    	if(first == null || second == null) 
    		throw new IllegalArgumentException("Tuple cannot contain null values");
        this.first = first;
        this.second = second;
    }

    @Override
	public X getFirst() {
        return first;
    }

    @Override
	public Y getSecond() {
        return second;
    }
    
    @Override
	public ImmutableTuple<X, Y> setFirst(X first) {
        return new ImmutableTuple<X, Y>(first, second);
    }
    
    @Override
	public ImmutableTuple<X, Y> setSecond(Y second) {
        return new ImmutableTuple<X, Y>(first, second);
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first.hashCode();
		result = prime * result + second.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableTuple<?,?> other = (ImmutableTuple<?,?>) obj;
		if (!first.equals(other.first))
			return false;
		if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }
	
}
