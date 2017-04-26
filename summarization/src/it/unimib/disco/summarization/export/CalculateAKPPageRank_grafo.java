package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.jena.sdb.util.Pair;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class CalculateAKPPageRank_grafo {

	public static void main(String[] args) {
		DirectedSparseGraph<String, Integer> g = new DirectedSparseGraph<String, Integer>();
		computeConceptsGraph(g,
				"/home/silvia/git/abstat/data/summaries/dbpedia-2015-10/patterns/datatype-akp-rinomina.txt");
		computeConceptsGraph(g,"/home/silvia/git/abstat/data/summaries/dbpedia-2015-10/patterns/object-akp-original.txt");
		// computeConceptsGraph(g,
		// "/home/silvia/git/abstat/data/summaries/dbpedia-2015-10/patterns/datatype-akp-rinomina.txt");
		DirectedSparseGraph<String, Long> akpGraph = computeAkpGraph(g);

		/*
		 for (long e : akpGraph.getEdges()) {
		  System.out.println(akpGraph.getEndpoints(e).getFirst() + " ## " +
		  akpGraph.getEndpoints(e).getSecond()); }
		 */
		System.out.println(akpGraph.getEdgeCount());
		System.out.println(akpGraph.getVertexCount());

		/*
		 * "/home/silvia/git/abstat/data/summaries/dbpedia-2015-10/patterns/object-akp-original.txt"
		 * 
		 * "/home/silvia/git/abstat/data/summaries/dbpedia-2015-10/patterns/datatype-akp-rinomina.txt"
		 * );
		 */

	}

	public static DirectedSparseGraph<String, Integer> computeConceptsGraph(DirectedSparseGraph<String, Integer> graph,
			String fileAKP) {

		int edgeCount = graph.getEdgeCount(); // count the number of edges of
												// the graph

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(fileAKP));
			String line = null;

			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##"); // split fields on "##"
				String source = lineArr[0]; // subject of the AKP
				String dest = lineArr[2]; // object of the AKP

				graph.addVertex(source); // add the vertex to the graph
				graph.addVertex(dest); // add the vertex to the graph

				if (graph.findEdge(source, dest) == null) { // if there isn't
															// any edge between
															// source and dest,
															// add the edge to
															// the graph
					graph.addEdge(new Integer(edgeCount + 1), source, dest);
					edgeCount += 1;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Attention: FileNotFoundException!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Attention: IOException!");
			e.printStackTrace();
		}
		return graph;
	}

	public static DirectedSparseGraph<String, Long> computeAkpGraph(DirectedSparseGraph<String, Integer> graph) {
		DirectedSparseGraph<String, Long> akpGraph = new DirectedSparseGraph<String, Long>();

		// create vertices as string composed of two strings in
		// lexicographically order separated by a "##"
		for (String v : graph.getVertices()) {
			for (String neigh : graph.getNeighbors(v)) {
				if (v.compareTo(neigh) < 0)
					akpGraph.addVertex(v + "##" + neigh);
				else
					akpGraph.addVertex(neigh + "##" + v);
			}
		}

		// create edges
		long edgeCount = 0;
		for (int i : graph.getEdges()) { // loop on edges of the simple graph
			// get the endpoints from edge of the simple graph
			String firstVertex = graph.getEndpoints(i).getFirst();
			String secondVertex = graph.getEndpoints(i).getSecond();

			// loop on the neighbours of the first vertex of the simple graph
			for (String neighFirst : graph.getNeighbors(firstVertex)) {

				// compose the first vertex of the akpGraph with the
				// concatenation of the strings
				String akpFirstVertex = "";
				if (firstVertex.compareTo(neighFirst) < 0)
					akpFirstVertex = firstVertex + "##" + neighFirst;
				else
					akpFirstVertex = neighFirst + "##" + firstVertex;

				// loop on the neighbours of the second vertex of the simple
				// graph
				for (String neighSecond : graph.getNeighbors(secondVertex)) {

					// comparison between unordered pairs
					if (!((firstVertex.equals(neighSecond) || firstVertex.equals(secondVertex))
							&& (neighFirst.equals(neighSecond) || neighFirst.equals(secondVertex)))) {

						// compose the second vertex of the akpGraph with the
						// concatenation of the strings
						String akpSecondVertex = "";
						if (secondVertex.compareTo(neighSecond) < 0)
							akpSecondVertex = secondVertex + "##" + neighSecond;
						else
							akpSecondVertex = neighSecond + "##" + secondVertex;

						akpGraph.addEdge(new Long(edgeCount + 1), akpFirstVertex, akpSecondVertex);
						edgeCount += 1;
						if (edgeCount % 100 == 0)
							System.out.println(edgeCount);
					}
				}
			}

		}
		return akpGraph;
	}

}
