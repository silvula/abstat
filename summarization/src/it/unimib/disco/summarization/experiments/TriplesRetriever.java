package it.unimib.disco.summarization.experiments;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.dataset.Processing;
import it.unimib.disco.summarization.export.Events;


public class TriplesRetriever implements Processing{
	private File ontology;
	private File output_dir;
	private File specialParts_outputs;
	private HashSet<String> specialFiles;
	
	public TriplesRetriever(File ontology, File output_dir, File akps_Grezzo_splitted_dir, File specialParts_outputs){
		this.ontology = ontology;
		this.output_dir = output_dir;
		this.specialParts_outputs = specialParts_outputs;
		findSpecialFiles(akps_Grezzo_splitted_dir);
	}
	
	
	/* trova le partizioni i cui predicati hanno in comune un antenato, questi insiemi hanno un trattamento speciale */
	private void findSpecialFiles(File akps_Grezzo_splitted_dir){
		File[] listOfFiles = akps_Grezzo_splitted_dir.listFiles();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < listOfFiles.length; i++) {
			String name = listOfFiles[i].getName();
			String key =  name.replace( name.substring(name.indexOf("_"), name.lastIndexOf("_")), "");
			if(map.containsKey(key))
				map.put(key, map.get(key) +1);
			else
				map.put(key, 1);
		}
			
		specialFiles = new HashSet<String>();
		for(String key : map.keySet())
			if(map.get(key)>1)
				specialFiles.add(key);		    
	}
	
	
	@Override
	public void process(InputFile file) throws Exception {
		double startTime = System.currentTimeMillis();
		
		PatternGraph PG;
		String type;
		boolean full_inference;
		String fileName = file.name().substring(file.name().lastIndexOf("/")+1);
		
		if(fileName.contains("datatype"))  type = "datatype"; 
		else  type = "object";
		
		if(specialFiles.contains( fileName.replace( fileName.substring(fileName.indexOf("_"), fileName.lastIndexOf("_")), "") ))  full_inference = false;
		else  full_inference = true;
		
		PG = new PatternGraph(ontology, type, full_inference);
		
		
		try{
			while (file.hasNextLine()) {
				String line = file.nextLine();
				if(!line.equals("")){
					line = line.substring( line.indexOf("> [") + 3, line.length()-1);   //per togliere il relational assertion dalla riga.

					String[] stringAKPs = line.split(", ");  
					Pattern[] AKPs = new Pattern[stringAKPs.length];
		    		
					for(int i=0; i<stringAKPs.length;i++){
						String[] splitted = stringAKPs[i].split("##");
						String s = splitted[0];
						String p = splitted[1];
						String o = splitted[2];	
						
						Concept sConcept = PG.getTypeGraph().returnV(new Concept(s));
						if(sConcept ==  null)
							sConcept = new Concept(s);
						
						Concept oConcept = PG.getTypeGraph().returnV(new Concept(o));
						if(oConcept ==  null)
							oConcept = new Concept(o);
						
						Property property = PG.getPropertyGraph().returnV( PG.getPropertyGraph().createProperty(p));
						if(property == null){
							property = PG.getPropertyGraph().createProperty(p);
////per evitare i null//    PG.getPropertyGraph().getGraph().addVertex(property);
							PG.getPropertyGraph().linkExternalProperty(property, type);   ///////////////////////////
						}
						
						AKPs[i] = new Pattern( sConcept, p, oConcept);					
					}
					
					PG.contatoreIstanze(AKPs);
				}
			}
		}
		catch(Exception e){ Events.summarization().error(file, e);}  
		
		
		if(!full_inference){
			String s = fileName.replace( fileName.substring(fileName.indexOf("_"), fileName.lastIndexOf("_")), "");
			s = s.substring(0, s.length()-4); //tolgo il .txt
			File specialDir = new File(specialParts_outputs + "/"+ s);
			if(!specialDir.exists()) 
				specialDir.mkdir();
			PG.stampaPatternsSuFile(specialDir +"/"+ fileName);
		}
		else{
			PG.stampaPatternsSuFile(output_dir + "/patterns_splitMode_"+ type +".txt", output_dir + "/HEADpatterns_"+type+"_unmerged.txt");
		}
		
		
		double endTime   = System.currentTimeMillis();
		Events.summarization().info( (endTime - startTime)/1000 +"s  ..." + fileName); 	
	}

	
	@Override
	public void endProcessing() throws Exception {	
	}

}
