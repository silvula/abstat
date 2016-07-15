package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import it.unimib.disco.summarization.ontology.PropertyGraph;

public class AKPsPartitioner {

	private PropertyGraph propertyGraph;
	private List<HashSet<String>> pseudoSCS;
	private ArrayList<String> externalProperties = new ArrayList<String>();  //struttura usata per dare indici (negativi) 
	
	
	public AKPsPartitioner(File ontology) throws Exception{
		propertyGraph = new PropertyGraph(ontology);
		pseudoSCS = propertyGraph.convertPseudoSCS( propertyGraph.pseudoStronglyConnectedSets() );
	}
	
	
	public void AKPs_Grezzo_partition(File input_f, File output_dir, String suffix) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(input_f));
		String line;
		while ((line = br.readLine()) != null) {
			if(!line.equals("")){
				int begin = line.indexOf("<");
				int end = line.indexOf(">");
				String tripla = line.substring(begin, end);
				String predicate = tripla.split("##")[1];
				
				int indexSet = -1;
				for(HashSet<String> set : pseudoSCS){
					if(set.contains(predicate))
						indexSet = pseudoSCS.indexOf(set);	
				}
				
				if(indexSet==-1){
					if(!externalProperties.contains(predicate))
						externalProperties.add(predicate);
					indexSet = 0 - externalProperties.indexOf(predicate);
				}
				
				File outputFile = new File (output_dir+"/"+ "predicateSet"+indexSet + suffix + ".txt");
				FileOutputStream fos = new FileOutputStream(outputFile, true);
				fos.write((line+"\n\n").getBytes());
				fos.close();
			}
		}
		br.close();
	}
	
	
	public void AKPsPartion(File input_f, File output_dir, String suffix) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(input_f));
		String line;
		while ((line = br.readLine()) != null) {
			if(!line.equals("")){
				String[] splitted = line.split("##");
				String predicate = splitted[1];
					
				int indexSet = -1;
				for(HashSet<String> set : pseudoSCS){
					if(set.contains(predicate))
							indexSet = pseudoSCS.indexOf(set);	
				}
					
				if(indexSet==-1){
					if(!externalProperties.contains(predicate))
						externalProperties.add(predicate);
					indexSet = 0 - externalProperties.indexOf(predicate);
				}
				
				File outputFile = new File (output_dir+"/"+ "predicateSet"+indexSet + suffix + ".txt");
				FileOutputStream fos = new FileOutputStream(outputFile, true);
				fos.write((line+"\n\n").getBytes());
				fos.close();
			}
		}
		br.close();	
	}
	
	
}