package de.uniwue.smooth.util;

public class Pair<K> {

    private final K first;
    private final K second;

    public static <K> Pair<K> createPair(K element0, K element1) {
        return new Pair<K>(element0, element1);
    }

    public Pair(K element0, K element1) {
        this.first = element0;
        this.second = element1;
    }

    public K getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
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
		Pair<?> other = (Pair<?>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

}