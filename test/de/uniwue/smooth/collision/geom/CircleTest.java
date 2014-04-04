package de.uniwue.smooth.collision.geom;

import static de.uniwue.smooth.collision.geom.TestUtils.*;
import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class CircleTest {

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
	
	/*
	 * Values from http://ne.lo-net2.de/selbstlernmaterial/m/kg/kr/kr_gw8.pdf
	 */
	@Test
	public void testIntersectionsCircle2() {
		Circle circleA = new Circle(p(7.0, 6.0), 5);
		Circle circleB = new Circle(p(7.5, 9.5), Math.sqrt(12.5));
		
		List<Point2D> intersections = sortIntersections(circleA.intersections(circleB));
		assertEquals(2, intersections.size());
		
		assertEquals( 4,	intersections.get(0).getX(), DELTA);
		assertEquals(10,	intersections.get(0).getY(), DELTA);
		
		assertEquals(11,	intersections.get(1).getX(), DELTA);
		assertEquals( 9,	intersections.get(1).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsCircleOverlap() {
		Circle circleA = new Circle(p(7, 6), 5);
		Circle circleB = new Circle(p(7, 6), 5);
		
		assertNull(circleA.intersections(circleB));
	}
	
	@Test
	public void testNoIntersectionsCircle() {
		Circle circleA = new Circle(p(4.0, 5.0), 2);
		Circle circleB = new Circle(p(7.5, 1.5), 2);
		
		List<Point2D> intersections = sortIntersections(circleA.intersections(circleB));
		assertEquals(0, intersections.size());
	}
	
	@Test
	public void testNoIntersectionsCircleSameCenter() {
		Circle circleA = new Circle(p(3, 4), 2);
		Circle circleB = new Circle(p(3, 4), 3);
		
		List<Point2D> intersections = sortIntersections(circleA.intersections(circleB));
		assertEquals(0, intersections.size());
	}
	
	@Test
	public void testTangentCircle() {
		Circle circleA = new Circle(p(-2, 2), 3);
		Circle circleB = new Circle(p(-6, 2), 1);
		
		List<Point2D> intersections = sortIntersections(circleA.intersections(circleB));
		assertEquals(1, intersections.size());
		
		assertEquals(-5,	intersections.get(0).getX(), DELTA);
		assertEquals( 2,	intersections.get(0).getY(), DELTA);
	}
	
	@Test
	public void testContains() {
		Circle circle = new Circle(p(-2, 2), 3);
		
		assertTrue(circle.contains(p(-5, 2)));
		assertTrue(circle.contains(p( 1, 2)));
		assertTrue(circle.contains(p(-2, 5)));
		assertTrue(circle.contains(p(-2,-1)));
		assertTrue(circle.contains(p(-2 + Math.sqrt(4.5), 2 + Math.sqrt(4.5))));
		
		assertFalse(circle.contains(p(-5.00001, 2)));
		assertFalse(circle.contains(p(-5, 1.99999)));
		assertFalse(circle.contains(p(0, 0)));
		assertFalse(circle.contains(p(-2, 2)));
		assertFalse(circle.contains(p(42e12, 1337e11)));
	}
	
	@Test
	public void testGetPointAtAngle() {
		Circle circle = new Circle(p(-2, 2), 5);
		
		Point2D p4end = circle.getPointAtAngle(0.6435011087933);
		assertEquals( 2,	p4end.getX(), DELTA);
		assertEquals( 5,	p4end.getY(), DELTA);
		
		Point2D p3end = circle.getPointAtAngle(0.9272952180016);
		assertEquals( 1,	p3end.getX(), DELTA);
		assertEquals( 6,	p3end.getY(), DELTA);
		
		Point2D p4opp = circle.getPointAtAngle(0.6435011087933 -  + Math.PI);
		assertEquals(-6,	p4opp.getX(), DELTA);
		assertEquals(-1,	p4opp.getY(), DELTA);
		
		Point2D p3opp = circle.getPointAtAngle(0.9272952180016 + Math.PI);
		assertEquals(-5,	p3opp.getX(), DELTA);
		assertEquals(-2,	p3opp.getY(), DELTA);
	}
	
	@Test
	public void testGetBoundingBox() {
		Circle circle = new Circle(p(-2, 2), 5);
		
		Box box = circle.getBoundingBox();
		assertEquals(-7, box.getFrom().getX(), DELTA);
		assertEquals(-3, box.getFrom().getY(), DELTA);
		assertEquals( 3, box.getTo().getX(), DELTA);
		assertEquals( 7, box.getTo().getY(), DELTA);
	}
	
}
