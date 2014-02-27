package de.uniwue.smooth.palm;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStOrdering<V, E> implements StOrdering<V, E> {
	
	private Map<V, Integer> numbers = null;
		
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
	
	@Override
	public int compare(V o1, V o2) {
		return numbers.get(o1).compareTo(numbers.get(o2));
	}
	
	@Override
	public String toString() {
		return super.toString() + ":" + getList().toString();
	}
}
