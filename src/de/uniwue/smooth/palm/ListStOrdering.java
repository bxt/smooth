package de.uniwue.smooth.palm;

import java.util.List;

public class ListStOrdering<V, E> extends AbstractStOrdering<V, E> implements StOrdering<V, E> {
	
	private List<V> vertexList;
	
	public ListStOrdering(List<V> vertexList) {
		this.vertexList = vertexList;
	}
	
	@Override
	public List<V> getList() {
		return vertexList;
	}
	
}
