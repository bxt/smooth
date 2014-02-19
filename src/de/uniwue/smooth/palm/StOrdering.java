package de.uniwue.smooth.palm;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface StOrdering<V, E> extends Comparator<V> {

	public abstract List<V> getList();

	public abstract Map<V, Integer> asNumbers();

}