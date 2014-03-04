package de.uniwue.smooth.collison;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import org.junit.Test;

public class LineTest {

	@Test
	public void testLinePoint2DPoint2D() {
		Line line = new Line(p(0, 0), p(1,2));
		assertEquals(p(0, 0), line.getFrom());
		assertEquals(p(1, 2), line.getTo());
	}

	@Test
	public void testLineDoubleDouble() {
		Line line = new Line(2, 0);
		assertEquals(p(0, 0), line.getFrom());
		assertEquals(p(1, 2), line.getTo());
	}

	@Test
	public void testLineDoubleDoubleDouble() {
		Line line = new Line(-2, 1, 0);
		assertEquals(p(0, 0), line.getFrom());
		assertEquals(p(1, 2), line.getTo());
	}

	@Test
	public void testLineDoubleDoubleDoubleVertical() {
		Line line = new Line(2, 0, 4);
		assertEquals(p(2, 0), line.getFrom());
		assertEquals(p(2, 1), line.getTo());
	}

	@Test
	public void testLineDoubleDoubleDoubleHorizontal() {
		Line line = new Line(0, 2, 4);
		assertEquals(p(0, 2), line.getFrom());
		assertEquals(p(1, 2), line.getTo());
	}

	@Test
	public void testContains() {
		Line line = new Line(p(0, 0), p(1,2));
		
		assertTrue(line.contains(p(0,0)));
		assertTrue(line.contains(p(1,2)));
		assertTrue(line.contains(p(2,4)));
		assertTrue(line.contains(p(-0.5,-1)));
		
		assertFalse(line.contains(p(1, 0)));
		assertFalse(line.contains(p(-1, 0)));
		assertFalse(line.contains(p(0, 1)));
		assertFalse(line.contains(p(0, -1)));
		assertFalse(line.contains(p(1, 1)));
		assertFalse(line.contains(p(1, -1)));
		assertFalse(line.contains(p(-1, 1)));
		assertFalse(line.contains(p(-1, -1)));
		assertFalse(line.contains(p(0.3, 0.3)));
	}

	@Test
	public void testContainsHorizontal() {
		Line line = new Line(p(0, 0), p(0,1));
		assertTrue(line.contains(p(0,0)));
		assertTrue(line.contains(p(0,1)));
		assertTrue(line.contains(p(0,2)));
		assertTrue(line.contains(p(0,-0.5)));
		
		assertFalse(line.contains(p(1, 0)));
		assertFalse(line.contains(p(-1, 0)));
		assertFalse(line.contains(p(1, 1)));
		assertFalse(line.contains(p(1, -1)));
		assertFalse(line.contains(p(-1, 1)));
		assertFalse(line.contains(p(-1, -1)));
		assertFalse(line.contains(p(0.3, 0.3)));
	}

	@Test
	public void testContainsVertical() {
		Line line = new Line(p(0, 0), p(1,0));
		assertTrue(line.contains(p(0,0)));
		assertTrue(line.contains(p(1,0)));
		assertTrue(line.contains(p(2,0)));
		assertTrue(line.contains(p(-0.5,0)));
		
		assertFalse(line.contains(p(0, 1)));
		assertFalse(line.contains(p(0, -1)));
		assertFalse(line.contains(p(1, 1)));
		assertFalse(line.contains(p(1, -1)));
		assertFalse(line.contains(p(-1, 1)));
		assertFalse(line.contains(p(-1, -1)));
		assertFalse(line.contains(p(0.3, 0.3)));
	}

	private static Point2D p(double x, double y) {
		return new Point2D.Double(x, y);
	}
	
}
