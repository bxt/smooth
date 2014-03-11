package de.uniwue.smooth.collison;

import static de.uniwue.smooth.collison.TestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class SectorTest {

	@Test
	public void testContainsA() {
		Sector sector = Sector.getSectorBetween(p(0, 1), p(-1, 0));
		
		assertTrue(sector.contains(p(-1,  1)));
		
		// distance doesn't matter
		assertTrue(sector.contains(p(-2,  2)));
		
		// borders exclusive
		assertFalse(sector.contains(p( 0,  1)));
		assertFalse(sector.contains(p(-1,  0)));
		
		assertFalse(sector.contains(p( 1,  1)));
		assertFalse(sector.contains(p(-1, -1)));
		assertFalse(sector.contains(p( 1, -1)));
	}

	@Test
	public void testContainsB() {
		Sector sector = Sector.getSectorBetween(p(-1, 0), p(0, 1));
		
		assertTrue(sector.contains(p( 1,  1)));
		assertTrue(sector.contains(p(-1, -1)));
		assertTrue(sector.contains(p( 1, -1)));
		
		// distance doesn't matter
		assertTrue(sector.contains(p( 2,  2)));
		
		// borders exclusive
		assertFalse(sector.contains(p( 0,  1)));
		assertFalse(sector.contains(p(-1,  0)));
		
		assertFalse(sector.contains(p(-1,  1)));
		assertFalse(sector.contains(p(-2,  2)));
	}

	@Test
	public void testContainsC() {
		Sector sector = Sector.getSectorBetween(p(1, 1), p(1, -1));
		
		assertTrue(sector.contains(p( 0,  1)));
		assertTrue(sector.contains(p( 0,  2)));
		assertTrue(sector.contains(p( 0, -1)));
		assertTrue(sector.contains(p( 0, -2)));
		assertTrue(sector.contains(p( 1,  2)));
		assertTrue(sector.contains(p( 1, -2)));
		
		assertFalse(sector.contains(p( 1,  0)));
		assertFalse(sector.contains(p( 2,  0)));
		assertFalse(sector.contains(p( 2,  1)));
		assertFalse(sector.contains(p( 2, -1)));
		
		assertFalse(sector.contains(p( 1,  1)));
		assertTrue(sector.contains(p(-1, -1)));
		assertFalse(sector.contains(p( 1, -1)));		
		assertTrue(sector.contains(p(-1,  1)));
	}

	@Test
	public void testContainsD() {
		Sector sector = Sector.getSectorBetween(p(1, -1), p(1, 1));
		
		assertFalse(sector.contains(p( 0,  1)));
		assertFalse(sector.contains(p( 0,  2)));
		assertFalse(sector.contains(p( 0, -1)));
		assertFalse(sector.contains(p( 0, -2)));
		assertFalse(sector.contains(p( 1,  2)));
		assertFalse(sector.contains(p( 1, -2)));
		
		assertTrue(sector.contains(p( 1,  0)));
		assertTrue(sector.contains(p( 2,  0)));
		assertTrue(sector.contains(p( 2,  1)));
		assertTrue(sector.contains(p( 2, -1)));
		
		assertFalse(sector.contains(p( 1,  1)));
		assertFalse(sector.contains(p(-1, -1)));
		assertFalse(sector.contains(p( 1, -1)));
		assertFalse(sector.contains(p(-1,  1)));
	}

	@Test
	public void testContainsE() {
		Sector sector = Sector.getSectorBetween(p(0, 1), p(0, -1));
		
		assertFalse(sector.contains(p( 1,  1)));
		assertTrue(sector.contains(p(-1, -1)));
		assertFalse(sector.contains(p( 1, -1)));
		assertTrue(sector.contains(p(-1,  1)));
	}

	@Test
	public void testContainsF() {
		Sector sector = Sector.getSectorBetween(p(1, 1), p(-1, -1));
		
		assertFalse(sector.contains(p( 1,  1)));
		assertFalse(sector.contains(p(-1, -1)));
		assertFalse(sector.contains(p( 1, -1)));
		assertTrue(sector.contains(p(-1,  1)));
	}

	@Test
	public void testContainsG() {
		Sector sector = Sector.getSectorBetween(p(-1, -1), p(1, 1));
		
		assertFalse(sector.contains(p( 1,  1)));
		assertFalse(sector.contains(p(-1, -1)));
		assertTrue(sector.contains(p( 1, -1)));
		assertFalse(sector.contains(p(-1,  1)));
	}
	
}
