package de.uniwue.smooth.draw;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Draws the primitives of orthogonal layouts using Ipe.
 */
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
	public void edgeMidpoint(Pair<Integer> position, String color) {
		draw(IpeDraw.drawIpeMark(position.getFirst(), position.getSecond(), "cross", color));
	}
	
	@Override
	public void line(Pair<Integer> from, Pair<Integer> to) {
		draw(IpeDraw.drawIpeEdge(from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond()));
	}
	
	@Override
	public void line(Pair<Pair<Integer>> endpoints) {
		line(endpoints.getFirst(), endpoints.getSecond());
	}

	@Override
	public void label(Pair<Integer> position, String labelText) {
		draw(IpeDraw.writeIpeText(labelText, position.getFirst(), position.getSecond()));
	}

	@Override
	public void arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		draw(IpeDraw.drawIpeCircularArc(mid.getFirst(), mid.getSecond(), from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond()));
	}
	@Override
	public void arc(Pair<Integer> from, Pair<Integer> to) {
		draw(IpeDraw.drawIpeSemiCircle(from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond()));
	}

}
