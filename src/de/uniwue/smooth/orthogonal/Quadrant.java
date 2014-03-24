package de.uniwue.smooth.orthogonal;

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
public enum Quadrant {
	
	/**
	 * The top right quadrant. 
	 */
	I,
	/**
	 * The top left quadrant.
	 */
	II,
	/**
	 * The bottom left quadrant.
	 */
	III,
	/**
	 * The bottom right quadrant.
	 */
	IV,
	;
	
	/**
	 * Get the Quadrant of a point by its x and y values.
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @return The Quadrant this point is part of.
	 */
	public static Quadrant getQuadrant(int x, int y) {
		return getQuadrant(x > 0,  y > 0);
	}
	
	/**
	 * Get the Quadrant of a point by its x and y values.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @return The Quadrant this point is part of.
	 */
	public static Quadrant getQuadrant(double x, double y) {
		return getQuadrant(x > 0,  y > 0);
	}
	
	/**
	 * Get the Quadrant by its position relative to the coordinate axes.
	 * @param isRight If a quadrant right of the y-axis is to be returned. (positive values of x)
	 * @param isUpper If a quadrant above the x-axis is to be returned. (positive values of y
	 * @return The unique Quadrant matching the criteria.
	 */
	public static Quadrant getQuadrant(boolean isRight, boolean isUpper) {
		if(isRight) {
			return isUpper ? I : IV;
		} else {
			return isUpper ? II : III;
		}
	}
	
	private boolean isUpper;
	private boolean isRight;
	
	static {
		I.isUpper = true;
		II.isUpper = true;
		III.isUpper = false;
		IV.isUpper = false;
	}
	
	static {
		I.isRight = true;
		II.isRight = false;
		III.isRight = false;
		IV.isRight = true;
	}
	
	/**
	 * If or not this quadrant is above the x axis.
	 * @return
	 */
	public boolean isUpper() {
		return isUpper;
	}
	
	/**
	 * If or not this quadrant is below the x axis.
	 * @return
	 */
	public boolean isRight() {
		return isRight;
	}
	
	/**
	 * The next quadrant clockwise of this quadrant (rotate by 90 degrees). 
	 * @return The next quadrant.
	 */
	public Quadrant getNext() {
		return values()[(values().length + ordinal() - 1) % values().length];
	}
	
	/**
	 * The previous quadrant clockwise (next quadrant counter-clockwise) of this quadrant (rotate by - 90 degrees). 
	 * @return The previous quadrant.
	 */
	public Quadrant getPrev() {
		return values()[(ordinal() + 1) % values().length];
	}
	
	/**
	 * The quadrant on the other side of this quadrant (mirror at origin, diagonal opposite, rotate by 180 degrees). 
	 * @return The opposite quadrant.
	 */
	public Quadrant getOpposite() {
		return values()[(ordinal() + 2) % values().length];
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isDiagonal() {
		return isRight == isUpper;
	}
	
	/**
	 * Get the port adjacent to this quadrant at the x-axis.
	 * @return Left or right port, depending on the side of this quadrant around the y-axis.
	 */
	public Port getHorizontalPort() {
		return isRight ? Port.R : Port.L;
	}
	
	
	/**
	 * Get the port adjacent to this quadrant at the y-axis.
	 * @return Top or bottom port, depending on the side of this quadrant around the x-axis.
	 */
	public Port getVerticalPort() {
		return isUpper ? Port.T : Port.B;
	}
	
	/**
	 * Get the quadrant on the other side of the x-axis.
	 * @return Quadrant on the other side of the x-axis.
	 */
	public Quadrant getVerticalOpposite() {
		return Quadrant.getQuadrant(isRight, !isUpper);
	}
	
	/**
	 * Get the quadrant on the other side of the y-axis.
	 * @return Quadrant on the other side of the y-axis.
	 */
	public Quadrant getHorizontalOpposite() {
		return Quadrant.getQuadrant(!isRight, isUpper);
	}
	
}