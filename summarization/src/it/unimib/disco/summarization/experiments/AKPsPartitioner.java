package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.PropertyGraph;

public class AKPsPartitioner {

	private PropertyGraph propertyGraph;
	private List<ArrayList<String>> pseudoSCS;
	private ArrayList<String> externalProperties;  //struttura usata per dare indici (negativi) 
	
	
	public AKPsPartitioner(File ontology) throws Exception{
		propertyGraph = new PropertyGraph(ontology);
		pseudoSCS = propertyGraph.convertPseudoSCS( propertyGraph.pseudoStronglyConnectedSets() );
		externalProperties = new ArrayList<String>();
	}
	
	
	/* Legge il file AKP_Grezzo e smista le sue righe (tripla ->akps) in files separati per predicato. Due triple che usano predicati che stanno nello stessopseudoSCS si 
	 * troveranno in partizioni i cui nomi hanno la stessa radice.*/
	public void AKPs_Grezzo_partition(File input_f, File output_dir, String suffix) throws Exception{
		Events.summarization().info("START partitioning " + input_f.getName()); 	
		
		BufferedReader br = new BufferedReader(new FileReader(input_f));
		String line;
		while ((line = br.readLine()) != null) {
			if(!line.equals("")){
				String tripla = line.substring( line.indexOf("<"), line.indexOf(">"));
				String predicate = tripla.split("##")[1];
				
				int indexSet = -1;
				int indexProp = -1;
				int depthProp = -1;
				for(ArrayList<String> set : pseudoSCS){
					if(set.contains(predicate)){
						indexSet = pseudoSCS.indexOf(set);
						indexProp = set.indexOf(predicate);
						depthProp = propertyGraph.returnV(predicate).getDepth();
						break;
					}
				}
				
				if(indexSet==-1){
					if(!externalProperties.contains(predicate))
						externalProperties.add(predicate);
					indexSet = 0 - externalProperties.indexOf(predicate) -1;  //il -1 perchè lo 0 appartiene a quelli interni
				}
				
				File outputFile = new File (output_dir+"/"+ "predicateSet"+indexSet + "_"+ indexProp + "_Depth" + depthProp + suffix + ".txt");
				FileOutputStream fos = new FileOutputStream(outputFile, true);
				fos.write((line+"\n").getBytes());
				fos.close();
			}
		}
		br.close();
		
		Events.summarization().info("END partitioning " + input_f.getName());	
	}

	
}