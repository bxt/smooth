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
	
	private boolean bare = false;
	
	protected OrthogonalLayout<V, E> layout;
	protected OrthogonalDrawing<?> drawing;
	
	/**
	 * Enables "bare" mode for drawings, that is: do not draw any labels,
	 * edge midpoints or other decorative features, just the bare graph.
	 */
	public void setBare() {
		setBare(true);
	}
	
	/**
	 * Sets the "bare" flag for drawings, when enabled do not draw any labels,
	 * edge midpoints or other decorative features, just the bare graph.
	 */
	public void setBare(boolean bare) {
		this.bare = bare;
	}
	
	/**
	 * If or not the drawings should be created in bare mode.
	 * @return True iff the drawings should leave out expendable details.
	 */
	protected boolean isBare() {
		return bare;
	}
	
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
			if(!isBare()) drawing.label(coordinates, v.toString());
		}
	}
	
	/**
	 * Draws the edges.
	 */
	protected abstract void drawEdges();
	
}