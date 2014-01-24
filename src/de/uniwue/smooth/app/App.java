package de.uniwue.smooth.app;

import java.awt.Dimension;

import javax.swing.JFrame;

import de.uniwue.smooth.orthogonal.LiuEtAlLayout;
import de.uniwue.smooth.planar.BrandesEmbedding;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * Main class for testing and running examples.
 */
public class App {

	public static void main(String[] args) {
		
		new App().main();
	}
	
	public void main() {
		drawLiu(Generators.octahedron());
	}
	
	public boolean isPlanar(UndirectedGraph<Vertex, Edge> graph) {
		return BrandesEmbedding.isPlanar(graph);
	}
		
	public void drawLiu(Graph<Vertex, Edge> graph) {
		draw(new LiuEtAlLayout<Vertex, Edge>(graph));
	}
	public void drawCircle(Graph<Vertex, Edge> graph) {
		draw(new CircleLayout<Vertex, Edge>(graph));
	}
	
	public void draw(final Layout<Vertex, Edge> layout) {
		layout.setSize(new Dimension(600,600));
		VisualizationViewer<Vertex, Edge> vv = new VisualizationViewer<Vertex, Edge>(layout, new Dimension(600,600));
        vv.setVertexToolTipTransformer(new ToStringLabeller<Vertex>());
        vv.setEdgeToolTipTransformer(new ToStringLabeller<Edge>());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex, Edge>());
        
		JFrame frame = new JFrame("Graph view");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
		
	}
}
