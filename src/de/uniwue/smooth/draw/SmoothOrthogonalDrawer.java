package de.uniwue.smooth.draw;

import de.uniwue.smooth.collision.CollisionManager;
import de.uniwue.smooth.collision.segments.Segment;
import de.uniwue.smooth.collision.segments.Segment.Arc;
import de.uniwue.smooth.collision.segments.SmoothEdge;
import de.uniwue.smooth.collision.segments.SmoothEdgeGenerator;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;

/**
 * Performs the drawing of smooth primitives into an {@link OrthogonalDrawing} given an {@link OrthogonalLayout}.
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class SmoothOrthogonalDrawer<V, E> extends AbstractOrthogonalDrawer<V, E> implements OrthogonalDrawer<V, E> {
	
	private CollisionManager collisionManager;
	private SmoothEdgeGenerator<V, E> edgeGenerator;
	
	public CollisionManager getCollisionManager() {
		return collisionManager;
	}
	
	/**
	 * Draws the edges smoothly.
	 */
	@Override
	protected void drawEdges() {
		collisionManager = new CollisionManager();
		edgeGenerator = new SmoothEdgeGenerator<V, E>(layout);
		for (final E e : layout.getGraph().getEdges()) {
			SmoothEdge edge = edgeGenerator.generateEdge(e);
			
			switch(edge.getEdgeType()) {
			case I:
			case S:
			case U:
			case C:
				drawEdge(edge);
				break;
			case L:
			case G:
				drawLGEdges(edge);
				break;
			default:
				throw new IllegalStateException("Unknown edge type!");
			}
		}
		collisionManager = null;
		edgeGenerator = null; 
	}
	
	private void drawEdge(SmoothEdge edge) {
		if(edge.getKink() != null) drawing.edgeMidpoint(edge.getKink());
		drawSegments(edge);
	}
	
	/**
	 * Draw L (90° turns) and G (270° turns) edges.
	 * @param vertexCoordinates Start and end points of the edge.
	 * @param ports Start and end port of the edge.
	 */
	private void drawLGEdges(SmoothEdge edge) {
		drawing.edgeMidpoint(edge.getSegment(Arc.class).getMid(), "blue");
		drawing.edgeMidpoint(edge.getKink(), "red");
		drawSegments(edge);
	}
	
	private void drawSegments(SmoothEdge edge) {
		for (Segment segment : edge.getSegments()) {
			segment.draw(drawing, collisionManager.addAndCollides(segment));
		}
	}
	
}