package de.uniwue.smooth.util.tuples;

import java.io.Serializable;

import de.uniwue.smooth.util.Util;

public class MutableTuple<X, Y> implements Serializable, Tuple<X, Y> {
	
	private static final long serialVersionUID = 1L;

    private X first;
    private Y second;
    
    public MutableTuple() {
        this.first = null;
        this.second = null;
    }

    public MutableTuple(X first, Y second) {
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
	public MutableTuple<X, Y> setFirst(X first) {
        this.first = first;
        return this;
    }
    
    @Override
	public MutableTuple<X, Y> setSecond(Y second) {
        this.second = second;
        return this;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((first == null) ? 0 : first.hashCode());
		result = prime * result
				+ ((second == null) ? 0 : second.hashCode());
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
		MutableTuple<?,?> other = (MutableTuple<?,?>) obj;
		if (!first.equals(other.first))
			return false;
		if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return "(" + Util.nullsafeToString(first) + ", " + Util.nullsafeToString(second) + ")";
    }
	
}
