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
			} else if (ports.getFirst().isVertical() && ports.getSecond().isVertical()) { // U
				throw new UnsupportedOperationException("U edges not implemented.");
			} else if (ports.getFirst().isHorizontal() && ports.getSecond().isHorizontal()) { // C
				boolean firstIsRighter = vertexCoordinates.getFirst().getFirst() > vertexCoordinates.getSecond().getFirst();
				boolean isLeft = ports.getFirst() == Port.L;
				Pair<Integer> end;
				Pair<Integer> start;
				if ((firstIsRighter && isLeft) || (!firstIsRighter && !isLeft)) {
					end = vertexCoordinates.getFirst();
					start = vertexCoordinates.getSecond();
				} else {
					end = vertexCoordinates.getSecond();
					start = vertexCoordinates.getFirst();
				}
				Pair<Integer> kink = new Pair<>(start.getFirst(), end.getSecond());
				if (isLeft)
					drawing.arc(start, kink);
				else
					drawing.arc(kink, start);
				drawing.line(end, kink);
				drawing.edgeMidpoint(kink);
			} else { // L or G
				int dx =  vertexCoordinates.getSecond().getFirst() - vertexCoordinates.getFirst().getFirst();
				int dy =  vertexCoordinates.getSecond().getSecond() - vertexCoordinates.getFirst().getSecond();
				boolean slopeGreaterOne = dx / dy > 1;
				boolean firstIsVertical = ports.getFirst().isVertical();
				boolean slopePositive = dy > 0 == dy > 0;
				boolean firstIsDiagonalStart = firstIsVertical == slopeGreaterOne;
				
				boolean mid_addToFirst = firstIsDiagonalStart;
				boolean mid_changeXandAddDy = slopeGreaterOne;
				boolean mid_subtract = slopeGreaterOne == firstIsDiagonalStart;
				
				Pair<Integer> mid;
				Pair<Integer> kink;
				Pair<Integer> ref = mid_addToFirst ? vertexCoordinates.getFirst() : vertexCoordinates.getSecond();
				int factor = (mid_subtract ? -1 : 1);
				int kink_factor = (firstIsDiagonalStart ? 1 : -1);
				if(mid_changeXandAddDy) {
					mid = new Pair<>(ref.getFirst() + dy * factor,ref.getSecond());
					kink = new Pair<>(ref.getFirst() + dy * factor,ref.getSecond() - dy);
				} else {
					mid = new Pair<>(ref.getFirst(),ref.getSecond() + dx * factor);
					kink = new Pair<>(ref.getFirst() + dx * kink_factor,ref.getSecond() + dx * factor);
				}
				drawing.edgeMidpoint(mid, "blue");
				drawing.edgeMidpoint(kink, "red");
				
				drawing.line(firstIsDiagonalStart ? vertexCoordinates.getSecond() : vertexCoordinates.getFirst(), kink);
				
				drawing.arc(firstIsDiagonalStart ? vertexCoordinates.getFirst() : vertexCoordinates.getSecond(), mid, kink);
			}
			
			//drawing.edgeMidpoint(layout.getEdgeLocation(e));
		}
	}
	
	private Port getPort(V v, E e) {
		return Util.getKeyByValue(layout.getPortAssignment(v), e);
	}
	
}