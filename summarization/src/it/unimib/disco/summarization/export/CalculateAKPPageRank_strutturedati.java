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

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class CalculateAKPPageRank_strutturedati {

	// hashmap containing the pagerank for single concepts, datatypes or
	// properties
	private static HashMap<String, Double> pagerankMap = new HashMap<String, Double>();
	// arraylist containing pairs (subject, object) from a file AKP
	private static HashSet<PairAKP> subjectObjectPairs = new HashSet<PairAKP>();
	
	private static HashMap<String, HashSet<String>> conceptNeighboursMap = new HashMap<String, HashSet<String>>();
	// hashmap containing for each pair (concept1, concept2) the related AKP
	// pagerank
	private static HashMap<ArrayList<String>, Double> AkpPagerankMap = new HashMap<ArrayList<String>, Double>();

	public static void main(String[] args) {
		/*
		buildSubjectObjectPairs(
				"/home/silvia/git/abstat/data/summaries/dbpedia-2015-10/patterns/object-akp-original.txt");
		*/
		buildSubjectObjectPairs(
				"/home/silvia/git/abstat/data/summaries/dbpedia-2015-10/patterns/datatype-akp-rinomina.txt");
		DirectedSparseGraph<HashSet<String>, Long> g = computeAKPGraph(
				new DirectedSparseGraph<HashSet<String>, Long>());
		//HashMap<HashSet<String>, Double> pg = computePageRank(g);
	
		/*
		 * String source1 = args[0]; // count-concepts.txt file String source2 =
		 * args[1]; // count-datatype.txt file String source3 = args[2]; //
		 * count-datatype-properties.txt file String source4 = args[3]; //
		 * count-object-properties.txt file String dest1 = args[4]; //
		 * datatype-akp.txt file String dest2 = args[5]; // object-akp.txt file
		 * 
		 * BufferedReader br = null;
		 * 
		 * try { br = new BufferedReader(new FileReader(dest1));
		 * 
		 * if (br.readLine().split("##").length > 4) { System.out.
		 * println("PageRank score has already been computed for the first destination file"
		 * ); br = new BufferedReader(new FileReader(dest2)); if
		 * (br.readLine().split("##").length > 4) { System.out.
		 * println("PageRank score has already been computed for the second destination file"
		 * ); br.close(); } else { br.close(); buildPagerankMap(source1);
		 * buildPagerankMap(source2); buildPagerankMap(source3);
		 * buildPagerankMap(source4); computeAndWrite(dest2); } } else { br =
		 * new BufferedReader(new FileReader(dest2)); if
		 * (br.readLine().split("##").length > 2) { System.out.
		 * println("PageRank score has already been computed for the second destination file"
		 * ); br.close(); buildPagerankMap(source1); buildPagerankMap(source2);
		 * buildPagerankMap(source3); buildPagerankMap(source4);
		 * computeAndWrite(dest1); } else { br.close();
		 * buildPagerankMap(source1); buildPagerankMap(source2);
		 * buildPagerankMap(source3); buildPagerankMap(source4);
		 * computeAndWrite(dest1); computeAndWrite(dest2); } }
		 * 
		 * } catch (FileNotFoundException e) {
		 * System.out.println("Attention: FileNotFoundException!");
		 * e.printStackTrace(); } catch (IOException e) {
		 * System.out.println("Attention: IOException!"); e.printStackTrace(); }
		 */
	}

	/**
	 * This method builds pagerankMap, that is a hashmap containing the pagerank
	 * for single concepts, datatypes or properties
	 * 
	 * @param file
	 */
	public static void buildPagerankMap(String file) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##");
				if (!(pagerankMap.containsKey(lineArr[0]))) {
					pagerankMap.put(lineArr[0], Double.parseDouble(lineArr[2]));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Attention: FileNotFoundException!");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("Attention: NumberFormatException!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Attention: IOException!");
			e.printStackTrace();
		}
	}

	public static void buildSubjectObjectPairs(String AKPFile) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(AKPFile));

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##");

				subjectObjectPairs.add(new PairAKP(lineArr[0], lineArr[2]));
				if(!conceptNeighboursMap.containsKey(lineArr[0])){
					HashSet<String> neighbours = new HashSet<String>();
					neighbours.add(lineArr[2]);
					conceptNeighboursMap.put(lineArr[0],neighbours);
				}
				else{
					HashSet<String> neighbours = conceptNeighboursMap.get(lineArr[0]);
					neighbours.add(lineArr[2]);
					conceptNeighboursMap.put(lineArr[0],neighbours);
				}
				
				if(!conceptNeighboursMap.containsKey(lineArr[2])){
					HashSet<String> neighbours = new HashSet<String>();
					neighbours.add(lineArr[0]);
					conceptNeighboursMap.put(lineArr[2],neighbours);
				}
				else{
					HashSet<String> neighbours = conceptNeighboursMap.get(lineArr[2]);
					neighbours.add(lineArr[0]);
					conceptNeighboursMap.put(lineArr[2],neighbours);
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

	}

	public static void computeAndWrite(String file, HashMap<HashSet<String>, Double> pagerankAKP) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<String> tmp = new ArrayList<String>();
			String line = null;

			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##"); // split on "##"

				double prSubj = Collections.min(pagerankMap.values());
				double prProp = prSubj;
				double prObj = prSubj;

				if (pagerankMap.containsKey(lineArr[0])) {
					prSubj = pagerankMap.get(lineArr[0]);
				} else {
					pagerankMap.put(lineArr[0], prSubj);
				}
				if (pagerankMap.containsKey(lineArr[1])) {
					prProp = pagerankMap.get(lineArr[1]);
				} else {
					pagerankMap.put(lineArr[1], prProp);
				}
				if (pagerankMap.containsKey(lineArr[2])) {
					prObj = pagerankMap.get(lineArr[2]);
				} else {
					pagerankMap.put(lineArr[2], prObj);
				}

				HashSet<String> key = new HashSet<String>();
				key.add(lineArr[0]);
				key.add(lineArr[2]);
				double pagerank = pagerankAKP.get(key);

				tmp.add(line + "##" + pagerank + "##" + prSubj + "##" + prProp + "##" + prObj + "\n");
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

	public static DirectedSparseGraph<HashSet<String>, Long> computeAKPGraph(
			DirectedSparseGraph<HashSet<String>, Long> graph) {

		// count the number of edges of the graph
		long edgeCount = graph.getEdgeCount();

		for (PairAKP t : subjectObjectPairs) {
			String subject = t.getSubject();
			String object = t.getObject();

			HashSet<String> subjectNeighbours = conceptNeighboursMap.get(subject); 
			HashSet<String> objectNeighbours = conceptNeighboursMap.get(object); 
			for (String sn : subjectNeighbours) {
					for (String on : objectNeighbours) {
						PairAKP ts = new PairAKP(subject, sn);
						PairAKP td = new PairAKP(object, on);
						if ((!td.unorderedEquals(ts))
								&& (td.getSubject().equals(object) || td.getObject().equals(object))) {

							HashSet<String> source = new HashSet<String>();
							source.add(ts.getSubject());
							source.add(ts.getObject());

							graph.addVertex(source);

							HashSet<String> destination = new HashSet<String>();
							destination.add(td.getSubject());
							destination.add(td.getObject());

							graph.addVertex(destination);

							if (graph.findEdge(source, destination) == null) {
								graph.addEdge(new Long(edgeCount + 1), source, destination);
								edgeCount += 1;
								if (edgeCount % 100 == 0)
									System.out.println("conteggio archi:" + edgeCount);

							}
					}
				}
			}
		}
		for (long e : graph.getEdges()) {
			System.out.println(graph.getEndpoints(e).getFirst() + " ## " + graph.getEndpoints(e).getSecond());
		}
		System.out.println(graph.getEdgeCount());
		System.out.println(graph.getVertices().size());

		return graph;
	}

	public static HashMap<HashSet<String>, Double> computePageRank(
			DirectedSparseGraph<HashSet<String>, Long> graph) {

		/* Execute PageRank algorithm with alpha (dumping factor) = 0.3 */
		PageRank<HashSet<String>, Long> ranker = new PageRank<HashSet<String>, Long>(graph, 0.3);

		ranker.evaluate();
		ranker.setTolerance(0.000001);
		ranker.setMaxIterations(200);

		HashMap<HashSet<String>, Double> result = new HashMap<HashSet<String>, Double>();
		for (HashSet<String> v : graph.getVertices()) {
			result.put(v, ranker.getVertexScore(v));
		}
		return result;
	}

}
