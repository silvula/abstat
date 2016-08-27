package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.summarization.experiments.Concept;
import it.unimib.disco.summarization.experiments.Property;
import it.unimib.disco.summarization.ontology.PropertyGraph;
import it.unimib.disco.summarization.ontology.TypeGraphExperimental;

public class PatternDepth {

	TypeGraphExperimental typeGraph;
	PropertyGraph propertyGraph;
	
	public PatternDepth(TypeGraphExperimental typeGraph, PropertyGraph propertyGraph){
		this.typeGraph = typeGraph;
		this.propertyGraph = propertyGraph;
	}

	
	private void readAKPS(File f) throws Exception{
		FileOutputStream fos;
		if(f.getName().contains("datatype"))
			fos = new FileOutputStream("patterns_datatype_WDepth.txt", true);
		else
			fos = new FileOutputStream("patterns_object_WDepth.txt", true);
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {
			if(!line.equals("")){
				String[] splitted = line.split("##");
				String s = splitted[0];
				String p = splitted[1];
				String o = splitted[2];
				int freq = Integer.parseInt(splitted[3]);
				int numIstanze = Integer.parseInt(splitted[4]);
				
				String outputLine = "";
				
				outputLine += "\n" + s +"$$";
				//se soggetto o oggetto sono esterni setto depth = 1
				Concept sConcept = typeGraph.returnV_graph(new Concept(s));
				if(sConcept ==  null)
					outputLine += 1 + "##";
				else
					outputLine += sConcept.getDepth() + "##";
				
				
				outputLine += p + "$$"; 	
				Property property = propertyGraph.returnV_graph( propertyGraph.createProperty(p));
				if(property == null)
					outputLine += 1 + "##";
				else
					outputLine += property.getDepth() + "##";
				
				
				outputLine += o +"$$";
				Concept oConcept = typeGraph.returnV_graph(new Concept(o));
				if(oConcept ==  null){
					if(o.equals("http://www.w3.org/2000/01/rdf-schema#Literal"))
						outputLine += 0;
					else
						outputLine += 1;
				}
				else
					outputLine += oConcept.getDepth();
				
				outputLine += "##" + freq + "##" + numIstanze;
				
				fos.write(outputLine.getBytes());
			}
		}
		br.close();
		fos.close();
	}
	
	
	public static void main(String[] args) throws Exception{
		Events.summarization();
		
		File folder = new File(args[0]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		
		String patterns_dir = args[1];
		
		
		TypeGraphExperimental typeGraph = new TypeGraphExperimental(ontology);
		PropertyGraph propertyGraph = new PropertyGraph(ontology);
		propertyGraph.linkToTheoreticalProperties();
		
		PatternDepth PD = new PatternDepth(typeGraph, propertyGraph);
		PD.readAKPS(new File( patterns_dir + "/patterns_splitMode_datatype.txt"));
		PD.readAKPS(new File( patterns_dir + "/patterns_splitMode_object.txt"));
	}
	
}
