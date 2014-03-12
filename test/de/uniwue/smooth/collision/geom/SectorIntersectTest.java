package de.uniwue.smooth.collision.geom;

import static de.uniwue.smooth.collision.geom.TestUtils.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.uniwue.smooth.collision.geom.Sector;

@RunWith(Parameterized.class)
public class SectorIntersectTest {

	@Parameters(name = "{0} cap {1} = {2}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{Sector.getSectorBetween(1, 3), Sector.getSectorBetween(2, 4), Sector.getSectorBetween(2, 3)},
				{Sector.getSectorBetween(2, 3), Sector.getSectorBetween(1, 4), Sector.getSectorBetween(2, 3)},
				{Sector.getSectorBetween(1, 2), Sector.getSectorBetween(3, 4), null},
				{Sector.getSectorBetween(1, 2), Sector.getSectorBetween(2, 4), null},
				{Sector.getSectorBetween(6, 3), Sector.getSectorBetween(2, 4), Sector.getSectorBetween(2, 3)},
				{Sector.getSectorBetween(2, 3), Sector.getSectorBetween(6, 4), Sector.getSectorBetween(2, 3)},
				{Sector.getSectorBetween(6, 2), Sector.getSectorBetween(3, 4), null},
				{Sector.getSectorBetween(1, 4), Sector.getSectorBetween(4, 1), null},
				{Sector.getSectorBetween(4, 6), Sector.getSectorBetween(5, 1), Sector.getSectorBetween(5, 6)},
				{Sector.getSectorBetween(5, 6), Sector.getSectorBetween(4, 1), Sector.getSectorBetween(5, 6)},
				{Sector.getSectorBetween(5, 3), Sector.getSectorBetween(6, 4), Sector.getSectorBetween(6, 3)},
				{Sector.getSectorBetween(5, 4), Sector.getSectorBetween(6, 3), Sector.getSectorBetween(6, 3)},
		});
	}
	
	@Parameter(0)
	public Sector sectorA;
	
	@Parameter(1)
	public Sector sectorB;
	
	@Parameter(2)
	public Sector expectedIntersectionSector;
	
	@Test
	public void testIntersect() {
		Sector actualIntersectionSector = sectorA.intersect(sectorB);
		assertSectorEquals(expectedIntersectionSector, actualIntersectionSector);
	}
	
	@Test
	public void testIntersectReversed() {
		Sector actualIntersectionSector = sectorB.intersect(sectorA);
		assertSectorEquals(expectedIntersectionSector, actualIntersectionSector);
	}

	private static void assertSectorEquals(Sector expected, Sector actual) {
		assertTrue((actual == null) == (expected == null));
		if(expected != null) {
			assertEquals(expected.toString(), actual.toString());
			assertEquals(expected.getFrom(), actual.getFrom(), DELTA);
			assertEquals(expected.getTo(), actual.getTo(), DELTA);
		}
	}

}
