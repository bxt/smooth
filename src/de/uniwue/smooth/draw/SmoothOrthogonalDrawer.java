package de.uniwue.smooth.draw;

import de.uniwue.smooth.app.EdgeTestDrawings;
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
				boolean firstIsHigher = vertexCoordinates.getFirst().getSecond() > vertexCoordinates.getSecond().getSecond();
				boolean slopePositive = firstIsRighter == firstIsHigher;
				boolean isLeft = ports.getFirst() == Port.L;
				
				Pair<Integer> end;
				Pair<Integer> start;
				if (firstIsRighter == isLeft) {
					end = vertexCoordinates.getFirst();
					start = vertexCoordinates.getSecond();
				} else {
					end = vertexCoordinates.getSecond();
					start = vertexCoordinates.getFirst();
				}
				Pair<Integer> kink = new Pair<>(start.getFirst(), end.getSecond());
				drawing.edgeMidpoint(kink);
				
				drawing.line(end, kink);
				
				if (slopePositive) drawing.arc(kink, start); else drawing.arc(start, kink);
				
			} else { // L or G
				
				Quadrant lQuadrant = null;
				Port fIDS1 = null;
				Port fIDS2 = null;
				int mask = 0;
				if(ports.getFirst() == Port.R && ports.getSecond() == Port.T) { lQuadrant = Quadrant.IV ; fIDS1 = Port.B; fIDS2 = Port.L; mask = 0b11001; }
				if(ports.getFirst() == Port.B && ports.getSecond() == Port.L) { lQuadrant = Quadrant.IV ; fIDS1 = Port.T; fIDS2 = Port.R; mask = 0b01100; }
				if(ports.getFirst() == Port.T && ports.getSecond() == Port.L) { lQuadrant = Quadrant.I  ; fIDS1 = Port.R; fIDS2 = Port.B; mask = 0b10010; }
				if(ports.getFirst() == Port.R && ports.getSecond() == Port.B) { lQuadrant = Quadrant.I  ; fIDS1 = Port.L; fIDS2 = Port.T; mask = 0b00111; }
				if(ports.getFirst() == Port.L && ports.getSecond() == Port.B) { lQuadrant = Quadrant.II ; fIDS1 = Port.B; fIDS2 = Port.L; mask = 0b01001; }
				if(ports.getFirst() == Port.T && ports.getSecond() == Port.R) { lQuadrant = Quadrant.II ; fIDS1 = Port.T; fIDS2 = Port.R; mask = 0b11100; }
				if(ports.getFirst() == Port.B && ports.getSecond() == Port.R) { lQuadrant = Quadrant.III; fIDS1 = Port.R; fIDS2 = Port.B; mask = 0b00010; }
				if(ports.getFirst() == Port.L && ports.getSecond() == Port.T) { lQuadrant = Quadrant.III; fIDS1 = Port.L; fIDS2 = Port.T; mask = 0b10111; }
				
				int dx =  vertexCoordinates.getSecond().getFirst() - vertexCoordinates.getFirst().getFirst();
				int dy =  vertexCoordinates.getSecond().getSecond() - vertexCoordinates.getFirst().getSecond();
				Quadrant quadrant = quadrant(dx, dy);
				boolean upper = (0 < (mask & 8) ? -1 : 1) * dx < dy;
				boolean lTyped = quadrant == lQuadrant;
				
				boolean firstIsDiagonalStart = upper != (0 < (mask & 16)); // choose second or first point as reference for further calculations
				boolean mid_changeXandAddDy = firstIsDiagonalStart != (0 < (mask & 1)); // which delta value to add to which coordinate
				boolean mid_subtract = firstIsDiagonalStart != (0 < (mask & 2)); // if or not to subtract the delta value from the reference point
				boolean kink_subtract = !firstIsDiagonalStart; // if or not to subtract the delta value from the midpoint
				boolean diagStartFirst = firstIsDiagonalStart != lTyped != (0 < (mask & 4)); // if or not to go ccw from ref to kink or the other way around
				
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
	
	private static Quadrant quadrant(int x, int y) {
		if(x>0) {
			return y > 0 ? Quadrant.I : Quadrant.IV;
		} else {
			return y > 0 ? Quadrant.II : Quadrant.III;
		}
	}
	
	private enum Quadrant {
		I,II,III,IV;	
	}
}