package de.uniwue.smooth.collison;

import static de.uniwue.smooth.collison.TestUtils.p;
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
		
		assertTrue(box.contains(p(xFactor * 2, yFactor * 3)));
		assertTrue(box.contains(p(xFactor * 2, yFactor * 4)));
		
		assertFalse(box.contains(p(xFactor * 1, yFactor * 2)));
		assertFalse(box.contains(p(xFactor * 3, yFactor * 5)));
		assertFalse(box.contains(p(xFactor * 1, yFactor * 3)));
	}

}
