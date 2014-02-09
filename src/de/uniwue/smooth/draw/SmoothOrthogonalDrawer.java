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
				boolean slopeGreaterOne = Math.abs(dx / dy) > 1;
				boolean firstIsVertical = ports.getFirst().isVertical();
				boolean slopePositive = dx > 0 == dy > 0;
				
				boolean firstIsDiagonalStart = firstIsVertical == slopeGreaterOne; // choose second or first point as reference for further calculations
				boolean mid_changeXandAddDy = slopeGreaterOne; // which delta value to add to which coordinate
				boolean mid_subtract = slopePositive != firstIsDiagonalStart; // if or not to subtract the delta value from the reference point
				boolean kink_subtract = !firstIsDiagonalStart; // if or not to subtract the delta value from the midpoint
				boolean diagStartFirst = slopePositive != slopeGreaterOne; // if or not to go ccw from ref to kink or the other way around
				
				Pair<Integer> mid; // center of the circle arc
				Pair<Integer> kink; // point connecting line and circle arc
				Pair<Integer> ref = firstIsDiagonalStart ? vertexCoordinates.getFirst() : vertexCoordinates.getSecond(); // endpoint connected to arc
				int mid_factor = (mid_subtract ? -1 : 1); // fix sign of delta value for midpoint
				int kink_factor = (kink_subtract ? -1 : 1); // fix sign of delta value for kink point
				if(mid_changeXandAddDy) {
					mid = new Pair<>(ref.getFirst() + dy * mid_factor,ref.getSecond());
					kink = Util.add(mid, new Pair<>(0, dy * kink_factor));
				} else {
					mid = new Pair<>(ref.getFirst(),ref.getSecond() + dx * mid_factor);
					kink = Util.add(mid, new Pair<>(dx * kink_factor, 0));
				}
				drawing.edgeMidpoint(mid, "blue");
				drawing.edgeMidpoint(kink, "red");
				
				drawing.line(firstIsDiagonalStart ? vertexCoordinates.getSecond() : vertexCoordinates.getFirst(), kink);
				
				if(diagStartFirst) drawing.arc(ref, mid, kink); else drawing.arc(kink, mid, ref);
			}
			
			//drawing.edgeMidpoint(layout.getEdgeLocation(e));
		}
	}
	
	private Port getPort(V v, E e) {
		return Util.getKeyByValue(layout.getPortAssignment(v), e);
	}
	
}