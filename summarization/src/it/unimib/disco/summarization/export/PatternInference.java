package it.unimib.disco.summarization.export;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.summarization.experiments.PatternGraph;

public class PatternInference {

	public static void main(String[] args) throws Exception{
	
		Events.summarization();
		
		File folder = new File(args[0]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		
			
		PatternGraph PGMakerDatatype = new PatternGraph(ontology, "datatype");
		PGMakerDatatype.readTriplesAKPs(args[1]+"datatype-akp_grezzo.txt");
		PGMakerDatatype.stampaPatternsSuFile(args[1]+"patterns_datatype.txt");

		
		
		PatternGraph PGMakerObject = new PatternGraph(ontology, "object");
		PGMakerObject.readTriplesAKPs(args[1]+"object-akp_grezzo.txt");
		PGMakerObject.stampaPatternsSuFile(args[1]+"patterns_object.txt");
	}
		
}