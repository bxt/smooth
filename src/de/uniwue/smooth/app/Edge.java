package de.uniwue.smooth.app;

import org.apache.commons.collections15.Factory;

/**
 * Minimal edge type with just an id property.
 * 
 * The id is used for {@link #hashCode()} so that
 * hash maps can be balanced.
 */
public class Edge {
	
	/**
	 * Create a factory for edges. The edges created will have incrementing ids per factory.
	 * This way you will have unique ids as long as you use only one factory.
	 * 
	 * @return A fresh factory for edges.
	 */
	public static final Factory<Edge> getFactory() {
		return new Factory<Edge> () {
			private int nextId = 0;
			public Edge create() {
				return new Edge(nextId++);
			}
		};
	}

	private int id;

	/**
	 * Create a new edge.
	 * @param id The id of this edge, should be unique in your domain.
	 */
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
