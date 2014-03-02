package de.uniwue.smooth.planar;

import org.apache.commons.collections15.Factory;

public class Faces<V, E> {
	
	@SuppressWarnings("unused") // TODO: remove
	public Faces(Embedding<V, E> embedding) {
		
		Factory<Face> factory = Face.getFactory();
		
		EmbeddingIterator<V, E> iterator = embedding.getEmbeddingIteratorOnOuterface();
		
		// TODO: Collect data about faces and implement check for correct embedding.
	}
	
	/**
	 * Minimal face type with just an id property.
	 * 
	 * The id is used for {@link #hashCode()} so that
	 * hash maps can be balanced.
	 */
	public static class Face {
		
		/**
		 * Create a factory for faces. The faces created will have incrementing ids per factory.
		 * This way you will have unique ids as long as you use only one factory.
		 * 
		 * @return A fresh factory for faces.
		 */
		private static final Factory<Face> getFactory() {
			return new Factory<Face> () {
				private int nextId = 0;
				public Face create() {
					return new Face(nextId++);
				}
			};
		}

		private int id;

		/**
		 * Create a new face.
		 * @param id The id of this face, should be unique in your domain.
		 */
		private Face(int id) {
			super();
			this.id = id;
		}

		public String getLabel() {
			return Integer.toString(id);
		}
		
		@Override
		public String toString() {
			return "F" + getLabel();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Face other = (Face) obj;
			if (id != other.id)
				return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			return id;
		}
		
	}

}
