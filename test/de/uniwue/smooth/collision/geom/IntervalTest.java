package de.uniwue.smooth.collision.geom;

import static de.uniwue.smooth.collision.geom.TestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class IntervalTest {

	@Test
	public void testContainsA() {
		Interval interval = Interval.getIntervalBetween(2, 4);
		
		assertTrue(interval.contains(3));
		
		// borders inclusive
		assertTrue(interval.contains(2));
		assertTrue(interval.contains(4));
		
		assertFalse(interval.contains(1));
		assertFalse(interval.contains(5));
		assertFalse(interval.contains(-1));
	}
	
	@Test
	public void testContainsB() {
		Interval interval = Interval.getIntervalBetween(4, 2);
		
		assertTrue(interval.contains(3));
		
		// borders inclusive
		assertTrue(interval.contains(2));
		assertTrue(interval.contains(4));
		
		assertFalse(interval.contains(1));
		assertFalse(interval.contains(5));
		assertFalse(interval.contains(-1));
	}
	
	@Test
	public void testContainsC() {
		Interval interval = Interval.getIntervalBetween(2, -2);
		
		assertTrue(interval.contains(0));
		
		// borders inclusive
		assertTrue(interval.contains(2));
		assertTrue(interval.contains(-2));
		
		assertFalse(interval.contains(3));
		assertFalse(interval.contains(-3));
	}
	
	@Test
	public void testContainsD() {
		Interval interval = Interval.getIntervalFrom(2, 2);
		
		assertTrue(interval.contains(3));
		
		// borders inclusive
		assertTrue(interval.contains(2));
		assertTrue(interval.contains(4));
		
		assertFalse(interval.contains(1));
		assertFalse(interval.contains(5));
		assertFalse(interval.contains(-1));
	}
	
	@Test
	public void testContainsE() {
		Interval interval = Interval.getIntervalFrom(2, -4);
		
		assertTrue(interval.contains(0));
		
		// borders inclusive
		assertTrue(interval.contains(2));
		assertTrue(interval.contains(-2));
		
		assertFalse(interval.contains(3));
		assertFalse(interval.contains(-3));
	}
	
	@Test
	public void testMerge() {
		Interval intervalA = Interval.getIntervalFrom(2, -5);
		Interval intervalB = Interval.getIntervalFrom(3, 6);
		
		assertIntervalEquals(Interval.getIntervalBetween(-3, 9), intervalA.merge(intervalB));
		assertIntervalEquals(Interval.getIntervalBetween(-3, 9), intervalB.merge(intervalA));
		
	}
	
	public static void assertIntervalEquals(Interval expected, Interval actual) {
		assertTrue((actual == null) == (expected == null));
		if(expected != null) {
			assertEquals(expected.toString(), actual.toString());
			assertEquals(expected.getFrom(), actual.getFrom(), DELTA);
			assertEquals(expected.getTo(), actual.getTo(), DELTA);
		}
	}

}
