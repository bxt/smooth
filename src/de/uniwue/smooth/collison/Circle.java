package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents a 2D circle.
 */
public class Circle {
	
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
	 * Calculates the intersection points between this circle and a {@link Line}.
	 * 
	 * @see <a href="http://mathworld.wolfram.com/Circle-LineIntersection.html">Circle-Line Intersection - from Wolfram MathWorld</a> 
	 * @param line The line to calculate the intersections for.
	 * @return The 0, 1 or 2 intersection points.
	 */
	public Collection<Point2D> intersections(Line line) {
		Line normalizedLine = new Line(Point2DOperations.subtract(line.getFrom(), center), Point2DOperations.subtract(line.getTo(), center));
		double dX = normalizedLine.getTo().getX() - normalizedLine.getFrom().getX();
		double dY = normalizedLine.getTo().getY() - normalizedLine.getFrom().getY();
		double dr2 =sq(dX) + sq(dY);
		double dr = Math.sqrt(dr2);
		double D  = Point2DOperations.dot(normalizedLine.getFrom(), normalizedLine.getTo());
		
		double discriminant = sq(radius) * sq(dr) - sq(D);
		
		if(discriminant < 0) { // No intersections
			return Collections.emptyList();
		} else { // Intersections
			double a = D * dY;
			double b = -D * dX;
			
			if (discriminant > 0) { // Secant, Two intersections
				double c = sgn(dY) * dX * Math.sqrt(discriminant);
				double d = Math.abs(dY) * Math.sqrt(discriminant);
				
				return new Pair<Point2D>(Point2DOperations.add(new Point2D.Double((a + c) / dr2, (b + d) / dr2), center) ,
				                         Point2DOperations.add(new Point2D.Double((a - c) / dr2, (b - d) / dr2), center) );
			} else { // Tangent, one intersection
				return Collections.<Point2D>singletonList(
				                         Point2DOperations.add(new Point2D.Double( a      / dr2,  b      / dr2), center));
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
		if(chordal == null) return null;
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
	
	private static double sq(double a) {
		return a*a;
	}
	
	private static double sgn(double a) {
		return a < 0 ? -1 : 1;
	}
	
}
