package de.uniwue.smooth.app;

import org.apache.commons.collections15.Factory;

/**
 * Minimal vertex type with just an id property.
 * 
 * The id is used for {@link #hashCode()} so that
 * hash maps can be balanced.
 */
public class Vertex {
	
	/**
	 * Create a factory for vertices. The vertices created will have incrementing ids per factory.
	 * This way you will have unique ids as long as you use only one factory.
	 * 
	 * @return A fresh factory for vertices.
	 */
	public static final Factory<Vertex> getFactory() {
		return new Factory<Vertex> () {
			private int nextId = 0;
			public Vertex create() {
				return new Vertex(nextId++);
			}
		};
	}

	private int id;

	/**
	 * Create a new vertex.
	 * @param id The id of the vertex, should be unique in your domain.
	 */
	public Vertex(int id) {
		super();
		this.id = id;
	}

	public String getLabel() {
		return Integer.toString(id);
	}

	@Override
	public String toString() {
		return "V" + getLabel();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
