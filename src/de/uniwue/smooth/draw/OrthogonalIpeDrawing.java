package de.uniwue.smooth.draw;

import edu.uci.ics.jung.graph.util.Pair;

public class OrthogonalIpeDrawing extends AbstractIpeDrawing implements OrthogonalDrawing<String> {
	
	@Override
	public void vertex(Pair<Integer> position) {
		draw(IpeDraw.drawIpeMark(position.getFirst(), position.getSecond()));
	}
	
	@Override
	public void edgeMidpoint(Pair<Integer> position) {
		draw(IpeDraw.drawIpeMark(position.getFirst(), position.getSecond(), "cross", "gray"));
	}
	
	@Override
	public void line(Pair<Integer> from, Pair<Integer> to) {
		draw(IpeDraw.drawIpeEdge(from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond()));
	}
	
	@Override
	public void line(Pair<Pair<Integer>> endpoints) {
		line(endpoints.getFirst(), endpoints.getSecond());
	}
}
