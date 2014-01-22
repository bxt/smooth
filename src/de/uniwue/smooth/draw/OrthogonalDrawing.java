package de.uniwue.smooth.draw;

import edu.uci.ics.jung.graph.util.Pair;

public interface OrthogonalDrawing<T> {

	public abstract void vertex(Pair<Integer> position);

	public abstract void edgeMidpoint(Pair<Integer> position);

	public abstract void line(Pair<Integer> from, Pair<Integer> to);

	public abstract void line(Pair<Pair<Integer>> endpoints);
	
	public abstract T create();

}