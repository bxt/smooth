package de.uniwue.smooth.collison;

import static de.uniwue.smooth.collison.TestUtils.*;
import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.List;

import org.junit.Test;

public class CircleArcTest {
	
	@Test
	public void testContainsA() {
		CircleArc circleArc = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(1, 7));
		
		assertFalse(circleArc.contains(p( 6,  2)));
		assertFalse(circleArc.contains(p( 1,  2)));
		assertFalse(circleArc.contains(p( 1,  7)));
		
		assertTrue(circleArc.contains(p( 5,  5)));
		assertTrue(circleArc.contains(p( 4,  6)));
		assertFalse(circleArc.contains(p( 5, -1)));
		assertFalse(circleArc.contains(p( 4, -2)));
		assertFalse(circleArc.contains(p(-3, -5)));
		assertFalse(circleArc.contains(p(-3, -1)));
	}

	@Test
	public void testContainsB() {
		CircleArc circleArc = CircleArc.getCircleArc(p(1, 7), p(1, 2), p(6, 2));
		
		assertFalse(circleArc.contains(p( 6,  2)));
		assertFalse(circleArc.contains(p( 1,  2)));
		assertFalse(circleArc.contains(p( 1,  7)));
		
		assertFalse(circleArc.contains(p( 5,  5)));
		assertFalse(circleArc.contains(p( 4,  6)));
		assertTrue(circleArc.contains(p( 5, -1)));
		assertTrue(circleArc.contains(p( 4, -2)));
		assertTrue(circleArc.contains(p(-3,  5)));
		assertTrue(circleArc.contains(p(-3, -1)));
	}

	@Test
	public void testIntersectionsLineOne() {
		CircleArc circleArc = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(1, 7));
		LineSegment lineSegment = new LineSegment(p(4, 5), p(6, 5));
		
		List<Point2D> intersections = sortIntersections(circleArc.intersections(lineSegment));
		assertEquals(1, intersections.size());
		
		assertEquals(5,	intersections.get(0).getX(), DELTA);
		assertEquals(5,	intersections.get(0).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsLineOneCicleBigEnough() {
		CircleArc circleArc = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(-4, 2));
		LineSegment lineSegment = new LineSegment(p(4, 5), p(6, 5));
		
		List<Point2D> intersections = sortIntersections(circleArc.intersections(lineSegment));
		assertEquals(1, intersections.size());
		
		assertEquals(5,	intersections.get(0).getX(), DELTA);
		assertEquals(5,	intersections.get(0).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsLineOneLineBigEnough() {
		CircleArc circleArc = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(1, 7));
		LineSegment lineSegment = new LineSegment(p(-4, 5), p(6, 5));
		
		List<Point2D> intersections = sortIntersections(circleArc.intersections(lineSegment));
		assertEquals(1, intersections.size());
		
		assertEquals(5,	intersections.get(0).getX(), DELTA);
		assertEquals(5,	intersections.get(0).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsLineTwoA() {
		CircleArc circleArc = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(-4, 2));
		LineSegment lineSegment = new LineSegment(p(-4, 5), p(6, 5));
		
		List<Point2D> intersections = sortIntersections(circleArc.intersections(lineSegment));
		assertEquals(2, intersections.size());
		
		assertEquals(-3,	intersections.get(0).getX(), DELTA);
		assertEquals( 5,	intersections.get(0).getY(), DELTA);
		
		assertEquals( 5,	intersections.get(1).getX(), DELTA);
		assertEquals( 5,	intersections.get(1).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsLineTwoB() {
		CircleArc circleArc = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(1, 7));
		LineSegment lineSegment = new LineSegment(p(3, 7), p(6, 4));
		
		List<Point2D> intersections = sortIntersections(circleArc.intersections(lineSegment));
		assertEquals(2, intersections.size());
		
		assertEquals(4,	intersections.get(0).getX(), DELTA);
		assertEquals(6,	intersections.get(0).getY(), DELTA);
		
		assertEquals(5,	intersections.get(1).getX(), DELTA);
		assertEquals(5,	intersections.get(1).getY(), DELTA);
	}
	
	@Test
	public void testNoIntersectionsLineTangentiallyConnected() {
		CircleArc circleArc = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(1, 7));
		LineSegment lineSegment = new LineSegment(p(6, -2), p(6, 2));
		
		List<Point2D> intersections = sortIntersections(circleArc.intersections(lineSegment));
		assertEquals(0, intersections.size());
	}
	
	@Test
	public void testIntersectionsCircleOneA() {
		CircleArc circleArcA = CircleArc.getCircleArc(p( 1, -3), p( 1,  2), p( 1,  7));
		CircleArc circleArcB = CircleArc.getCircleArc(p( 9,  7), p( 9,  2), p( 4,  2));
		
		List<Point2D> intersections = sortIntersections(circleArcA.intersections(circleArcB));
		assertEquals(1, intersections.size());
		
		assertEquals(5,	intersections.get(0).getX(), DELTA);
		assertEquals(5,	intersections.get(0).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsCircleOneB() {
		CircleArc circleArcA = CircleArc.getCircleArc(p( 1, -3), p( 1,  2), p( 1,  7));
		CircleArc circleArcB = CircleArc.getCircleArc(p( 4,  2), p( 9,  2), p( 9,  7));
		
		List<Point2D> intersections = sortIntersections(circleArcA.intersections(circleArcB));
		assertEquals(1, intersections.size());
		
		assertEquals( 5,	intersections.get(0).getX(), DELTA);
		assertEquals(-1,	intersections.get(0).getY(), DELTA);
	}
	
	@Test
	public void testIntersectionsCircleTwo() {
		CircleArc circleArcA = CircleArc.getCircleArc(p( 1, -3), p( 1,  2), p( 1,  7));
		CircleArc circleArcB = CircleArc.getCircleArc(p( 9,  7), p( 9, 2 ), p( 9, -3));
		
		List<Point2D> intersections = sortIntersections(circleArcA.intersections(circleArcB));
		assertEquals(2, intersections.size());
		
		assertEquals( 5,	intersections.get(0).getX(), DELTA);
		assertEquals(-1,	intersections.get(0).getY(), DELTA);
		
		assertEquals( 5,	intersections.get(1).getX(), DELTA);
		assertEquals( 5,	intersections.get(1).getY(), DELTA);
	}
	
	@Test
	public void testNoIntersectionsCircleDifferentRadii() {
		CircleArc circleArcA = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(1, 7));
		CircleArc circleArcB = CircleArc.getCircleArc(p(7, 2), p(1, 2), p(1, 8));
		
		List<Point2D> intersections = sortIntersections(circleArcA.intersections(circleArcB));
		assertEquals(0, intersections.size());
	}
	
	@Test
	public void testNoIntersectionsCircleTangentiallyConnected() {
		CircleArc circleArcA = CircleArc.getCircleArc(p(4, 6), p(1, 2), p(1, 7));
		CircleArc circleArcB = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(4, 6));
		
		List<Point2D> intersections = sortIntersections(circleArcA.intersections(circleArcB));
		assertEquals(0, intersections.size());
	}
	
	@Test
	public void testNoIntersectionsCircleNoArcOverlap() {
		CircleArc circleArcA = CircleArc.getCircleArc(p(4, 6), p(1, 2), p(1, 7));
		CircleArc circleArcB = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(5, 5));
		
		List<Point2D> intersections = sortIntersections(circleArcA.intersections(circleArcB));
		assertEquals(0, intersections.size());
	}
	
	@Test
	public void testNoIntersectionsCircleArcOverlap() {
		CircleArc circleArcA = CircleArc.getCircleArc(p(5, 5), p(1, 2), p(1, 7));
		CircleArc circleArcB = CircleArc.getCircleArc(p(6, 2), p(1, 2), p(4, 6));
		
		assertNull(circleArcA.intersections(circleArcB));
	}
	
}
