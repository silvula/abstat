package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class CalculatePropertiesPageRank {

	private static HashMap<String, ArrayList<String>> propertyDestinationsMap = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, ArrayList<String>> propertySourcesMap = new HashMap<String, ArrayList<String>>();

	public static void main(String[] args) {

		String source1 = args[0]; // datatype-akp.txt file
		String source2 = args[1]; // object-akp.txt file
		String destination1 = args[2]; // count-datatype-properties.txt file
		String destination2 = args[3]; //count-object-properties.txt file

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(destination1));

			if (br.readLine().split("##").length > 2) {
				System.out.println("PageRank score has already been computed for the first destination file");
				
				br = new BufferedReader(new FileReader(destination2));
				
				if (br.readLine().split("##").length > 2) {
					System.out.println("PageRank score has already been computed for the second destination file");
				} else {
					buildGraphMaps(source1);
					buildGraphMaps(source2);
					DirectedSparseGraph<String, Integer> g = computePropertiesGraph();
					HashMap<String, Double> ranking = computePageRank(g);
					writePageRank(ranking, destination2);
				}
			} else {
				br = new BufferedReader(new FileReader(destination2));
				
				if (br.readLine().split("##").length > 2) {
					System.out.println("PageRank score has already been computed for the second destination file");
					buildGraphMaps(source1);
					buildGraphMaps(source2);
					DirectedSparseGraph<String, Integer> g = computePropertiesGraph();
					HashMap<String, Double> ranking = computePageRank(g);
					writePageRank(ranking, destination1);
				} else {
					buildGraphMaps(source1);
					buildGraphMaps(source2);
					DirectedSparseGraph<String, Integer> g = computePropertiesGraph();
					HashMap<String, Double> ranking = computePageRank(g);
					writePageRank(ranking, destination1);
					writePageRank(ranking, destination2);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Attention: FileNotFoundException!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Attention: IOException!");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void buildGraphMaps(String fileAKP) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileAKP));
			
			String line = "";
			ArrayList<String> vertices = new ArrayList<String>();
			
			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##"); //split on "##"

				if (propertyDestinationsMap.containsKey(lineArr[2])) {
					vertices = propertyDestinationsMap.get(lineArr[2]);
				}
				vertices.add(lineArr[1]);
				propertyDestinationsMap.put(lineArr[2], (ArrayList<String>) vertices.clone());
				vertices.clear();

				if (propertySourcesMap.containsKey(lineArr[0])) {
					vertices = propertySourcesMap.get(lineArr[0]);
				}
				vertices.add(lineArr[1]);
				propertySourcesMap.put(lineArr[0], (ArrayList<String>) vertices.clone());
				vertices.clear();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Attention: FileNotFoundException!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Attention: IOException!");
			e.printStackTrace();
		}
	}

	public static DirectedSparseGraph<String, Integer> computePropertiesGraph() {
		
		DirectedSparseGraph<String, Integer> graph = new DirectedSparseGraph<String, Integer>();
		int edgeCount = 0;
		
		for (String kd : propertySourcesMap.keySet()) {
			ArrayList<String> destinationVertices = propertyDestinationsMap.get(kd);
			ArrayList<String> sourceVertices = propertySourcesMap.get(kd);
			
			if ((destinationVertices != null) && (sourceVertices != null)) {
				for (String source : sourceVertices) {
					graph.addVertex(source); //add the vertex to the graph
					for (String dest : destinationVertices) {
						graph.addVertex(dest); //add the vertex to the graph
						/*if there isn't any edge between source and dest, add to the graph*/
						if (graph.findEdge(source, dest) == null) {
							graph.addEdge(new Integer(edgeCount + 1), source, dest);
							edgeCount += 1;
						}
					}
				}
			}
		}
		return graph;
	}

	public static HashMap<String, Double> computePageRank(DirectedSparseGraph<String, Integer> graph) {

		/*Execute PageRank algorithm with alpha (dumping factor) = 0.3*/
		PageRank<String, Integer> ranker = new PageRank<String, Integer>(graph, 0.3);

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

				String resource = line.split("##")[0]; // concept
				double pagerank = 0; // value of the PageRank computed before

				try {
					pagerank = ranking.get(resource); // get the value of the PageRank of the resource
				} catch (NullPointerException e) {
					try {
						/*If there isn't any value of the PageRank for any resource, set the minimum value as PageRank*/
						pagerank = Collections.min(ranking.values());
					} catch (NoSuchElementException e1) {
						pagerank = 0.0000000000001;
					}
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
			System.out.println("Attention: IOException!");
			e.printStackTrace();
		}
	}
}
