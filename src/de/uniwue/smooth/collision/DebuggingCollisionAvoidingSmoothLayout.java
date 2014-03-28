package de.uniwue.smooth.collision;

import java.util.List;

import de.uniwue.smooth.collision.segments.Segment;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import de.uniwue.smooth.draw.SmoothOrthogonalDrawer;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.util.tuples.MutablePair;
import edu.uci.ics.jung.graph.util.Pair;

public class DebuggingCollisionAvoidingSmoothLayout<V, E> extends CollisionAvoidingSmoothLayout<V, E> {
	
	private static int MAXIMUM_MOVING_DISTANCE = 1000;
	
	int snapshotCounter = 0;
	private OrthogonalDrawing<Appendable> drawing;
	
	public DebuggingCollisionAvoidingSmoothLayout(CompressingLiuEtAlLayout<V, E> liuEtAlLayout, OrthogonalDrawing<Appendable> drawing) {
		super(liuEtAlLayout);
		this.drawing = drawing;
	}
	
	@Override
	protected void snapshot(String comment) {
		
		drawing.label(new Pair<Integer>(-1, -1), "\\verb|snap #" + snapshotCounter + " " + comment.replace('|', '\\') + "|");
		
		SmoothOrthogonalDrawer<V, E> drawer = new SmoothOrthogonalDrawer<V, E>();
		drawer.draw(this, drawing);
		
		drawOpenEdges();
		
		drawing.newPage();
		snapshotCounter++;
		
	}
	
	private void drawOpenEdges() {
		for (E e : getOriginalGraph().getEdges()) {
			MutablePair<Pair<Integer>> endpoints = getEndpointLocations(e);
			if(endpoints.getFirst() != null && endpoints.getSecond() == null) { // yes, open edge
				List<Segment> segments = getOpenEdgeSegmentsForDisplay(endpoints, e);
				for(Segment segment : segments) segment.draw(drawing);
				
			}
		}

	}
	
	@Override
	protected int getMaximumMovingDistance() {
		return MAXIMUM_MOVING_DISTANCE;
	}
	
}
