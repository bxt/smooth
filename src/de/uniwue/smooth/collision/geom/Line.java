package de.uniwue.smooth.collision.geom;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents a 2D line.
 */
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
	
	/**
	 * Calculates the intersection points between this line and another one.
	 * 
	 * @param line The other circle to calculate the intersections for.
	 * @return The 0 or 1 intersection points, null if the lines are equal.
	 */
	public Collection<Point2D> intersections(Line line) {
		double dX = getTo().getX() - getFrom().getX();
		double dY = getTo().getY() - getFrom().getY();
		double ldX = line.getTo().getX() - line.getFrom().getX();
		double ldY = line.getTo().getY() - line.getFrom().getY();
		double denominator = dX * ldY - dY * ldX;
		if(denominator == 0) {
			if(line.contains(getFrom())) { // equal
				return null;
			} else { // parallel
				return Collections.<Point2D>emptyList();
			}
		} else {
			double diffThis =      getTo().getX() *      getFrom().getY() -      getTo().getY() *      getFrom().getX();
			double diffLine = line.getTo().getX() * line.getFrom().getY() - line.getTo().getY() * line.getFrom().getX();
			return Collections.<Point2D>singletonList(new Point2D.Double(
					(diffThis * ldX - dX * diffLine)/denominator,
					(diffThis * ldY - dY * diffLine)/denominator));
		}
	}
	
	/**
	 * Calculates the intersection points between this line and a {@link LineSegment}.
	 * 
	 * @param lineSegment The line segment to calculate the intersections for.
	 * @return The 0 or 1 intersection points, null if the line segment is fully contained in this line.
	 */
	public Collection<Point2D> intersections(final LineSegment lineSegment) {
		Collection<Point2D> intersectionsLine = intersections(lineSegment.getLine());
		if(intersectionsLine == null) return null;
		return CollectionUtils.select(intersectionsLine, new Predicate<Point2D>() {
			@Override
			public boolean evaluate(Point2D point) {
				return lineSegment.contains(point);
			}
		});
	}
	
	/**
	 * Check if a points lies on the line.
	 * @param point The point to check.
	 * @return If or not the point is on the line.
	 */
	public boolean contains(Point2D point) {
		return Point2DOperations.dot(point, Point2DOperations.subtract(getTo(), getFrom())) == Point2DOperations.dot(getFrom(), getTo());
	}

	@Override
	public String toString() {
		return "Line [from=" + from + ", to=" + to + "]";
	}
}
