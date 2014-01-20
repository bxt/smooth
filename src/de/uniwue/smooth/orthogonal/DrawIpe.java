package de.uniwue.smooth.orthogonal;

import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.util.OrthogonalIpeDrawing;

public class DrawIpe<V, E> implements Transformer<OrthogonalLayout<V, E>, String> {

	@Override
	public String transform(OrthogonalLayout<V, E> input) {
		OrthogonalIpeDrawing drawing = new OrthogonalIpeDrawing();
		
		// TODO ...
		
		return drawing.create();
	}
	
}