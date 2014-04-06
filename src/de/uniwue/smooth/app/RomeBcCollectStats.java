package de.uniwue.smooth.app;

import java.awt.geom.AffineTransform;
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
import de.uniwue.smooth.collision.segments.SegmentedEdge;
import de.uniwue.smooth.collision.segments.SmoothEdgeGenerator;
import de.uniwue.smooth.collision.segments.StraightlineEdgeGenerator;
import de.uniwue.smooth.draw.IpeDrawing;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import de.uniwue.smooth.draw.OrthogonalIpeDrawing;
import de.uniwue.smooth.draw.TransformingOrthogonalDrawing;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.LiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.util.Benchmark;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
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
		
		List<Double> avgAll = new ArrayList<Double>(filesCsvList.size());
		List<Double> avgBst = new ArrayList<Double>(filesCsvList.size());
		List<Double> avgNoC = new ArrayList<Double>(filesCsvList.size());
		List<Double> avgCom = new ArrayList<Double>(filesCsvList.size());
		int nodesTotal = 0;
		
		OrthogonalDrawing<Appendable> drawingNoC = createPreparedDrawing();
		OrthogonalDrawing<Appendable> drawingCom = createPreparedDrawing();
		OrthogonalDrawing<Appendable> drawingBst = createPreparedDrawing();
		OrthogonalDrawing<Appendable> drawingAll = createPreparedDrawing();
		
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
				
				nodesTotal += graph.getVertexCount();
				sb.append(graph.getVertexCount());
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
				
				analyseStraightlineLayout(nocompressLiuLayout, sb, null, avgNoC, null, drawingNoC, "black");
				analyseStraightlineLayout(compressliuLayout, sb, null, avgCom, null, drawingCom, "black");
				
				boolean x = analyseSmoothLayout(layoutNoadjust, sb, null, avgBst, null, drawingBst, "green");
				boolean y = analyseSmoothLayout(layoutSomeadjust, sb, null, x ? avgBst : null, null, x ? drawingBst : null, "blue");
				boolean z = analyseSmoothLayout(layoutAlladjust, sb, avgAll, y ? avgBst : null, drawingAll, y ? drawingBst : null, "red");
				if(z) throw new RuntimeException("Uh oh, collisions left!");
								
				sb.append("\r\n");
				graphReader.close();
			} catch (GraphIOException e) {
				throw new RuntimeException(e);
			/*} catch (Exception e) {
				e.printStackTrace();*/
			} finally {
			}
		}
		
		Util.writeFile("resources/drawingNoC.ipe", drawingNoC.create().toString());
		Util.writeFile("resources/drawingCom.ipe", drawingCom.create().toString());
		Util.writeFile("resources/drawingBst.ipe", drawingBst.create().toString());
		Util.writeFile("resources/drawingAll.ipe", drawingAll.create().toString());

		
		int totalCount = filesCsvList.size() - ignored;
		
		sb.append("Anzahl der Graphen: " + totalCount);
		sb.append(SPACE);
		sb.append("Anzahl der Knoten: " + nodesTotal);
		sb.append(SPACE);
		if(avgBst.size() != totalCount) throw new IllegalStateException("Had " + avgNoC.size() + " numbers instead of " + totalCount);
		sb.append("Durchschnittliche Fläche pro Knoten orthogonal:" + avg(avgNoC));
		sb.append(SPACE);
		if(avgBst.size() != totalCount) throw new IllegalStateException("Had " + avgCom.size() + " numbers instead of " + totalCount);
		sb.append("Durchschnittliche Fläche pro Knoten orthogonal, with compression:" + avg(avgCom));
		sb.append(SPACE);
		if(avgBst.size() != totalCount) throw new IllegalStateException("Had " + avgBst.size() + " numbers instead of " + totalCount);
		sb.append("Durchschnittliche Fläche pro Knoten smooth:" + avg(avgBst));
		sb.append(SPACE);
		if(avgAll.size() != totalCount) throw new IllegalStateException("Had " + avgAll.size() + " numbers instead of " + totalCount);
		sb.append("Durchschnittliche Fläche pro Knoten smooth, ohne Optimierung:" + avg(avgAll));
		sb.append(SPACE);
		
		System.out.println("Analyzed " + totalCount +  " graphs. ");
		Util.writeFile("resources/analysis.txt", sb.toString());
		b.print();
	}
	
	private static double avg(List<Double> doubles) {
		double sum = 0;
		for(double d : doubles) {
			sum += d;
		}
		return sum/doubles.size();
	}
	
	private boolean analyseStraightlineLayout(OrthogonalLayout<Vertex, Edge> layout, StringBuilder sb, List<Double> avgAlways, List<Double> avgSometimes, OrthogonalDrawing<?> drawingA, OrthogonalDrawing<?> drawingB, String color) {
		EdgeGenerator<Vertex, Edge, ?> edgeGenerator = new StraightlineEdgeGenerator<Vertex, Edge>(layout);
		return analyseLayout(edgeGenerator, layout, sb, avgAlways, avgSometimes, false, drawingA, drawingB, color);
	}
	
	private boolean analyseSmoothLayout(OrthogonalLayout<Vertex, Edge> layout, StringBuilder sb, List<Double> avgAlways, List<Double> avgSometimes, OrthogonalDrawing<?> drawingA, OrthogonalDrawing<?> drawingB, String color) {
		EdgeGenerator<Vertex, Edge, ?> edgeGenerator = new SmoothEdgeGenerator<Vertex, Edge>(layout);
		return analyseLayout(edgeGenerator, layout, sb, avgAlways, avgSometimes, true, drawingA, drawingB, color);
	}
	
	private boolean analyseLayout(EdgeGenerator<Vertex, Edge, ?> edgeGenerator, OrthogonalLayout<Vertex, Edge> layout, StringBuilder sb, List<Double> avgAlways, List<Double> avgSometimes, boolean analysePlanarity, OrthogonalDrawing<?> drawingA, OrthogonalDrawing<?> drawingB, String color) {
		CollisionManager collisionManager = new CollisionManager();
		BoundariesManager boundariesManager = new BoundariesManager();
		for (final Edge e : layout.getGraph().getEdges()) {
			SegmentedEdge edge = edgeGenerator.generateEdge(e);
			collisionManager.addAll(edge.getSegments());
			boundariesManager.addAll(edge.getSegments());
		}
		boolean collides = ! collisionManager.collisions().isEmpty();
		if(analysePlanarity && collides) {
			sb.append("n.p.");
		} else {
			int width = (int) Math.ceil(boundariesManager.getWidth());
			int height = (int) Math.ceil(boundariesManager.getHeight());
			sb.append(width + " x " + height);
			int sqrtArea = (int) (1000*Math.sqrt(height*width));
			double areaPerNode = width*height/(double)layout.getGraph().getVertexCount();
			if(avgAlways != null) avgAlways.add(areaPerNode);
			if(avgSometimes != null) avgSometimes.add(areaPerNode);
			if(drawingA != null) {
				drawingA.edgeMidpoint(new Pair<Integer>(layout.getGraph().getVertexCount(), sqrtArea), color);
			}
			if(drawingB != null) {
				drawingB.edgeMidpoint(new Pair<Integer>(layout.getGraph().getVertexCount(), sqrtArea), color);
			}
		}
		sb.append(SPACE);
		return collides;
	}
	
	private OrthogonalDrawing<Appendable> createPreparedDrawing() {
		OrthogonalDrawing<Appendable> drawing = createDrawing();
		drawing.label(new Pair<Integer>(61,1000), "Anzahl der Knoten");
		drawing.line(new Pair<Integer>(0,0), new Pair<Integer>(60,0));
		for (int i = 0; i <= 6; i++) {
			drawing.line(new Pair<Integer>(i*10,1000), new Pair<Integer>(i*10,-1000));
			drawing.label(new Pair<Integer>(i*10,-2000), ""+i*10);
		}
		
		drawing.label(new Pair<Integer>(2,61000), "Fl\\\"ache der Zeichnung");
		drawing.line(new Pair<Integer>(0,0), new Pair<Integer>(0,60000));
		for (int i = 0; i <= 6; i++) {
			drawing.line(new Pair<Integer>(1,i*10000), new Pair<Integer>(-1,i*10000));
			drawing.label(new Pair<Integer>(1,i*10000), (i*10) + " x " + (i*10));
		}
		
		return drawing;
	}
	private OrthogonalDrawing<Appendable> createDrawing() {
		AffineTransform transform = new AffineTransform();
		transform.translate(60, 60);
		transform.scale(10, 0.01);
		
		return new TransformingOrthogonalDrawing<>(new OrthogonalIpeDrawing(new IpeDrawing()), transform);
	}
}
