package it.unimib.disco.summarization.experiments;

import java.io.File;

import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.dataset.Processing;
import it.unimib.disco.summarization.export.Events;

public class TriplesRetriever implements Processing{
	private File ontology;
	private File output_dir;
	
	public TriplesRetriever(File ontology, File output_dir){
		this.ontology = ontology;
		this.output_dir = output_dir;
	}
	
	@Override
	public void process(InputFile file) throws Exception {
		String outputFileSuffix;
		PatternGraph PG;
		if(file.name().contains("datatype")){
			outputFileSuffix = "_datatype.txt";
			PG =  new PatternGraph(ontology, "datatype", true);
		}
		else{
			outputFileSuffix = "_object.txt";
			PG =  new PatternGraph(ontology, "object", true);
		}

		try{
			while (file.hasNextLine()) {
				String line = file.nextLine();
				if(!line.equals("")){
					int index = line.indexOf(" [")+1;
					line = line.substring(index);   //per togliere il relational assertion dalla riga.
					
					line =line.substring(1, line.length()-1);
					String[] stringAKPs = line.split(", ");  
					Pattern[] AKPs = new Pattern[stringAKPs.length];
		    		
					for(int i=0; i<stringAKPs.length;i++){
						String[] splitted = stringAKPs[i].split("##");
						String s = splitted[0];
						String p = splitted[1];
						String o = splitted[2];
						AKPs[i] = new Pattern( new Concept(s), p, new Concept(o));
					}
		    		
					PG.contatoreIstanze(AKPs);
				}
			}
		}
		catch(Exception e){
			Events.summarization().error(file, e);
		}  
		
		PG.stampaPatternsSuFile(output_dir + "/patterns_splitMode" + outputFileSuffix);
		PG.getHeadPatterns(output_dir);
	}

	@Override
	public void endProcessing() throws Exception {	
	}

}
