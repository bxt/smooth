package de.uniwue.smooth.util;

public class OrthogonalIpeDrawing extends AbstractIpeDrawing {
	
	public void vertex(int x, int y) {
		draw(IpeDraw.drawIpeMark(x, y));
	}
	
	public void edgeMidpoint(int x, int y) {
		draw(IpeDraw.drawIpeMark(x, y, "cross"));
	}
	
	public void line(int x1, int y1, int x2, int y2) {
		draw(IpeDraw.drawIpeEdge(x1, y1, x2, y2));
	}
}
