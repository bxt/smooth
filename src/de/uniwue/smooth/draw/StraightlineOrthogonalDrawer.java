package de.uniwue.smooth.draw;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Performs the drawing of primitives into an {@link OrthogonalDrawing} given an {@link OrthogonalLayout}.
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 * @param <T> Type of resulting painting.
 */
public class StraightlineOrthogonalDrawer<V, E, T> extends AbstractOrthogonalDrawer<V, E, T> implements OrthogonalDrawer<V, E, T> {
	
	/**
	 * Draws the edges with the following method:
	 * <ul>
	 *   <li>Start at one endpoint vertex.
	 *   <li>Move and draw 1 unit into the direction of the port
	 *   <li>Move and draw horizontally to the left-position of the edge midpoint
	 *   <li>Save that position
	 *   <li>Repeat these steps for the other endpoint vertex.
	 *   <li>Connect both saved positions with a vertical line.
	 * </ul>
	 */
	@Override
	protected void drawEdges() {
		for (final E e : layout.getGraph().getEdges()) {
			Pair<Pair<Integer>> edgeCoordinates = new Pair<>(CollectionUtils.collect(layout.getGraph().getEndpoints(e), new Transformer<V, Pair<Integer>>() {
				@Override
				public Pair<Integer> transform(V v) {
					return drawEdgeHalf(v, e);
				}
			}));
			
			drawing.line(edgeCoordinates);
			drawing.edgeMidpoint(layout.getEdgeLocation(e));
		}
	}
	
	private Pair<Integer> drawEdgeHalf(V v, E e) {
		Pair<Integer> vertexCoordinates = layout.getVertexLocation(v);
		Port port = Util.getKeyByValue(layout.getPortAssignment(v), e);
		Pair<Integer> portCoordinates = Util.add(vertexCoordinates, port.getDirection());
		Pair<Integer> edgeCoordinates = new Pair<Integer>(layout.getEdgeLocation(e).getFirst(), portCoordinates.getSecond());
		
		drawing.line(vertexCoordinates, portCoordinates);
		drawing.line(portCoordinates, edgeCoordinates);
		
		return edgeCoordinates;
	}
	
}