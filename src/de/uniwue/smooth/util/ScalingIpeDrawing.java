package de.uniwue.smooth.util;

import edu.uci.ics.jung.graph.util.Pair;

public class ScalingIpeDrawing extends AbstractIpeDrawing {

	private Pair<Integer> scale;

	public ScalingIpeDrawing(Pair<Integer> scale) {
		super();
		this.scale = scale;
	}
	
	protected Pair<Integer> scale(Pair<Integer> input) {
		return new Pair<>(input.getFirst() * scale.getFirst(), input.getSecond() * scale.getSecond());
	}

}