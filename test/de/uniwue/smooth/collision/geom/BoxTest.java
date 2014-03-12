package de.uniwue.smooth.collision.geom;

import static de.uniwue.smooth.collision.geom.TestUtils.p;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BoxTest {
	
	@Parameters(name = "{0}*x / {1}*y")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{1, 1}, {1, -1}, {-1, 1}, {-1, -1} 
		});
	}
	
	@Parameter(0)
	public double xFactor;
	
	@Parameter(1)
	public double yFactor;
	
	@Test
	public void testContains() {
		Box box = new Box(p(xFactor * 1, yFactor * 2), p(xFactor * 3, yFactor * 5));
		
		// Inside
		assertTrue(box.contains(p(xFactor * 2, yFactor * 3)));
		assertTrue(box.contains(p(xFactor * 2, yFactor * 4)));
		
		// Endpoints
		assertTrue(box.contains(p(xFactor * 1, yFactor * 2)));
		assertTrue(box.contains(p(xFactor * 3, yFactor * 5)));
		
		// Boundaries
		assertTrue(box.contains(p(xFactor * 1, yFactor * 3)));
		
		// Outside
		assertFalse(box.contains(p(xFactor * 0, yFactor * 1)));
		assertFalse(box.contains(p(xFactor * 4, yFactor * 4)));
		assertFalse(box.contains(p(xFactor * 2, yFactor * 6)));
		assertFalse(box.contains(p(xFactor * 4, yFactor * 6)));
	}
	
	@Test
	public void testIntersection() {
		Box boxA = new Box(p(xFactor * 1, yFactor * 1), p(xFactor * 5, yFactor * 3));
		Box boxB = new Box(p(xFactor * 2, yFactor * 0), p(xFactor * 4, yFactor * 2));
		
		Box boxIntersection = boxA.intersect(boxB);
		assertTrue(boxIntersection.contains(p(xFactor * 2, yFactor * 1)));
		assertTrue(boxIntersection.contains(p(xFactor * 4, yFactor * 2)));
		assertFalse(boxIntersection.contains(p(xFactor * 1, yFactor * 1)));
		
	}

	@Test
	public void testIntersects() {
		Box boxA = new Box(p(xFactor * 1, yFactor * 1), p(xFactor * 5, yFactor * 3));
		Box boxB = new Box(p(xFactor * 2, yFactor * 0), p(xFactor * 4, yFactor * 2));
		Box boxC = new Box(p(xFactor * 6, yFactor * 1), p(xFactor * 7, yFactor * 3));
		
		assertTrue(boxA.intersects(boxB));
		assertTrue(boxB.intersects(boxA));
		assertFalse(boxA.intersects(boxC));
		assertFalse(boxB.intersects(boxC));
		assertFalse(boxC.intersects(boxA));
		assertFalse(boxC.intersects(boxB));
		
	}

}
