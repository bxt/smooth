package de.uniwue.smooth.planar;

import static org.junit.Assert.*;

import org.junit.Test;

import de.uniwue.smooth.app.Generators;

public class BrandesEmbeddingTest {

	@Test
	public void testIsPlanar() {
		assertFalse(BrandesEmbedding.isPlanar(Generators.matching(3))); // K_{3,3}
		assertFalse(BrandesEmbedding.isPlanar(Generators.k5())); // K_5
		
		assertFalse(BrandesEmbedding.isPlanar(Generators.matching(99))); // K_{99,99}
		assertFalse(BrandesEmbedding.isPlanar(Generators.completeGraph(99))); // K_99

		assertTrue(BrandesEmbedding.isPlanar(Generators.circle(99)));
		assertTrue(BrandesEmbedding.isPlanar(Generators.hexahedron()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.octahedron()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.path(99)));
		assertTrue(BrandesEmbedding.isPlanar(Generators.simplePlanarGraph()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.tetrahedron()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.triangle()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.wheel(99)));
}
	
	
}
