package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class CalculateAKPPageRank {
	private static HashMap<String, Double> rankingMap = new HashMap<String, Double>();

	public static void main(String[] args) {
		
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(args[4]));

			if (br.readLine().split("##").length > 4) {
				System.out.println("PageRank score has already been computed for the first destination file");
				br = new BufferedReader(new FileReader(args[5]));
				if (br.readLine().split("##").length > 4) {
					System.out.println("PageRank score has already been computed for the second destination file");

				} else {

					buildMap(args[0]);
					buildMap(args[1]);
					buildMap(args[2]);
					buildMap(args[3]);
					computeAndWrite(args[5]);
				}
			} else {
				br = new BufferedReader(new FileReader(args[5]));
				if (br.readLine().split("##").length > 2) {
					System.out.println("PageRank score has already been computed for the second destination file");

					buildMap(args[0]);
					buildMap(args[1]);
					buildMap(args[2]);
					buildMap(args[3]);
					computeAndWrite(args[4]);
				} else {
					buildMap(args[0]);
					buildMap(args[1]);
					buildMap(args[2]);
					buildMap(args[3]);
					computeAndWrite(args[4]);
					computeAndWrite(args[5]);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void buildMap(String file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##");
				if (!(rankingMap.containsKey(lineArr[0]))) {
					rankingMap.put(lineArr[0], Double.parseDouble(lineArr[2]));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void computeAndWrite(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<String> tmp = new ArrayList<String>();
			String line = null;

			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##");
				double prSubj = Collections.min(rankingMap.values());
				double prProp = prSubj;
				double prObj = prSubj;

				if(rankingMap.containsKey(lineArr[0])){
					prSubj = rankingMap.get(lineArr[0]);
				}
				else{
					rankingMap.put(lineArr[0], prSubj);
				}
				if(rankingMap.containsKey(lineArr[1])){
					prProp = rankingMap.get(lineArr[1]);
				}
				else{
					rankingMap.put(lineArr[1], prProp);
				}
				if(rankingMap.containsKey(lineArr[2])){
					prObj = rankingMap.get(lineArr[2]);
				}
				else{
					rankingMap.put(lineArr[2], prObj);
				}

				double pagerank = (prSubj + prProp + prObj) / 3;

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
			System.out.println("IOException");
			e.printStackTrace();
		}

	}
}
