package de.uniwue.smooth.draw;

import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.util.tuples.Tuple;

/**
 * Draws vertices and edges from a layout into a drawing.
 * 
 * Implementors will make sure that all the vertices appear in their positions
 * and the edge positions and bends are well-positioned and agree with the ports
 * assigned in the layout.
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 * @param <T> Result type of the target drawing.
 */
public interface OrthogonalDrawer<V, E, T> extends Transformer<Tuple<OrthogonalLayout<V, E>, OrthogonalDrawing<T>>, T> {

}
