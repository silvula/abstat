package it.unimib.disco.summarization.experiments;

import java.io.File;

import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.dataset.Processing;
import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.PropertyGraph;
import it.unimib.disco.summarization.ontology.TypeGraphExperimental;

public class TriplesRetriever implements Processing{
	private File ontology;
	private File output_dir;
	
	public TriplesRetriever(File ontology, File output_dir){
		this.ontology = ontology;
		this.output_dir = output_dir;
	}
	
	@Override
	public void process(InputFile file) throws Exception {
		double startTime = System.currentTimeMillis();
	
		
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
							PG.getPropertyGraph().getGraph().addVertex(property);
						}
						
						AKPs[i] = new Pattern( sConcept, p, oConcept);
						
					}
					
					PG.contatoreIstanze(AKPs);
					
				}
			}
		}
		catch(Exception e){
			Events.summarization().error(file, e);
		}  

		if(file.name().contains("datatype"))
			PG.stampaPatternsSuFile(output_dir + "/patterns-datatype_parts/"+ file.name().substring(file.name().lastIndexOf("/")), false);
		else
			PG.stampaPatternsSuFile(output_dir + "/patterns-object_parts/"+ file.name().substring(file.name().lastIndexOf("/")), false);

		PG.getHeadPatterns(output_dir);
		
		double endTime   = System.currentTimeMillis();
		Events.summarization().info( (endTime - startTime)/1000 +"s  ..." + file.name().substring(file.name().lastIndexOf("/"))); 	
	}

	@Override
	public void endProcessing() throws Exception {	
	}

}
