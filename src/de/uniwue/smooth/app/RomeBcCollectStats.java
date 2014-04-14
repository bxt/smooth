package de.uniwue.smooth.app;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import de.uniwue.smooth.collision.BoundariesManager;
import de.uniwue.smooth.collision.CollisionAvoidingSmoothLayout;
import de.uniwue.smooth.collision.CollisionAvoidingSmoothLayout.EscalationLevel;
import de.uniwue.smooth.collision.CollisionManager;
import de.uniwue.smooth.collision.segments.EdgeGenerator;
import de.uniwue.smooth.collision.segments.Segment;
import de.uniwue.smooth.collision.segments.SegmentedEdge;
import de.uniwue.smooth.collision.segments.SmoothEdge;
import de.uniwue.smooth.collision.segments.SmoothEdgeGenerator;
import de.uniwue.smooth.collision.segments.SmoothEdgeType;
import de.uniwue.smooth.collision.segments.StraightlineEdgeGenerator;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.LiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.util.Benchmark;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;

public class RomeBcCollectStats implements Runnable {
	
	private static String SPACE = "\t";
	
	public static void main(String[] args) throws Exception {
		new RomeBcCollectStats().run();
	}

	@Override
	public void run() {
		Benchmark b = new Benchmark();
		
		List<String[]> filesCsvList;
		try {
			filesCsvList = new CSVReader(new FileReader("resources/rome_bc/degree-4-rm_duplicates.txt"), ';').readAll();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		int ignored = 0;
		Set<String> ignore = new HashSet<>();
		Collections.addAll(ignore, "bc_grafo10187.33.lgr.graphml", "bc_grafo2456.22.lgr.graphml", "bc_grafo2735.36.lgr.graphml", "bc_grafo5663.34.lgr.graphml", "bc_grafo5759.35.lgr.graphml", "bc_grafo5808.33.lgr.graphml", "bc_grafo5917.34.lgr.graphml", "bc_grafo920.24.lgr.graphml", "bc_grafo928.13.lgr.graphml");
		
		StringBuilder sb = new StringBuilder();
		sb.append("filename" + SPACE + "vertexCount" + SPACE + "edgeCount");
		for(String mode : new String[]{"orthogonal", "orthogonalCompressed", "smooth", "smoothCheapAdj", "smoothAllAdj", "smoothNoG"}) {
			sb.append(SPACE + mode + "Width" + SPACE + mode + "Height" + SPACE + mode + "Complexity");
		}
		sb.append("\r\n");
		
		for (final String[] cvsLine : filesCsvList) {
			
			String filename = cvsLine[0];
			if(ignore.contains(filename)) {ignored++; continue;}
			
			try {
				File file = new File("resources/rome_bc/" + filename);
				System.out.println(filename);
				Matcher m = Pattern.compile("bc_(.*)\\.\\d+\\.lgr\\.graphml").matcher(filename);
				if(!m.matches()) throw new IllegalStateException("bad filename.");
				sb.append(m.group(1));
				sb.append(SPACE);
				
				GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
				Graph<Vertex, Edge> graph = graphReader.readGraph();
				
				sb.append(graph.getVertexCount());
				sb.append(SPACE);
				
				sb.append(graph.getEdgeCount());
				sb.append(SPACE);
				
				LiuEtAlLayout<Vertex, Edge> nocompressLiuLayout = new LiuEtAlLayout<Vertex, Edge>(graph);
				CompressingLiuEtAlLayout<Vertex, Edge> compressliuLayout = new CompressingLiuEtAlLayout<Vertex, Edge>(graph);
				OrthogonalLayout<Vertex, Edge> layoutNoadjust = new CollisionAvoidingSmoothLayout<Vertex, Edge>(compressliuLayout, EscalationLevel.NO_ADJUSTMENTS);
				OrthogonalLayout<Vertex, Edge> layoutSomeadjust = new CollisionAvoidingSmoothLayout<Vertex, Edge>(compressliuLayout, EscalationLevel.CHEAP_ADJUSTMENTS);
				OrthogonalLayout<Vertex, Edge> layoutAlladjust = new CollisionAvoidingSmoothLayout<Vertex, Edge>(compressliuLayout, EscalationLevel.ALL_ADJUSTMENTS);
				
				nocompressLiuLayout.initialize();
				compressliuLayout.initialize();
				layoutNoadjust.initialize();
				layoutSomeadjust.initialize();
				layoutAlladjust.initialize();
				
				analyseStraightlineLayout(nocompressLiuLayout, sb);
				analyseStraightlineLayout(compressliuLayout, sb);
				
				boolean x = analyseSmoothLayout(layoutNoadjust, sb);
				boolean y = analyseSmoothLayout(layoutSomeadjust, sb);
				boolean z = analyseSmoothLayout(layoutAlladjust, sb);
				if(z) throw new RuntimeException("Uh oh, collisions left!");
				
				if(x) {
					if(y) analyseSmoothLayoutGLess(layoutAlladjust, sb);
					else analyseSmoothLayoutGLess(layoutSomeadjust, sb);
				} else analyseSmoothLayoutGLess(layoutNoadjust, sb);
				
				sb.append("\r\n");
				graphReader.close();
			} catch (GraphIOException e) {
				throw new RuntimeException(e);
			/*} catch (Exception e) {
				e.printStackTrace();*/
			} finally {
			}
		}
		
		int totalCount = filesCsvList.size() - ignored;
		
		System.out.println("Analyzed " + totalCount +  " graphs. ");
		Util.writeFile("resources/analysis.txt", sb.toString());
		b.print();
	}
	
	private boolean analyseStraightlineLayout(OrthogonalLayout<Vertex, Edge> layout, StringBuilder sb) {
		EdgeGenerator<Vertex, Edge, ?> edgeGenerator = new StraightlineEdgeGenerator<Vertex, Edge>(layout);
		return analyseLayout(edgeGenerator, layout, sb, false);
	}
	
	private boolean analyseSmoothLayout(OrthogonalLayout<Vertex, Edge> layout, StringBuilder sb) {
		EdgeGenerator<Vertex, Edge, ?> edgeGenerator = new SmoothEdgeGenerator<Vertex, Edge>(layout);
		return analyseLayout(edgeGenerator, layout, sb, true);
	}
	
	private boolean analyseSmoothLayoutGLess(OrthogonalLayout<Vertex, Edge> layout, StringBuilder sb) {
		SmoothEdgeGenerator<Vertex, Edge> innerEdgeGenerator = new SmoothEdgeGenerator<Vertex, Edge>(layout);
		EdgeGenerator<Vertex, Edge, ?> edgeGenerator = new NoGEdgeGenerator<Vertex, Edge>(innerEdgeGenerator);
		return analyseLayout(edgeGenerator, layout, sb, true);
	}
	
	private boolean analyseLayout(EdgeGenerator<Vertex, Edge, ?> edgeGenerator, OrthogonalLayout<Vertex, Edge> layout, StringBuilder sb, boolean analysePlanarity) {
		CollisionManager collisionManager = new CollisionManager();
		BoundariesManager boundariesManager = new BoundariesManager();
		int segmentsTotal = 0;
		List<Double> segmentSizes = new ArrayList<Double>(layout.getGraph().getEdgeCount());
		for (final Edge e : layout.getGraph().getEdges()) {
			SegmentedEdge edge = edgeGenerator.generateEdge(e);
			collisionManager.addAll(edge.getSegments());
			boundariesManager.addAll(edge.getSegments());
			segmentsTotal += edge.getSegments().size();
			segmentSizes.add((double)edge.getSegments().size());
		}
		boolean collides = ! collisionManager.collisions().isEmpty();
		if(analysePlanarity && collides) {
			sb.append("-1");
			sb.append(SPACE);
			sb.append("-1");
			sb.append(SPACE);
			sb.append("-1");
			sb.append(SPACE);
		} else {
			int width = (int) Math.ceil(boundariesManager.getWidth());
			int height = (int) Math.ceil(boundariesManager.getHeight());
			sb.append(width);
			sb.append(SPACE);
			sb.append(height);
			sb.append(SPACE);
			sb.append(segmentsTotal);
			sb.append(SPACE);
		}
		return collides;
	}
	
	private static class NoGEdgeGenerator<V, E> implements EdgeGenerator<V, E, SegmentedEdge> {
		
		private SmoothEdgeGenerator<V, E> edgeGenerator;
		
		public NoGEdgeGenerator(SmoothEdgeGenerator<V, E> edgeGenerator) {
			this.edgeGenerator = edgeGenerator;
		}

		@Override
		public SegmentedEdge generateEdge(E e) {
			 SmoothEdge edge = edgeGenerator.generateEdge(e);
			 if(edge.getEdgeType() == SmoothEdgeType.G) {
				 return new SmoothEdge(Collections.<Segment>emptyList(), null, SmoothEdgeType.G);
			 } else {
				return edge; 
			 }
		}
		
	}
	
}
