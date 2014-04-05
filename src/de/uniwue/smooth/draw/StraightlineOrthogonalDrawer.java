package de.uniwue.smooth.draw;

import de.uniwue.smooth.collision.segments.EdgeGenerator;
import de.uniwue.smooth.collision.segments.Segment;
import de.uniwue.smooth.collision.segments.SegmentedEdge;
import de.uniwue.smooth.collision.segments.StraightlineEdge;
import de.uniwue.smooth.collision.segments.StraightlineEdgeGenerator;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;

/**
 * Performs the drawing of primitives into an {@link OrthogonalDrawing} given an {@link OrthogonalLayout}.
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class StraightlineOrthogonalDrawer<V, E> extends AbstractOrthogonalDrawer<V, E> implements OrthogonalDrawer<V, E> {
	
	private EdgeGenerator<V, E, StraightlineEdge> edgeGenerator;
	
	/**
	 * Draws the edges with the edge generator.
	 */
	@Override
	protected void drawEdges() {
		edgeGenerator = new StraightlineEdgeGenerator<V, E>(layout);
		for (final E e : layout.getGraph().getEdges()) {
			StraightlineEdge edge = edgeGenerator.generateEdge(e);
			
			drawSegments(edge);
			drawing.edgeMidpoint(edge.getMidpoint());
		}
	}
	
	private void drawSegments(SegmentedEdge edge) {
		for (Segment segment : edge.getSegments()) {
			segment.draw(drawing);
		}
	}
}