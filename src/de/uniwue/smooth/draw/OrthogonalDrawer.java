package de.uniwue.smooth.draw;

import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.util.tuples.Tuple;

public interface OrthogonalDrawer<V, E, T> extends Transformer<Tuple<OrthogonalLayout<V, E>, OrthogonalDrawing<T>>, T> {

}
