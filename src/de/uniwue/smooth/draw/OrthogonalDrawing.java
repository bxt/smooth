package de.uniwue.smooth.draw;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Allows to draw some primitives needed for visualizing orthogonal layouts.
 * 
 * @param <T> Type of the resulting (low level) painting.
 */
public interface OrthogonalDrawing<T> extends Factory<T> {
	
	/**
	 * Draws a vertex marker.
	 * @param position Left/top coordinates of the vertex.
	 */
	public abstract void vertex(Pair<Integer> position);

	/**
	 * Draws an edge marker.
	 * @param position Left/top coordinates of the edge.
	 */
	public abstract void edgeMidpoint(Pair<Integer> position);

	/**
	 * Draws an edge marker.
	 * @param position Left/top coordinates of the edge.
	 */
	public abstract void edgeMidpoint(Pair<Integer> position, String color);

	/**
	 * Draws a straight line.
	 * @param from Left/top coordinates of the origin of the line.
	 * @param to Left/top coordinates of the endpoint of the line.
	 */
	public abstract void line(Pair<Integer> from, Pair<Integer> to);

	/**
	 * Draws a straight line.
	 * @param endpoints Pair of Left/top coordinates of the endpoints of the line.
	 */
	public abstract void line(Pair<Pair<Integer>> endpoints);
	
	/**
	 * Draws a label text.
	 * @param position Left/top coordinates of the label.
	 * @param labelText Text to write at the position.
	 */
	public abstract void label(Pair<Integer> position, String labelText);

	/**
	 * Finish the painting. (optional)
	 * 
	 * @return The completed painting.
	 * @throws UnsupportedOperationException When this drawing does not have a defined result (stateful drawing).
	 */
	public abstract T create();

	/**
	 * Draws a circular arc.
	 * @param from Left/top coordinates of the origin of the arc.
	 * @param mid Left/top coordinates of the midpoint of the arc.
	 * @param to Left/top coordinates of the endpoint of the arc.
	 */
	public abstract void arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to);
	
	/**
	 * Draws a circular arc.
	 * @param from Left/top coordinates of the origin of the arc.
	 * @param to Left/top coordinates of the endpoint of the arc.
	 */
	public abstract void arc(Pair<Integer> from, Pair<Integer> to);
	
	// Modified versions for visualizing collisions:
	
	public abstract void line(Pair<Integer> from, Pair<Integer> to, boolean collidesLine);
	public abstract void line(Pair<Pair<Integer>> endpoints, boolean collidesLine);
	public abstract void arc(Pair<Integer> from, Pair<Integer> to, boolean collidesArc);
	public abstract void arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to, boolean collidesArc);
	
	/**
	 * Continue the drawing on a new page.
	 */
	public abstract void newPage();

}