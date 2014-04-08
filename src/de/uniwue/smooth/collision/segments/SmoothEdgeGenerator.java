package de.uniwue.smooth.collision.segments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.orthogonal.Quadrant;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * 
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class SmoothEdgeGenerator<V, E> implements EdgeGenerator<V, E, SmoothEdge> {
	
	OrthogonalLayout<V, E> layout;
	
	public SmoothEdgeGenerator(OrthogonalLayout<V, E> layout) {
		this.layout = layout;
	}

	@Override
	public SmoothEdge generateEdge(E e) {
		Pair<V> endpoints = layout.getGraph().getEndpoints(e);
		Pair<Port> ports = new Pair<>(getPort(endpoints.getFirst(), e), getPort(endpoints.getSecond(), e));
		Pair<Pair<Integer>> vertexCoordinates = new Pair<>(layout.getVertexLocation(endpoints.getFirst()), layout.getVertexLocation(endpoints.getSecond()));
		
		if (ports.getFirst().getOpposite() == ports.getSecond()) { // I or S
			return drawIEdges(vertexCoordinates, ports);
		} else if (ports.getFirst().isVertical() == ports.getSecond().isVertical()) { // U or C
			return drawUCEdges(vertexCoordinates, ports);
		} else { // L or G
			return drawLGEdges(vertexCoordinates, ports);
		}
	}
	
	/**
	 * Draw linear edges (straight lines).
	 * @param vertexCoordinates Start and end points of the edge.
	 * @param ports Start and end port of the edge.
	 */
	private SmoothEdge drawIEdges(Pair<Pair<Integer>> vertexCoordinates, Pair<Port> ports) {
		// TODO: check for S shape?
		return new SmoothEdge(Collections.<Segment>singletonList(new Segment.Line(vertexCoordinates)), null, SmoothEdgeType.I);
	}
	
	/**
	 * Draw U oder C edges, i.e. 180� turns.
	 * @param vertexCoordinates Start and end points of the edge.
	 * @param ports Start and end port of the edge.
	 */
	private SmoothEdge drawUCEdges(Pair<Pair<Integer>> vertexCoordinates, Pair<Port> ports) {
		Collection<Segment> segments = new ArrayList<Segment>(2);
		
		boolean isU = ports.getFirst().isVertical();
		Quadrant quadrant = Quadrant.getQuadrant(vertexCoordinates.getSecond(), vertexCoordinates.getFirst());
		boolean slopePositive = quadrant.isDiagonal();
		boolean isNegative = ports.getFirst() == Port.B || ports.getFirst() == Port.L;
		boolean firstIsGreater = isU ? quadrant.isUpper() : quadrant.isRight();
		
		Pair<Integer> end;
		Pair<Integer> start;
		if (firstIsGreater == isNegative) {
			end = vertexCoordinates.getFirst();
			start = vertexCoordinates.getSecond();
		} else {
			end = vertexCoordinates.getSecond();
			start = vertexCoordinates.getFirst();
		}
		Pair<Integer> kink = isU ?  new Pair<>(end.getFirst(), start.getSecond()) : new Pair<>(start.getFirst(), end.getSecond());
		
		if(!end.equals(kink))
			segments.add(new Segment.Line(end, kink));
		
		if (slopePositive != isU) {
			segments.add(new Segment.SemiArc(kink, start));
		} else {
			segments.add(new Segment.SemiArc(start, kink));
		}
		
		return new SmoothEdge(segments, kink, isU ? SmoothEdgeType.U : SmoothEdgeType.C);
	}
	
	/**
	 * Draw L (90� turns) and G (270� turns) edges.
	 * @param vertexCoordinates Start and end points of the edge.
	 * @param ports Start and end port of the edge.
	 */
	private SmoothEdge drawLGEdges(Pair<Pair<Integer>> vertexCoordinates, Pair<Port> ports) {
		Collection<Segment> segments = new ArrayList<Segment>(2);
		
		// Depending on the ports we have some switches to flip:
		
		Quadrant lQuadrant = Quadrant.getQuadrant(Util.subtract(ports.getFirst().getDirection(), ports.getSecond().getDirection())); // Quadrant in which the edges are L shaped
		boolean flipDiagonalStart = ports.getFirst() == Port.T || ports.getSecond() == Port.T; // Is the circle start above or below the diagonal
		boolean antidiagonal = ! lQuadrant.isDiagonal(); // Which diagonal is significant for determining the start point of the circle arc
		boolean clockwise = ports.getFirst().getNext() == ports.getSecond(); // Draw the arc in clockwise or counter-clockwise direction
		
		// Depending on the coordinates we have some more switches to flip:
		
		Pair<Integer> diff = Util.subtract(vertexCoordinates.getSecond(), vertexCoordinates.getFirst());
		boolean lTyped = Quadrant.getQuadrant(diff) == lQuadrant; // L shaped?
		boolean upper = (antidiagonal ? -1 : 1) * diff.getFirst() < diff.getSecond(); // Above or below diagonal?
		
		// Now with these switches we can gather the parameters for our algorithm:
		
		boolean firstIsDiagonalStart = upper != flipDiagonalStart; // choose second or first point as reference for further calculations
		boolean mid_changeXandAddDy = firstIsDiagonalStart != clockwise != antidiagonal; // which delta value to add to which coordinate
		boolean mid_subtract = firstIsDiagonalStart == antidiagonal; // if or not to subtract the delta value from the reference point
		boolean kink_subtract = !firstIsDiagonalStart; // if or not to subtract the delta value from the midpoint
		boolean diagStartFirst = firstIsDiagonalStart != lTyped != clockwise; // if or not to go ccw from ref to kink or the other way around
		
		// Finally run the algorithm:
		
		Pair<Integer> mid; // center of the circle arc
		Pair<Integer> kink; // point connecting line and circle arc
		Pair<Integer> ref = firstIsDiagonalStart ? vertexCoordinates.getFirst() : vertexCoordinates.getSecond(); // endpoint connected to arc
		int mid_factor = (mid_subtract ? -1 : 1); // fix sign of delta value for midpoint
		int kink_factor = (kink_subtract ? -1 : 1); // fix sign of delta value for kink point
		if(mid_changeXandAddDy) {
			mid = new Pair<>(ref.getFirst() + diff.getSecond() * mid_factor,ref.getSecond());
			kink = Util.add(mid, new Pair<>(0, diff.getSecond() * kink_factor));
		} else {
			mid = new Pair<>(ref.getFirst(),ref.getSecond() + diff.getFirst() * mid_factor);
			kink = Util.add(mid, new Pair<>(diff.getFirst() * kink_factor, 0));
		}
		
		Pair<Integer> lineStart = firstIsDiagonalStart ? vertexCoordinates.getSecond() : vertexCoordinates.getFirst();
		if(!lineStart.equals(kink))
			segments.add(new Segment.Line(lineStart, kink));
		
		if(diagStartFirst) {
			segments.add(new Segment.Arc(ref, mid, kink));
		} else {
			segments.add(new Segment.Arc(kink, mid, ref));
		}
		
		return new SmoothEdge(segments, kink, lTyped ? SmoothEdgeType.L : SmoothEdgeType.G);
	}
	
	private Port getPort(V v, E e) {
		return Util.getKeyByValue(layout.getPortAssignment(v), e);
	}
}