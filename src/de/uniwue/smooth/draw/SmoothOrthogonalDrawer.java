package de.uniwue.smooth.draw;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Performs the drawing of smooth primitives into an {@link OrthogonalDrawing} given an {@link OrthogonalLayout}.
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 * @param <T> Type of resulting painting.
 */
public class SmoothOrthogonalDrawer<V, E, T> extends AbstractOrthogonalDrawer<V, E, T> implements OrthogonalDrawer<V, E, T> {
	
	/**
	 * Draws the edges smoothly.
	 */
	@Override
	protected void drawEdges() {
		for (final E e : layout.getGraph().getEdges()) {
			Pair<V> endpoints = layout.getGraph().getEndpoints(e);
			Pair<Port> ports = new Pair<>(getPort(endpoints.getFirst(), e), getPort(endpoints.getSecond(), e));
			Pair<Pair<Integer>> vertexCoordinates = new Pair<>(layout.getVertexLocation(endpoints.getFirst()), layout.getVertexLocation(endpoints.getSecond()));
			
			if (ports.getFirst().getOpposite() == ports.getSecond()) { // Line
				drawing.line(vertexCoordinates);
			} else if (ports.getFirst().isHorizontal() && ports.getSecond().isHorizontal()) { // C
				boolean firstIsRighter = vertexCoordinates.getFirst().getFirst() > vertexCoordinates.getSecond().getFirst();
				boolean isLeft = ports.getFirst() == Port.L;
				Pair<Integer> tucked;
				Pair<Integer> start;
				if ((firstIsRighter && isLeft) || (!firstIsRighter && !isLeft)) {
					tucked = vertexCoordinates.getFirst();
					start = vertexCoordinates.getSecond();
				} else {
					tucked = vertexCoordinates.getSecond();
					start = vertexCoordinates.getFirst();
				}
				Pair<Integer> end = new Pair<>(start.getFirst(), tucked.getSecond());
//				Pair<Integer> mid = new Pair<>(start.getFirst(), (tucked.getSecond() + start.getSecond())/2);
				if (isLeft)
					drawing.arc(start, end);
				else
					drawing.arc(end, start);
				drawing.line(tucked, end);
				drawing.edgeMidpoint(end);
			}
			
			// TODO: L, O edge
			
			//drawing.edgeMidpoint(layout.getEdgeLocation(e));
		}
	}
	
	private Port getPort(V v, E e) {
		return Util.getKeyByValue(layout.getPortAssignment(v), e);
	}
	
}