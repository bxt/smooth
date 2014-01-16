package de.uniwue.smooth.app;

import org.apache.commons.collections15.Factory;

public class Edge {
	
	public static final Factory<Edge> getFactory() {
		return new Factory<Edge> () {
			private int nextId = 0;
			public Edge create() {
				return new Edge(nextId++);
			}
		};
	}

	private int id;

	public Edge(int id) {
		super();
		this.id = id;
	}

	public String getLabel() {
		return Integer.toString(id);
	}
	
	@Override
	public String toString() {
		return "E" + getLabel();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
}
