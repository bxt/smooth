package de.uniwue.smooth.orthogonal;

import static org.junit.Assert.*;

import org.junit.Test;

public class QuadrantTest {

	@Test
	public void testGetQuadrantIntInt() {
		assertEquals(Quadrant.  I, Quadrant.getQuadrant( 1,  1));
		assertEquals(Quadrant. II, Quadrant.getQuadrant(-1,  1));
		assertEquals(Quadrant.III, Quadrant.getQuadrant(-1, -1));
		assertEquals(Quadrant. IV, Quadrant.getQuadrant( 1, -1));
	}

	@Test
	public void testGetQuadrantDoubleDouble() {
		assertEquals(Quadrant.  I, Quadrant.getQuadrant( 1.0,  1.0));
		assertEquals(Quadrant. II, Quadrant.getQuadrant(-1.0,  1.0));
		assertEquals(Quadrant.III, Quadrant.getQuadrant(-1.0, -1.0));
		assertEquals(Quadrant. IV, Quadrant.getQuadrant( 1.0, -1.0));
	}

	@Test
	public void testGetQuadrantBooleanBoolean() {
		assertEquals(Quadrant.  I, Quadrant.getQuadrant( true,  true));
		assertEquals(Quadrant. II, Quadrant.getQuadrant(false,  true));
		assertEquals(Quadrant.III, Quadrant.getQuadrant(false, false));
		assertEquals(Quadrant. IV, Quadrant.getQuadrant( true, false));
	}

	@Test
	public void testIsUpper() {
		assertEquals( true, Quadrant.  I.isUpper());
		assertEquals( true, Quadrant. II.isUpper());
		assertEquals(false, Quadrant.III.isUpper());
		assertEquals(false, Quadrant. IV.isUpper());
	}

	@Test
	public void testIsRight() {
		assertEquals( true, Quadrant.  I.isRight());
		assertEquals(false, Quadrant. II.isRight());
		assertEquals(false, Quadrant.III.isRight());
		assertEquals( true, Quadrant. IV.isRight());
	}

	@Test
	public void testGetNext() {
		assertEquals(Quadrant. IV, Quadrant.  I.getNext());
		assertEquals(Quadrant.  I, Quadrant. II.getNext());
		assertEquals(Quadrant. II, Quadrant.III.getNext());
		assertEquals(Quadrant.III, Quadrant. IV.getNext());
	}

	@Test
	public void testGetPrev() {
		assertEquals(Quadrant. II, Quadrant.  I.getPrev());
		assertEquals(Quadrant.III, Quadrant. II.getPrev());
		assertEquals(Quadrant. IV, Quadrant.III.getPrev());
		assertEquals(Quadrant.  I, Quadrant. IV.getPrev());
	}

	@Test
	public void testGetOpposite() {
		assertEquals(Quadrant.III, Quadrant.  I.getOpposite());
		assertEquals(Quadrant. IV, Quadrant. II.getOpposite());
		assertEquals(Quadrant.  I, Quadrant.III.getOpposite());
		assertEquals(Quadrant. II, Quadrant. IV.getOpposite());
	}

	@Test
	public void testIsDiagonal() {
		assertEquals( true, Quadrant.  I.isDiagonal());
		assertEquals(false, Quadrant. II.isDiagonal());
		assertEquals( true, Quadrant.III.isDiagonal());
		assertEquals(false, Quadrant. IV.isDiagonal());
	}

	@Test
	public void testGetHorizontalPort() {
		assertEquals(Port.R, Quadrant.  I.getHorizontalPort());
		assertEquals(Port.L, Quadrant. II.getHorizontalPort());
		assertEquals(Port.L, Quadrant.III.getHorizontalPort());
		assertEquals(Port.R, Quadrant. IV.getHorizontalPort());
	}

	@Test
	public void testGetVerticalPort() {
		assertEquals(Port.T, Quadrant.  I.getVerticalPort());
		assertEquals(Port.T, Quadrant. II.getVerticalPort());
		assertEquals(Port.B, Quadrant.III.getVerticalPort());
		assertEquals(Port.B, Quadrant. IV.getVerticalPort());
	}

	@Test
	public void testGetVerticalOpposite() {
		assertEquals(Quadrant. IV, Quadrant.  I.getVerticalOpposite());
		assertEquals(Quadrant.III, Quadrant. II.getVerticalOpposite());
		assertEquals(Quadrant. II, Quadrant.III.getVerticalOpposite());
		assertEquals(Quadrant.  I, Quadrant. IV.getVerticalOpposite());
	}

	@Test
	public void testGetHorizontalOpposite() {
		assertEquals(Quadrant. II, Quadrant.  I.getHorizontalOpposite());
		assertEquals(Quadrant.  I, Quadrant. II.getHorizontalOpposite());
		assertEquals(Quadrant. IV, Quadrant.III.getHorizontalOpposite());
		assertEquals(Quadrant.III, Quadrant. IV.getHorizontalOpposite());
	}

}
