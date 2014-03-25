package de.uniwue.smooth.draw;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Some basics for performing the drawing of primitives into an {@link OrthogonalDrawing} given an {@link OrthogonalLayout}.
 * 
 * Provides access to the two inputs as instance variables for subclasses.
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public abstract class AbstractOrthogonalDrawer<V, E> implements OrthogonalDrawer<V, E> {
	
	protected OrthogonalLayout<V, E> layout;
	protected OrthogonalDrawing<?> drawing;
	
	@Override
	public void draw(OrthogonalLayout<V, E> layout, OrthogonalDrawing<?> drawing) {
		this.layout = layout;
		this.drawing = drawing;
		
		draw();
		
		this.layout = null;
		this.drawing = null;
	}
	
	/**
	 * Draws everything.
	 */
	private void draw() {
		drawVertices();
		drawEdges();
	}
	
	/**
	 * Draws the vertices, setting a marker at each vertex' position.
	 */
	private void drawVertices() {
		for (V v : layout.getGraph().getVertices()) {
			Pair<Integer> coordinates = layout.getVertexLocation(v);
			drawing.vertex(coordinates);
			drawing.label(coordinates, v.toString());
		}
	}
	
	/**
	 * Draws the edges.
	 */
	protected abstract void drawEdges();
	
}