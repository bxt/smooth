package de.uniwue.smooth.util;

import edu.uci.ics.jung.graph.util.Pair;

public class OrthogonalIpeDrawing extends ScalingIpeDrawing {
	
	public OrthogonalIpeDrawing(Pair<Integer> scale) {
		super(scale);
	}

	public void vertex(Pair<Integer> position) {
		Pair<Integer> positionScaled = scale(position);
		draw(IpeDraw.drawIpeMark(positionScaled.getFirst(), positionScaled.getSecond()));
	}
	
	public void edgeMidpoint(Pair<Integer> position) {
		Pair<Integer> positionScaled = scale(position);
		draw(IpeDraw.drawIpeMark(positionScaled.getFirst(), positionScaled.getSecond(), "cross"));
	}
	
	public void line(Pair<Integer> from, Pair<Integer> to) {
		Pair<Integer> fromScaled = scale(from);
		Pair<Integer> toScaled = scale(to);
		draw(IpeDraw.drawIpeEdge(fromScaled.getFirst(), fromScaled.getSecond(), toScaled.getFirst(), toScaled.getSecond()));
	}
	
	public void line(Pair<Pair<Integer>> endpoints) {
		line(endpoints.getFirst(), endpoints.getSecond());
	}
}
