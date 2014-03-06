package de.uniwue.smooth.collison;

import static de.uniwue.smooth.collison.TestUtils.p;
import static org.junit.Assert.*;

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

}
