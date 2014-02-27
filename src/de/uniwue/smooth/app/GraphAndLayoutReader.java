package de.uniwue.smooth.app;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.GraphMetadata.EdgeDefault;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import edu.uci.ics.jung.io.graphml.NodeMetadata;

/**
 * Creates graph objects using {@link Vertex} and {@link Edge} from graphml files.
 * 
 * Also creates a Layout for the graph using the values of the x and y properties of the nodes.
 * 
 * If you layout graphs with yEd you can preprocess the *.graphml-Files with something like this to get the properties:
 * N;N;N; s/<node id="(.*?)">(.*?)<y:Geometry (.*?) x="(.*?)" y="(.*?)"/>/<node id="\1" x="\4" y="\5">\2<y:Geometry \3 x="\4" y="\5"/>/g
 */
public class GraphAndLayoutReader {
	
	private Graph<Vertex, Edge> graph;
	private Layout<Vertex, Edge> layout;
	
	private final Map<Vertex, Point2D> vertexLocations = new HashMap<Vertex, Point2D>();
	
	private final Transformer<GraphMetadata, Graph<Vertex, Edge>> graphTransformer = new Transformer<GraphMetadata, Graph<Vertex, Edge>>() {
		@Override public Graph<Vertex, Edge> transform(GraphMetadata metadata) {
			metadata.getEdgeDefault();
			if (EdgeDefault.DIRECTED.equals(metadata.getEdgeDefault())) {
				return new DirectedSparseGraph<Vertex, Edge>();
			} else {
				return new UndirectedSparseGraph<Vertex, Edge>();
			}
		}
	};
	private final Transformer<NodeMetadata, Vertex> vertexTransformer = new Transformer<NodeMetadata, Vertex>() {
		@Override public Vertex transform(NodeMetadata metadata) {
			Vertex v = new Vertex(Integer.parseInt(metadata.getId().substring(1)));
			vertexLocations.put(v, new Point2D.Double(Double.parseDouble(metadata.getProperty("x")), - Double.parseDouble(metadata.getProperty("y"))));
			return v;
		}
	};
	private final Transformer<EdgeMetadata, Edge> edgeTransformer = new Transformer<EdgeMetadata, Edge>() {
		public Edge transform(EdgeMetadata metadata) {
			return new Edge(Integer.parseInt(metadata.getId().substring(1)));
		}
	};
	private final Transformer<HyperEdgeMetadata, Edge> hyperEdgeTransformer = new Transformer<HyperEdgeMetadata, Edge>() {
		@Override public Edge transform(HyperEdgeMetadata metadata) {
			return new Edge(Integer.parseInt(metadata.getId().substring(1)));
		}
	};

	public GraphAndLayoutReader(File file) {
		try {
			Reader fileReader = new BufferedReader(new FileReader(file));
			
			GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = new GraphMLReader2<Graph<Vertex, Edge>, Vertex, Edge>(fileReader, graphTransformer, vertexTransformer, edgeTransformer, hyperEdgeTransformer);
			
			graph = graphReader.readGraph();
			graphReader.close();
			
			Transformer<Vertex,Point2D> vertexLocationTransformer = TransformerUtils.mapTransformer(vertexLocations);
			layout = new StaticLayout<>(graph, vertexLocationTransformer);
			
		} catch (FileNotFoundException | GraphIOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Graph<Vertex, Edge> getGraph() {
		return graph;
	}

	public Layout<Vertex, Edge> getLayout() {
		return layout;
	}

}
