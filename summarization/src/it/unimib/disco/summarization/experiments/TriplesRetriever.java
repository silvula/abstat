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
					
					line = line.substring(1, line.length()-1);
					String[] stringAKPs = line.split(", ");  
					Pattern[] AKPs = new Pattern[stringAKPs.length];
		    		
					
					for(int i=0; i<stringAKPs.length;i++){
						String[] splitted = stringAKPs[i].split("##");
						String s = splitted[0];
						String p = splitted[1];
						String o = splitted[2];
						

						TypeGraphExperimental typeGraph = PG.getTypeGraph();
						PropertyGraph propertyGraph = PG.getPropertyGraph();
						
						//se soggetto o oggetto sono esterni setto depth = 1
						Concept sConcept = typeGraph.returnV_graph(new Concept(s));
						if(sConcept ==  null){
							sConcept = new Concept(s);
				//			if(!s.equals("http://www.w3.org/2002/07/owl#Thing"))
				//				sConcept.setDepth(1);
						}
							
						Concept oConcept = typeGraph.returnV_graph(new Concept(o));
						if(oConcept ==  null){
							oConcept = new Concept(o);
				//			if(!o.equals("http://www.w3.org/2002/07/owl#Thing") && !o.equals("http://www.w3.org/2000/01/rdf-schema#Literal"))
				//				oConcept.setDepth(1);
						}
						
						Property property = propertyGraph.returnV_graph( propertyGraph.createProperty(p));
						if(property == null){
							property = propertyGraph.createProperty(p);
				//			property.setDepth(1);
							propertyGraph.getGraph().addVertex(property);
						}
						
						AKPs[i] = new Pattern( new Concept(s), p, new Concept(o));
					}
		    		
					PG.contatoreIstanze(AKPs);
				}
			}
		}
		catch(Exception e){
			Events.summarization().error(file, e);
		}  
		
		PG.stampaPatternsSuFile(output_dir + "/patterns_splitMode" + outputFileSuffix, false);
		PG.getHeadPatterns(output_dir);
	}

	@Override
	public void endProcessing() throws Exception {	
	}

}
