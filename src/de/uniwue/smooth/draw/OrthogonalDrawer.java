package de.uniwue.smooth.draw;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;

/**
 * Draws vertices and edges from a layout into a drawing.
 * 
 * Implementors will make sure that all the vertices appear in their positions
 * and the edge positions and bends are well-positioned and agree with the ports
 * assigned in the layout.
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public interface OrthogonalDrawer<V, E> {
	
	/**
	 * Write the graph with the given layout into the drawing.
	 * @param layout Layout of the graph (includes graph).
	 * @param drawing Drawing to paint the graph onto.
	 */
	void draw(OrthogonalLayout<V, E> layout, OrthogonalDrawing<?> drawing);

}
