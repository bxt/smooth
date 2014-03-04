package de.uniwue.smooth.collison;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.uniwue.smooth.util.point2d.Point2DComparatorFactories;

public class CircleTest {

	private static double DELTA = 0.0001;
	
	@Test
	public void testNoIntersectionsLine() {
		Circle circle = new Circle(p(4, 5), 2);
		Line line = new Line(p(5, 3), p(6, 4));
		
		Collection<Point2D> intersections = circle.intersections(line);
		assertEquals(0, intersections.size());
	}
	
	@Test
	public void testTangentLine() {
		Circle circle = new Circle(p(4, 5), 2);
		Line line = new Line(p(-2, 3), p(-5, 3));
		
		List<Point2D> intersections = sortIntersections(circle.intersections(line));
		assertEquals(1, intersections.size());
		
		assertEquals(4,	intersections.get(0).getX(), DELTA);
		assertEquals(3,	intersections.get(0).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsLine() {
		Circle circle = new Circle(p(4, 5), 2);
		Line line = new Line(p(3, 2), p(7, 6));
		
		List<Point2D> intersections = sortIntersections(circle.intersections(line));
		assertEquals(2, intersections.size());
		
		assertEquals(4,	intersections.get(0).getX(), DELTA);
		assertEquals(3,	intersections.get(0).getY(), DELTA);
		
		assertEquals(6,	intersections.get(1).getX(), DELTA);
		assertEquals(5,	intersections.get(1).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsLineHorizontal() {
		Circle circle = new Circle(p(4, 5), 2);
		Line line = new Line(p(3, 5), p(2, 5));
		
		List<Point2D> intersections = sortIntersections(circle.intersections(line));
		assertEquals(2, intersections.size());
		
		assertEquals(2,	intersections.get(0).getX(), DELTA);
		assertEquals(5,	intersections.get(0).getY(), DELTA);
		
		assertEquals(6,	intersections.get(1).getX(), DELTA);
		assertEquals(5,	intersections.get(1).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsLineVertical() {
		Circle circle = new Circle(p(4, 5), 2);
		Line line = new Line(p(4, 2), p(4, 1));
		
		List<Point2D> intersections = sortIntersections(circle.intersections(line));
		assertEquals(2, intersections.size());
		
		assertEquals(4,	intersections.get(0).getX(), DELTA);
		assertEquals(3,	intersections.get(0).getY(), DELTA);
		
		assertEquals(4,	intersections.get(1).getX(), DELTA);
		assertEquals(7,	intersections.get(1).getY(), DELTA);
	}
	
	@Test
	public void testGetChordal() {
		Circle circleA = new Circle(p(3, 4), 2);
		Circle circleB = new Circle(p(5, 6), 2);
		Line chordal = circleA.getChordal(circleB);
		assertTrue(chordal.contains(p(3, 6)));
		assertTrue(chordal.contains(p(5, 4)));
	}
	
	@Test
	public void testGetChordalFails() {
		Circle circleA = new Circle(p(3, 4), 2);
		Circle circleB = new Circle(p(3, 4), 2);
		assertNull(circleA.getChordal(circleB));
	}
	
	@Test
	public void testIntersectionsCircle() {
		Circle circleA = new Circle(p(3, 4), 2);
		Circle circleB = new Circle(p(5, 6), 2);
		
		List<Point2D> intersections = sortIntersections(circleA.intersections(circleB));
		assertEquals(2, intersections.size());
		
		assertEquals(3,	intersections.get(0).getX(), DELTA);
		assertEquals(6,	intersections.get(0).getY(), DELTA);
		
		assertEquals(5,	intersections.get(1).getX(), DELTA);
		assertEquals(4,	intersections.get(1).getY(), DELTA);
	}
	
	private static List<Point2D> sortIntersections(Collection<Point2D> intersections) {
		List<Point2D> intersectionList = new ArrayList<>(intersections);
		Collections.sort(intersectionList, Point2DComparatorFactories.xy());
		return intersectionList;
	}
	
	private static Point2D p(double x, double y) {
		return new Point2D.Double(x, y);
	}
	
}
