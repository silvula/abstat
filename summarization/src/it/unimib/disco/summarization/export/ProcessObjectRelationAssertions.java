package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.OverallObjectRelationsCounting;
import it.unimib.disco.summarization.dataset.ParallelProcessing;
import it.unimib.disco.summarization.experiments.PatternGraph;

import java.io.File;
import java.io.FileOutputStream;

public class ProcessObjectRelationAssertions {
	
	public static void main(String[] args) throws Exception {
		
		Events.summarization();
		
		File sourceDirectory = new File(args[0]);
		File minimalTypesDirectory = new File(args[1]);
		File properties = new File(new File(args[2]), "count-object-properties.txt");
		File akps = new File(new File(args[2]), "object-akp.txt");
		
		OverallObjectRelationsCounting counts = new OverallObjectRelationsCounting(properties, akps, minimalTypesDirectory);
		
		new ParallelProcessing(sourceDirectory, "_obj_properties.nt").process(counts);
		
		
		/*String ontologyDir ="";
		int index = sourceDirectory.getAbsolutePath().lastIndexOf("/");
		ontologyDir = sourceDirectory.getAbsolutePath().substring(0,index) + "/ontology";
		PatternGraph PGMaker = new PatternGraph(ontologyDir);
		
		FileOutputStream fos = new FileOutputStream(new File("ObjectTriple-AKPs.txt"));
		fos.write(("").getBytes());
		fos.close();

		PGMaker.readTriplesAKPs("ObjectTriple-AKPs.txt");
		new File("ObjectTriple-AKPs.txt").delete();
		PGMaker.stampaPatternsSuFile(args[2]+"patterns_object.txt");
		//PGMaker.stampaGrafoSuFile(args[2]+"patternGraph_object.txt");
		//counts.patternGraphMaker.disegna();
		*/

		
	    counts.endProcessing();
	}	
}