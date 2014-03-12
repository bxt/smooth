package de.uniwue.smooth.collision.geom;

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
}
