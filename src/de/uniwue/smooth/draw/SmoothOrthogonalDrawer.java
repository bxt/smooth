package de.uniwue.smooth.draw;

import de.uniwue.smooth.collision.CollisionManager;
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
	
	private CollisionManager collisionDetecor;
	
	/**
	 * Draws the edges smoothly.
	 */
	@Override
	protected void drawEdges() {
		collisionDetecor = new CollisionManager();
		for (final E e : layout.getGraph().getEdges()) {
			Pair<V> endpoints = layout.getGraph().getEndpoints(e);
			Pair<Port> ports = new Pair<>(getPort(endpoints.getFirst(), e), getPort(endpoints.getSecond(), e));
			Pair<Pair<Integer>> vertexCoordinates = new Pair<>(layout.getVertexLocation(endpoints.getFirst()), layout.getVertexLocation(endpoints.getSecond()));
			
			if (ports.getFirst().getOpposite() == ports.getSecond()) { // I
				drawIEdges(vertexCoordinates, ports);
			} else if (ports.getFirst().isVertical() == ports.getSecond().isVertical()) { // U or C
				drawUCEdges(vertexCoordinates, ports);
			} else { // L or G
				drawLGEdges(vertexCoordinates, ports);
			}
		}
	}
	
	/**
	 * Draw linear edges (straight lines).
	 * @param vertexCoordinates Start and end points of the edge.
	 * @param ports Start and end port of the edge.
	 */
	private void drawIEdges(Pair<Pair<Integer>> vertexCoordinates, Pair<Port> ports) {
		// TODO: check for S shape?
		drawing.line(vertexCoordinates, collisionDetecor.addAndCollidesLine(vertexCoordinates));
	}
	
	/**
	 * Draw U oder C edges, i.e. 180° turns.
	 * @param vertexCoordinates Start and end points of the edge.
	 * @param ports Start and end port of the edge.
	 */
	private void drawUCEdges(Pair<Pair<Integer>> vertexCoordinates, Pair<Port> ports) {
		boolean isU = ports.getFirst().isVertical();
		boolean firstIsRighter = vertexCoordinates.getFirst().getFirst() > vertexCoordinates.getSecond().getFirst();
		boolean firstIsHigher = vertexCoordinates.getFirst().getSecond() > vertexCoordinates.getSecond().getSecond();
		boolean slopePositive = firstIsRighter == firstIsHigher;
		boolean isNegative = ports.getFirst() == Port.B || ports.getFirst() == Port.L;
		boolean firstIsGreater = isU ? firstIsHigher : firstIsRighter;
		
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
		drawing.edgeMidpoint(kink);
		
		drawing.line(end, kink, collisionDetecor.addAndCollidesLine(end, kink));
		
		if (slopePositive != isU) {
			drawing.arc(kink, start, collisionDetecor.addAndCollidesLine(kink, start));
		} else {
			drawing.arc(start, kink, collisionDetecor.addAndCollidesLine(start, kink));
		}
	}
	
	/**
	 * Draw L (90° turns) and G (270° turns) edges.
	 * @param vertexCoordinates Start and end points of the edge.
	 * @param ports Start and end port of the edge.
	 */
	private void drawLGEdges(Pair<Pair<Integer>> vertexCoordinates, Pair<Port> ports) {
		
		// Depending on the ports we have some switches to flip:
		
		Quadrant lQuadrant = null; // Quadrant in which the edges are L shaped
		boolean antidiagonal = false; // Which diagonal is significant for determining the start point of the circle arc
		boolean flipDiagonalStart = false; // Is the circle start above or below the diagonal
		if(ports.getFirst() == Port.R && ports.getSecond() == Port.T) { lQuadrant = Quadrant.IV ; antidiagonal =  true; flipDiagonalStart =  true; }
		if(ports.getFirst() == Port.B && ports.getSecond() == Port.L) { lQuadrant = Quadrant.IV ; antidiagonal =  true; flipDiagonalStart = false; }
		if(ports.getFirst() == Port.T && ports.getSecond() == Port.L) { lQuadrant = Quadrant.I  ; antidiagonal = false; flipDiagonalStart =  true; }
		if(ports.getFirst() == Port.R && ports.getSecond() == Port.B) { lQuadrant = Quadrant.I  ; antidiagonal = false; flipDiagonalStart = false; }
		if(ports.getFirst() == Port.L && ports.getSecond() == Port.B) { lQuadrant = Quadrant.II ; antidiagonal =  true; flipDiagonalStart = false; }
		if(ports.getFirst() == Port.T && ports.getSecond() == Port.R) { lQuadrant = Quadrant.II ; antidiagonal =  true; flipDiagonalStart =  true; }
		if(ports.getFirst() == Port.B && ports.getSecond() == Port.R) { lQuadrant = Quadrant.III; antidiagonal = false; flipDiagonalStart = false; }
		if(ports.getFirst() == Port.L && ports.getSecond() == Port.T) { lQuadrant = Quadrant.III; antidiagonal = false; flipDiagonalStart =  true; }
		if(lQuadrant == null) throw new IllegalStateException();
		boolean clockwise = ports.getFirst().getNext() == ports.getSecond();
		
		// Depending on the coordinates we have some more switches to flip:
		
		int dx =  vertexCoordinates.getSecond().getFirst() - vertexCoordinates.getFirst().getFirst();
		int dy =  vertexCoordinates.getSecond().getSecond() - vertexCoordinates.getFirst().getSecond();
		boolean lTyped = quadrant(dx, dy) == lQuadrant; // L shaped?
		boolean upper = (antidiagonal ? -1 : 1) * dx < dy; // Above or below diagonal?
		
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
			mid = new Pair<>(ref.getFirst() + dy * mid_factor,ref.getSecond());
			kink = Util.add(mid, new Pair<>(0, dy * kink_factor));
		} else {
			mid = new Pair<>(ref.getFirst(),ref.getSecond() + dx * mid_factor);
			kink = Util.add(mid, new Pair<>(dx * kink_factor, 0));
		}
		drawing.edgeMidpoint(mid, "blue");
		drawing.edgeMidpoint(kink, "red");
		
		Pair<Integer> lineStart = firstIsDiagonalStart ? vertexCoordinates.getSecond() : vertexCoordinates.getFirst();
		drawing.line(lineStart, kink, collisionDetecor.addAndCollidesLine(lineStart, kink));
		
		if(diagStartFirst) {
			drawing.arc(ref, mid, kink, collisionDetecor.addAndCollidesArc(ref, mid, kink));
		} else {
			drawing.arc(kink, mid, ref, collisionDetecor.addAndCollidesArc(kink, mid, ref));
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
	
	/**
	 * Quadrants of our coordinate system.
	 * 
	 * The coordinate system can be visualized like this:
	 *
	 *           T
	 *           ^ y
	 *     II    |     I
	 *           |
	 *  L -------0--------> R
	 *           |        x
	 *     III   |    IV
	 *           |
	 *           B
	 */
	private enum Quadrant {
		I,II,III,IV;	
	}
}