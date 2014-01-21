package de.uniwue.smooth.orthogonal;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents one of the four sides ("ports") around a vertex in an orthogonal drawing.
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
	private Pair<Integer> direction;
	
	static {
		L.opposite = R;
		R.opposite = L;
		T.opposite = B;
		B.opposite = T;
	}
	
	static {
		L.direction = new Pair<>(-1, 0);
		R.direction = new Pair<>( 1, 0);
		T.direction = new Pair<>( 0, 1);
		B.direction = new Pair<>( 0,-1);
	}

	/**
	 * The port on the other side of this port (rotated by 180 degrees). 
	 * @return The opposite port.
	 */
	public Port getOpposite() {
		return opposite;
	}
	
	/**
	 * An normalized integer vector of length 1 pointing in the direction of this port with
	 * the first number being the x and the second the y coordinate.
	 * @return A pair of integers with sqrt(x^2+y^2) = 1.
	 */
	public Pair<Integer> getDirection() {
		return direction;
	}
}
