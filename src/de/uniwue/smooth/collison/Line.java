package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import de.uniwue.smooth.util.point2d.Point2DTranformers;
import edu.uci.ics.jung.graph.util.Pair;

public class Line {
	
	private Point2D from;
	private Point2D to;

	public Line(Pair<Integer> from, Pair<Integer> to) {
		this(new Pair<>(from, to));
	}

	public Line(Pair<Pair<Integer>> endpoints) {
		this(Point2DOperations.fromIntegerPair(endpoints.getFirst() ),
			 Point2DOperations.fromIntegerPair(endpoints.getSecond()));
	}

	/**
	 * Create a line through two points. The points may not be equal.
	 * @param from One point.
	 * @param to Other point.
	 */
	public Line(Point2D from, Point2D to) {
		if (from.equals(to)) throw new IllegalArgumentException();
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Create a line from the coefficients of its slope–intercept equation or <tt>y = mx + b</tt>.
	 * 
	 * Cannot be used for constructing vertical lines.
	 * 
	 * @param m Slope of the line
	 * @param b Y-coordinate where the line crosses the x-axis.
	 */
	public Line(double m, double b) {
		this(-m, 1, b);
	}

	/**
	 * Create a line from the coefficients of its general equation <tt>ax + by = c</tt>
	 * or <tt>y = (c - ax) / b</tt>.
	 * 
	 * @param a Factor of <tt>y<tt>.
	 * @param b Factor of <tt>x<tt>.
	 * @param c Constant.
	 */
	public Line(double a, double b, double c) {
		double x1, y1, x2, y2;
		
		if (a == 0 && b == 0) {
			throw new IllegalArgumentException();
		} else if (b == 0) { // parallel to y axis.
			x1 = c / a;
			x2 = c / a;
			y1 = 0;
			y2 = 1;
		} else { // a== 0 -> parallel to x axis or other cases
			x1 = 0;
			x2 = 1;
			y1 = c / b;
			y2 = (c - a) / b;
		}
		
		from = new Point2D.Double(x1, y1);
		to   = new Point2D.Double(x2, y2);
	}

	public Point2D getFrom() {
		return from;
	}

	public Point2D getTo() {
		return to;
	}
	
	public boolean contains(Point2D point) {
		return Point2DOperations.dot(point, Point2DOperations.subtract(to, from)) == Point2DOperations.dot(from, to);
	}
}
