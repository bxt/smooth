package de.uniwue.smooth.app;

import org.apache.commons.collections15.Factory;

public class Vertex {

	public static final Factory<Vertex> getFactory() {
		return new Factory<Vertex> () {
			private int nextId = 0;
			public Vertex create() {
				return new Vertex(nextId++);
			}
		};
	}

	private int id;

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
