package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class CalculatePageRank {

	public static void main(String[] args) {

		String source1 = args[0]; //datatype-akp.txt file
		String source2 = args[1]; //object-akp.txt file
		String destination = args[2]; //count-concepts.txt file

		DirectedSparseGraph<String, Integer> g = new DirectedSparseGraph<>(); //create a graph
		g = computeGraph(g, source1); //add vertex and edges to a graph
		g = computeGraph(g, source2); //add vertex and edges to a graph

		try {	
			HashMap<String, Double> hm = computePageRank(g);
			for (String s : hm.keySet()) {
				System.out.println("chiave: " + s + " valore: " + hm.get(s).toString());
			}
			writePageRank(hm, destination); //write to file .txt the values of PageRank
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}

	public static DirectedSparseGraph<String, Integer> computeGraph(DirectedSparseGraph<String, Integer> graph, String fileAKP) {

		int edgeCount = graph.getEdgeCount(); //count the number of edges of the graph
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(fileAKP));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##"); //split fields on "##"
				String source = lineArr[0]; //subject of the AKP
				String dest = lineArr[2]; //object of the AKP

				graph.addVertex(source);
				graph.addVertex(dest);

				if (graph.findEdge(source, dest) == null) { //if there isn't any edge between source and dest, add to the graph
					graph.addEdge(new Integer(edgeCount+1), source, dest);
					edgeCount += 1;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}

		return graph;
	}


	public static HashMap<String, Double> computePageRank(DirectedSparseGraph<String, Integer> graph) throws IOException {

		PageRank<String, Integer> ranker = new PageRank<String, Integer>(graph, 0.3); //alpha (dumping factor) = 0.3

		ranker.evaluate();
		ranker.setTolerance(0.000001);
		ranker.setMaxIterations(200);

		HashMap<String, Double> result = new HashMap<String, Double>();
		for (String v : graph.getVertices()) {
			result.put(v, ranker.getVertexScore(v));
		}
		return result;
	}

	public static void writePageRank(HashMap<String, Double> ranking, String file) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<String> tmp = new ArrayList<String>(); 
			String line = null;

			while ((line = br.readLine()) != null) {
				String resource = line.split("##")[0]; //concept
				double pagerank = 0; //value of the PageRank computed before

				try {
					pagerank = ranking.get(resource); //get the value of the PageRank of the resource
				}
				catch (NullPointerException e) {
					pagerank = Collections.min(ranking.values()); //if there isn't any value of the PageRank for any resource, set the minimum value as PageRank
				}

				tmp.add(line + "##" + pagerank + "\n");
			}

			FileWriter bw = new FileWriter(file);

			for (int i = 0; i < tmp.size(); i++) {
				bw.append(tmp.get(i));
				bw.flush();
			}

			br.close();
			bw.close();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}

	}
}

