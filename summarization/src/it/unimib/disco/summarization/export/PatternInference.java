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
		
		
		
		PatternGraph PGMakerDatatype = new PatternGraph();
		PGMakerDatatype.createTypeGraph(ontology);
		
		/*FileOutputStream fos = new FileOutputStream(new File("DatatypeTriple-AKPs.txt"));
		fos.write(("").getBytes());
		fos.close();*/

		PGMakerDatatype.readTriplesAKPs("DatatypeTriple-AKPs.txt");
		new File("DatatypeTriple-AKPs.txt").delete();
		PGMakerDatatype.stampaPatternsSuFile(args[1]+"patterns_datatype.txt");
		
		
		
		
		PatternGraph PGMakerObject = new PatternGraph();
		PGMakerObject.createTypeGraph(ontology);
		
		/*FileOutputStream fos2 = new FileOutputStream(new File("ObjectTriple-AKPs.txt"));
		fos2.write(("").getBytes());
		fos2.close();*/

		PGMakerObject.readTriplesAKPs("ObjectTriple-AKPs.txt");
		new File("ObjectTriple-AKPs.txt").delete();
		PGMakerObject.stampaPatternsSuFile(args[1]+"patterns_object.txt");
	}
		
}
