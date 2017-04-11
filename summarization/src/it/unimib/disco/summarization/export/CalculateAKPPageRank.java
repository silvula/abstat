package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CalculateAKPPageRank {
	
	private static HashMap<String, Double> rankingMap = new HashMap<String, Double>();

	public static void main(String[] args) {
		
		String source1 = args[0]; //count-concepts.txt file
		String source2 = args[1]; //count-datatype.txt file
		String source3 = args[2]; //count-datatype-properties.txt file
		String source4 = args[3]; //count-object-properties.txt file
		String dest1 = args[4]; //datatype-akp.txt file
		String dest2 = args[5]; //object-akp.txt file
		 
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(dest1));

			if (br.readLine().split("##").length > 4) {
				System.out.println("PageRank score has already been computed for the first destination file");
				br = new BufferedReader(new FileReader(dest2));
				if (br.readLine().split("##").length > 4) {
					System.out.println("PageRank score has already been computed for the second destination file");
					br.close();
				} else {
					br.close();
					buildMap(source1);
					buildMap(source2);
					buildMap(source3);
					buildMap(source4);
					computeAndWrite(dest2);
				}
			} else {
				br = new BufferedReader(new FileReader(dest2));
				if (br.readLine().split("##").length > 2) {
					System.out.println("PageRank score has already been computed for the second destination file");
					br.close();
					buildMap(source1);
					buildMap(source2);
					buildMap(source3);
					buildMap(source4);
					computeAndWrite(dest1);
				} else {
					br.close();
					buildMap(source1);
					buildMap(source2);
					buildMap(source3);
					buildMap(source4);
					computeAndWrite(dest1);
					computeAndWrite(dest2);
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

	public static void buildMap(String file) {
		
		BufferedReader br = null;
		
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

	public static void computeAndWrite(String file) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<String> tmp = new ArrayList<String>();
			String line = null;

			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##"); //split on "##"
				
				double prSubj = Collections.min(rankingMap.values());
				double prProp = prSubj;
				double prObj = prSubj;

				if (rankingMap.containsKey(lineArr[0])) {
					prSubj = rankingMap.get(lineArr[0]);
				}
				else {
					rankingMap.put(lineArr[0], prSubj);
				}
				if (rankingMap.containsKey(lineArr[1])) {
					prProp = rankingMap.get(lineArr[1]);
				}
				else {
					rankingMap.put(lineArr[1], prProp);
				}
				if(rankingMap.containsKey(lineArr[2])) {
					prObj = rankingMap.get(lineArr[2]);
				}
				else {
					rankingMap.put(lineArr[2], prObj);
				}

				double pagerank = (prSubj + prProp + prObj) / 3; //compute AKP PageRank as mean of 3 PageRank values

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
}
