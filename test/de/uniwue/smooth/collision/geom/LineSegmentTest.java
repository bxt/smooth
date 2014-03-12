package de.uniwue.smooth.collision.geom;

import static de.uniwue.smooth.collision.geom.TestUtils.*;
import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.List;

import org.junit.Test;

public class LineSegmentTest {

	@Test
	public void testIntersectionsRightThrough() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(3,1), p(1,3));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(1, intersections.size());
		
		assertEquals(2,	intersections.get(0).getX(), DELTA);
		assertEquals(2,	intersections.get(0).getY(), DELTA);
	}
	@Test
	public void testNoIntersectionsOrthogonal() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(3,1), p(5,-1));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(0, intersections.size());
	}

	@Test
	public void testIntersectionsEndpointParallel() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(3,3), p(6,6));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(1, intersections.size());
		
		assertEquals(3,	intersections.get(0).getX(), DELTA);
		assertEquals(3,	intersections.get(0).getY(), DELTA);
	}

	@Test
	public void testIntersectionsEndpointIntersectionNotParallel() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(1,5), p(3,3));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(1, intersections.size());
		
		assertEquals(3,	intersections.get(0).getX(), DELTA);
		assertEquals(3,	intersections.get(0).getY(), DELTA);
	}

	@Test
	public void testIntersectionsEndpointIntersectionNearlyParallel() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(2,1), p(3,3));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(1, intersections.size());
		
		assertEquals(3,	intersections.get(0).getX(), DELTA);
		assertEquals(3,	intersections.get(0).getY(), DELTA);
	}

	@Test
	public void testIntersectionsEnpointTangent() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(1,3), p(2,2));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(1, intersections.size());
		
		assertEquals(2,	intersections.get(0).getX(), DELTA);
		assertEquals(2,	intersections.get(0).getY(), DELTA);
	}


	@Test
	public void testNoIntersectionsParallel() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(2,1), p(4,3));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(0, intersections.size());
	}

	@Test
	public void testNoIntersectionsInRow() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(4,4), p(6,6));
		
		List<Point2D> intersections = sortIntersections(lineSegmentA.intersections(lineSegmentB));
		assertEquals(0, intersections.size());
	}

	@Test
	public void testIntersectionsOverlap() {
		LineSegment lineSegmentA = new LineSegment(p(1,1), p(3,3));
		LineSegment lineSegmentB = new LineSegment(p(2,2), p(6,6));
		
		assertNull(lineSegmentA.intersections(lineSegmentB));
	}

	@Test
	public void testContains() {
		LineSegment lineSegment = new LineSegment(p(1,1), p(3,3));
		
		assertTrue(lineSegment.contains(p(2, 2)));
		
		// Endpoints inclusive
		assertTrue(lineSegment.contains(p(1, 1)));
		assertTrue(lineSegment.contains(p(3, 3)));
		
		assertFalse(lineSegment.contains(p(0, 0)));
		assertFalse(lineSegment.contains(p(4, 4)));
		assertFalse(lineSegment.contains(p(-2, -2)));
		assertFalse(lineSegment.contains(p(3, 2)));
	}

}
