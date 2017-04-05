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
	
	
	public static void main(String[] args){
		String source1 = args[0];
		String source2 = args[1];
		String destination = args[2];
		
		DirectedSparseGraph<String, Integer> g = new DirectedSparseGraph<>();
		g = computeGraph(g, source1);
		g = computeGraph(g, source2);
		
		try {	
			HashMap<String, Double> hm = computePageRank(g);
			for(String s : hm.keySet())
				System.out.println("chiave: " + s + " valore: " + hm.get(s).toString());
			writePageRank(hm, destination);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static DirectedSparseGraph<String, Integer> computeGraph(DirectedSparseGraph<String, Integer> graph, String fileAKP){
		int edgeCount = graph.getEdgeCount();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileAKP));
			String line;
			while((line = br.readLine())!= null){
				String[] lineArr = line.split("##");
				String source = lineArr[0];
				String dest = lineArr[2];
				graph.addVertex(source);
				graph.addVertex(dest);
				System.out.println(edgeCount);
				if(graph.findEdge(source, dest)==null){
					graph.addEdge(new Integer(edgeCount+1), source, dest);
					edgeCount += 1;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("numero archi " + edgeCount);
		
		return graph;
	}
	
	
	public static HashMap<String, Double> computePageRank(DirectedSparseGraph<String, Integer> graph) throws IOException {
		
		PageRank<String, Integer> ranker = new PageRank<String, Integer>(graph, 0.3);
		//ranker.setTolerance(this.tolerance) ;	
		//ranker.setMaxIterations(this.maxIterations);

		ranker.evaluate();
		ranker.setTolerance(0.000001);
		ranker.setMaxIterations(200);

	
				
		HashMap<String, Double> result = new HashMap<String, Double>();
		for (String v : graph.getVertices()) {
			result.put(v, ranker.getVertexScore(v));
		}
		return result;
}


	public static void writePageRank(HashMap<String, Double> ranking, String file){
		System.out.println("write");
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				ArrayList<String> tmp = new ArrayList<String>(); 
				String line;
				while((line = br.readLine())!=null){
					System.out.println(line);
					String resource = line.split("##")[0];
					System.out.println(resource);
					double pagerank = 0;
					try{
						 pagerank = ranking.get(resource);
					}
					catch(NullPointerException e){
						pagerank = Collections.min(ranking.values());
					}
					
					tmp.add(line + "##" + pagerank + "\n");
				}
				FileWriter bw = new FileWriter(file);
				for(int i=0; i<tmp.size();i++){
					bw.append(tmp.get(i));
					bw.flush();
				}
				br.close();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}

