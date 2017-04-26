package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CalculateResourceDescription {

	static HashMap<String, HashSet<String>> resourceDescription = new HashMap<String, HashSet<String>>();

	public static void main(String[] args) {

		computeDescriptionHashMap(args[0]); // AKP file
		computeDescriptionHashMap(args[1]); // AKP file

		writeDescription(args[2]); // resource file
		writeDescription(args[3]); // resource file
		writeDescription(args[4]); // resource file
		writeDescription(args[5]); // resource file
	}

	public static void computeDescriptionHashMap(String AKPFile) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(AKPFile));

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lineArr = line.split("##");
				if (!resourceDescription.containsKey(lineArr[1])) {
					HashSet<String> description = new HashSet<String>();
					description.add(lineArr[0]);
					description.add(lineArr[2]);
					resourceDescription.put(lineArr[1], description);
				} else {
					HashSet<String> description = resourceDescription.get(lineArr[1]);
					description.add(lineArr[0]);
					description.add(lineArr[2]);
					resourceDescription.put(lineArr[1], description);
				}

				if (!resourceDescription.containsKey(lineArr[0])) {
					HashSet<String> description = new HashSet<String>();
					description.add(lineArr[1]);
					resourceDescription.put(lineArr[0], description);
				} else {
					HashSet<String> description = resourceDescription.get(lineArr[0]);
					description.add(lineArr[1]);
					resourceDescription.put(lineArr[0], description);
				}

				if (!resourceDescription.containsKey(lineArr[2])) {
					HashSet<String> description = new HashSet<String>();
					description.add(lineArr[1]);
					resourceDescription.put(lineArr[2], description);
				} else {
					HashSet<String> description = resourceDescription.get(lineArr[2]);
					description.add(lineArr[1]);
					resourceDescription.put(lineArr[2], description);
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

	public static void writeDescription(String resourceFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(resourceFile));
			ArrayList<String> tmp = new ArrayList<String>();
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.split("##").length > 3) {
					System.out.println("Description has already been computed for file " + resourceFile);
					break;
				}
				String resource = line.split("##")[0];
				String lineUpdated = line + "## ";
				if (resourceDescription.containsKey(resource)) {
					HashSet<String> description = resourceDescription.get(resource);

					for (String d : description) {
						lineUpdated = lineUpdated + " " + d;
					}
				}
				tmp.add(lineUpdated + "\n");
			}

			FileWriter bw = new FileWriter(resourceFile);

			for (String l : tmp) {
				bw.append(l);
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
