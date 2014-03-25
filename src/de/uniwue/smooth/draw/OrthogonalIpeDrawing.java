package de.uniwue.smooth.draw;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Draws the primitives of orthogonal layouts using Ipe.
 */
public class OrthogonalIpeDrawing implements OrthogonalDrawing<Appendable> {
	
	IpeDrawing ipeDrawing;
	
	public OrthogonalIpeDrawing(IpeDrawing ipeDrawing) {
		super();
		this.ipeDrawing = ipeDrawing;
	}

	@Override
	public void vertex(Pair<Integer> position) {
		ipeDrawing.draw(IpeDraw.drawIpeMark(position.getFirst(), position.getSecond()));
	}
	
	@Override
	public void edgeMidpoint(Pair<Integer> position) {
		edgeMidpoint(position, "gray");
	}
	
	@Override
	public void edgeMidpoint(Pair<Integer> position, String color) {
		ipeDrawing.draw(IpeDraw.drawIpeMark(position.getFirst(), position.getSecond(), "cross", color));
	}
	
	@Override
	public void line(Pair<Integer> from, Pair<Integer> to) {
		ipeDrawing.draw(IpeDraw.drawIpeEdge(from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond()));
	}
	
	@Override
	public void line(Pair<Pair<Integer>> endpoints) {
		line(endpoints.getFirst(), endpoints.getSecond());
	}

	@Override
	public void label(Pair<Integer> position, String labelText) {
		ipeDrawing.draw(IpeDraw.writeIpeText(labelText, position.getFirst(), position.getSecond()));
	}

	@Override
	public void arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		ipeDrawing.draw(IpeDraw.drawIpeCircularArc(mid.getFirst(), mid.getSecond(), from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond()));
	}
	@Override
	public void arc(Pair<Integer> from, Pair<Integer> to) {
		ipeDrawing.draw(IpeDraw.drawIpeSemiCircle(from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond()));
	}

	@Override
	public void line(Pair<Integer> from, Pair<Integer> to, boolean collidesLine) {
		ipeDrawing.draw(IpeDraw.drawIpeEdge(from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond(), collidesLine ? "red" : "black"));
	}

	@Override
	public void line(Pair<Pair<Integer>> endpoints, boolean collidesLine) {
		line(endpoints.getFirst(), endpoints.getSecond(), collidesLine);
	}

	@Override
	public void arc(Pair<Integer> from, Pair<Integer> to, boolean collidesArc) {
		ipeDrawing.draw(IpeDraw.drawIpeSemiCircle(from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond(), collidesArc ? "red" : "black"));
	}

	@Override
	public void arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to,
			boolean collidesArc) {
		ipeDrawing.draw(IpeDraw.drawIpeCircularArc(mid.getFirst(), mid.getSecond(), from.getFirst(), from.getSecond(), to.getFirst(), to.getSecond(), collidesArc ? "red" : "black"));
	}

	@Override
	public void newPage() {
		ipeDrawing.draw(IpeDraw.newPage());
	}

	@Override
	public Appendable create() {
		return ipeDrawing.create();
	}

}
