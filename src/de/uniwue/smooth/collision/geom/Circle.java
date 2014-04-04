package de.uniwue.smooth.collision.geom;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import de.uniwue.smooth.util.point2d.Point2DTranformers;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents a 2D circle.
 */
public class Circle implements Bounded {
	
	private static final double EPSILON = 1E-11;
	
	private Point2D center;
	private double radius;
	
	/**
	 * Construct a circle with its central point and its radius.
	 * @param center Center of the circle.
	 * @param radius Radius of the circle.
	 */
	public Circle(Point2D center, double radius) {
		super();
		this.center = center;
		this.radius = radius;
	}

	public Point2D getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}
	
	/**
	 * Get a point on this circle at a certain angle.
	 * @param radians The angle around the x axis in radians.
	 * @return The point at this angle around the circle.
	 */
	public Point2D getPointAtAngle(double radians) {
		double x = Math.cos(radians) * getRadius() + getCenter().getX();
		double y = Math.sin(radians) * getRadius() + getCenter().getY();
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Calculates the intersection points between this circle and a {@link Line}.
	 * 
	 * @see <a href="http://mathworld.wolfram.com/Circle-LineIntersection.html">Circle-Line Intersection - from Wolfram MathWorld</a> 
	 * @param line The line to calculate the intersections for.
	 * @return The 0, 1 or 2 intersection points.
	 */
	public Collection<Point2D> intersections(Line line) {
		Line normalizedLine = new Line(Point2DOperations.subtract(line.getFrom(), getCenter()), Point2DOperations.subtract(line.getTo(), getCenter()));
		Collection<Point2D> intersectionsOrigin = intersectionsOrigin(normalizedLine, getRadius());
		return CollectionUtils.collect(intersectionsOrigin, new Point2DTranformers.Add(getCenter()));
		
	}
	
	private static Collection<Point2D> intersectionsOrigin(Line line, double radius) {
		double dX = line.getTo().getX() - line.getFrom().getX();
		double dY = line.getTo().getY() - line.getFrom().getY();
		double dr2 =sq(dX) + sq(dY);
		double dr = Math.sqrt(dr2);
		double D  = Point2DOperations.dot(line.getFrom(), line.getTo());
		
		double discriminant = sq(radius) * sq(dr) - sq(D);
		
		if(discriminant < 0) { // No intersections
			return Collections.emptyList();
		} else { // Intersections
			double a = D * dY;
			double b = -D * dX;
			
			if (discriminant > 0) { // Secant, Two intersections
				double c = sgn(dY) * dX * Math.sqrt(discriminant);
				double d = Math.abs(dY) * Math.sqrt(discriminant);
				
				return new Pair<Point2D>(new Point2D.Double((a + c) / dr2, (b + d) / dr2) ,
				                         new Point2D.Double((a - c) / dr2, (b - d) / dr2) );
			} else { // Tangent, one intersection
				return Collections.<Point2D>singletonList(
				                         new Point2D.Double( a      / dr2,  b      / dr2) );
			}
		}
		
	}
	
	/**
	 * Calculates the intersection points between this circle and a {@link Line}.
	 * 
	 * @param circle The other circle to calculate the intersections for.
	 * @return The 0, 1 or 2 intersection points, null if the circles are equal.
	 */
	public Collection<Point2D> intersections(Circle circle) {
		Line chordal = getChordal(circle);
		if(chordal == null)
			return getRadius() == circle.getRadius() ? null : Collections.<Point2D>emptyList();
		return intersections(chordal);
	}
	
	/**
	 * Get the line of points with equal distance to both circle lines.
	 * @param circle Other circle.
	 * @return A line of points at which a circle touching both circles exists or <null> if infinitely many such lines exists, i.e. the center coincide.
	 */
	public Line getChordal(Circle circle) {
		double a = 2 * (circle.getCenter().getX() - getCenter().getX());
		double b = 2 * (circle.getCenter().getY() - getCenter().getY());
		if (a == 0 && b == 0) return null;
		double c = sq(getRadius()) - sq(getCenter().getX()) - sq(getCenter().getY()) - sq(circle.getRadius()) + sq(circle.getCenter().getX()) + sq(circle.getCenter().getY());
		return new Line(a, b, c);
	}
	
	/**
	 * Calculates the intersection points between this circle and a {@link LineSegment}.
	 * 
	 * @param lineSegment The line segment to calculate the intersections for.
	 * @return The 0, 1 or 2 intersection points.
	 */
	public Collection<Point2D> intersections(final LineSegment lineSegment) {
		Collection<Point2D> intersectionsLine = intersections(lineSegment.getLine());
		return CollectionUtils.select(intersectionsLine, new Predicate<Point2D>() {
			@Override
			public boolean evaluate(Point2D point) {
				return lineSegment.contains(point);
			}
		});
	}
	
	/**
	 * Check if a points lies on the circle.
	 * @param point The point to check.
	 * @return If or not the point is on the circle.
	 */
	public boolean contains(Point2D point) {
		return fuzzyEquals(sq(getRadius()), sq(point.getX() - getCenter().getX()) + sq(point.getY() - getCenter().getY()));
	}
	
	@Override
	public String toString() {
		return "Circle [center=" + center + ", radius=" + radius + "]";
	}
	
	private static double sq(double a) {
		return a*a;
	}
	
	private static double sgn(double a) {
		return a < 0 ? -1 : 1;
	}
	
	private static boolean fuzzyEquals(double a, double b) {
	    if (a==b) return true;
	    return Math.abs(a - b) < EPSILON * Math.max(Math.abs(a), Math.abs(b));
	 }

	@Override
	public Box getBoundingBox() {
		return new Box(Interval.getIntervalCentered(center.getX(), radius), Interval.getIntervalCentered(center.getY(), radius));
	}
}
