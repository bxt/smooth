package de.uniwue.smooth.orthogonal;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents one of the four sides ("ports") around a vertex in an orthogonal drawing.
 * 
 * The coordinate system can be visualized like this:
 *
 * <pre>
 *           T
 *           ^ y
 *     II    |     I
 *           |
 *  L -------0--------> R
 *           |        x
 *     III   |    IV
 *           |
 *           B
 * </pre>
 */
public enum Port {
	/**
	 * The left port (negative X direction)
	 */
	L,
	/**
	 * The right port (positive X direction)
	 */
	R,
	/**
	 * The top port (positive Y direction)
	 */
	T,
	/**
	 * The bottom port (negative Y direction)
	 */
	B,
	;
	
	private Port opposite;
	private Port next;
	private Pair<Integer> direction;
	private boolean horizontal;
	
	static {
		L.opposite = R;
		R.opposite = L;
		T.opposite = B;
		B.opposite = T;
	}
	
	static {
		L.next = T;
		R.next = B;
		T.next = R;
		B.next = L;
	}
	
	static {
		L.direction = new Pair<>(-1, 0);
		R.direction = new Pair<>( 1, 0);
		T.direction = new Pair<>( 0, 1);
		B.direction = new Pair<>( 0,-1);
	}

	static {
		L.horizontal = true;
		R.horizontal = true;
		T.horizontal = false;
		B.horizontal = false;
	}

	/**
	 * The port on the other side of this port (rotated by 180 degrees). 
	 * @return The opposite port.
	 */
	public Port getOpposite() {
		return opposite;
	}
	
	/**
	 * The next port clockwise of this port (rotate by 90 degrees). 
	 * @return The next port.
	 */
	public Port getNext() {
		return next;
	}
	
	/**
	 * An normalized integer vector of length 1 pointing in the direction of this port with
	 * the first number being the x and the second the y coordinate.
	 * @return A pair of integers with sqrt(x^2+y^2) = 1.
	 */
	public Pair<Integer> getDirection() {
		return direction;
	}
	
	/**
	 * If or not the port points to a horizontal direction.
	 * @return true, iff the port is horizontal.
	 */
	public boolean isHorizontal() {
		return horizontal;
	}
	
	/**
	 * If or not the port points to a vertical direction.
	 * @return true, iff the port is vertical.
	 */
	public boolean isVertical() {
		return ! horizontal;
	}
}
