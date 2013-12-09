package de.uniwue.smooth.util;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private X first;
    private Y second;
    
    public Tuple(X first, Y second) {
    	if(first == null || second == null) 
    		throw new IllegalArgumentException("Tuple cannot contain null values");
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element.
     */
    public X getFirst() {
        return first;
    }
    
    /**
     * Returns the second element.
     */
    public Y getSecond() {
        return second;
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
		Tuple<?,?> other = (Tuple<?,?>) obj;
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
