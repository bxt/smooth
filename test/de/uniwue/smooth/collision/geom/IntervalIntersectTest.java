package de.uniwue.smooth.collision.geom;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class IntervalIntersectTest {

	@Parameters(name = "{0} cap {1} = {2}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{Interval.getIntervalBetween( 2,  5), Interval.getIntervalBetween( 1,  4), Interval.getIntervalBetween( 2,  4)},
				{Interval.getIntervalBetween( 1,  5), Interval.getIntervalBetween( 2,  4), Interval.getIntervalBetween( 2,  4)},
				{Interval.getIntervalBetween( 1,  3), Interval.getIntervalBetween( 3,  4), Interval.getIntervalBetween( 3,  3)},
				{Interval.getIntervalBetween( 1,  2), Interval.getIntervalBetween( 3,  4), null},
				{Interval.getIntervalBetween(-2,  5), Interval.getIntervalBetween(-1,  4), Interval.getIntervalBetween(-1,  4)},
				{Interval.getIntervalBetween(-1,  5), Interval.getIntervalBetween(-2,  4), Interval.getIntervalBetween(-1,  4)},
				{Interval.getIntervalBetween(-1,  3), Interval.getIntervalBetween( 3,  4), Interval.getIntervalBetween( 3,  3)},
				{Interval.getIntervalBetween(-1, -2), Interval.getIntervalBetween(-3, -4), null},
				{Interval.getIntervalBetween(-1, -2), Interval.getIntervalBetween( 1,  2), null},
		});
	}
	
	@Parameter(0)
	public Interval intervalA;
	
	@Parameter(1)
	public Interval intervalB;
	
	@Parameter(2)
	public Interval expectedIntersectionInterval;
	
	@Test
	public void testIntersect() {
		Interval actualIntersectionInterval = intervalA.intersect(intervalB);
		IntervalTest.assertIntervalEquals(expectedIntersectionInterval, actualIntersectionInterval);
	}
	
	@Test
	public void testIntersectReversed() {
		Interval actualIntersectionInterval = intervalB.intersect(intervalA);
		IntervalTest.assertIntervalEquals(expectedIntersectionInterval, actualIntersectionInterval);
	}

}
