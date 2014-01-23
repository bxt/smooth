package de.uniwue.smooth.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.GraphReader;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.GraphMetadata.EdgeDefault;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import edu.uci.ics.jung.io.graphml.NodeMetadata;

/**
 * Creates graph objects using {@link Vertex} and {@link Edge} from graphml files. 
 */
public class GraphReaderFactory {
	
	private static final Transformer<GraphMetadata, Graph<Vertex, Edge>> graphTransformer = new Transformer<GraphMetadata, Graph<Vertex, Edge>>() {
		@Override public Graph<Vertex, Edge> transform(GraphMetadata metadata) {
			metadata.getEdgeDefault();
			if (EdgeDefault.DIRECTED.equals(metadata.getEdgeDefault())) {
				return new DirectedSparseGraph<Vertex, Edge>();
			} else {
				return new UndirectedSparseGraph<Vertex, Edge>();
			}
		}
	};
	private static final Transformer<NodeMetadata, Vertex> vertexTransformer = new Transformer<NodeMetadata, Vertex>() {
		@Override public Vertex transform(NodeMetadata metadata) {
			return new Vertex(Integer.parseInt(metadata.getId().substring(1)));
		}
	};
	private static final Transformer<EdgeMetadata, Edge> edgeTransformer = new Transformer<EdgeMetadata, Edge>() {
		public Edge transform(EdgeMetadata metadata) {
			return new Edge(Integer.parseInt(metadata.getId().substring(1)));
		}
	};
	private static final Transformer<HyperEdgeMetadata, Edge> hyperEdgeTransformer = new Transformer<HyperEdgeMetadata, Edge>() {
		@Override public Edge transform(HyperEdgeMetadata metadata) {
			return new Edge(Integer.parseInt(metadata.getId().substring(1)));
		}
	};

	public static GraphReader<Graph<Vertex, Edge>, Vertex, Edge> create(Reader reader) {
		return new GraphMLReader2<Graph<Vertex, Edge>, Vertex, Edge>(reader, graphTransformer, vertexTransformer, edgeTransformer, hyperEdgeTransformer);
	}
	
	public static GraphReader<Graph<Vertex, Edge>, Vertex, Edge> create(File file) {
		try {
			Reader fileReader = new BufferedReader(new FileReader(file));
			return create(fileReader);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static GraphReader<Graph<Vertex, Edge>, Vertex, Edge> create(String filename) {
		return create(new File("resources/rome/grafo" + filename + ".graphml"));
	}
	
}
