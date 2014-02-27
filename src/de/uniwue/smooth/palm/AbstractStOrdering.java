package de.uniwue.smooth.palm;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides some standard implementations for methods of
 * {@link StOrdering}. Children of the class must only
 * implement {@link #getList()} to fully implement 
 * the {@link StOrdering} contract.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public abstract class AbstractStOrdering<V, E> implements StOrdering<V, E> {
	
	private Map<V, Integer> numbers = null;
	
	/**
	 * Calculates the map with the position of the vertices in the
	 * list obtained by calling {@link #getList()}.
	 */
	@Override
	public Map<V, Integer> asNumbers() {
		if(numbers == null) {
			numbers = new HashMap<V, Integer>();
			for (int i = 0; i < getList().size(); i++) {
				numbers.put(getList().get(i), i);
			}
		}
		return numbers;
	}
	
	/**
	 * Compares two vertices based on the numbers assigned to them
	 * by {@link #asNumbers()}.
	 */
	@Override
	public int compare(V o1, V o2) {
		return asNumbers().get(o1).compareTo(asNumbers().get(o2));
	}
	
	/**
	 * Returns a string representation of this st-ordering.
	 * 
	 * The string is composed of the output from {@link Object#toString()}
	 * and the string representation of the result form {@link #getList()}.
	 */
	@Override
	public String toString() {
		return super.toString() + ":" + getList().toString();
	}
}
