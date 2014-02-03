package de.uniwue.smooth.draw;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.util.tuples.Tuple;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Some basics for performing the drawing of primitives into an {@link OrthogonalDrawing} given an {@link OrthogonalLayout}.
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 * @param <T> Type of resulting painting.
 */
public abstract class AbstractOrthogonalDrawer<V, E, T> implements OrthogonalDrawer<V, E, T> {
	
	protected OrthogonalLayout<V, E> layout;
	protected OrthogonalDrawing<T> drawing;
	
	@Override
	public T transform(Tuple<OrthogonalLayout<V, E>, OrthogonalDrawing<T>> input) {
		this.layout = input.getFirst();
		this.drawing = input.getSecond();
		
		draw();
		T result = drawing.create();
		
		this.layout = null;
		this.drawing = null;
		
		return result;
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