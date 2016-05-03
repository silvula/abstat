package it.unimib.disco.summarization.export;
import it.unimib.disco.summarization.dataset.OverallDatatypeRelationsCounting;
import it.unimib.disco.summarization.dataset.ParallelProcessing;
import it.unimib.disco.summarization.experiments.PatternGraph;

import java.io.File;
import java.io.FileOutputStream;

public class ProcessDatatypeRelationAssertions {

	public static void main(String[] args) throws Exception {
		
		Events.summarization();
		
		File sourceDirectory = new File(args[0]);
		File minimalTypesDirectory = new File(args[1]);
		File datatypes = new File(new File(args[2]), "count-datatype.txt");
		File properties = new File(new File(args[2]), "count-datatype-properties.txt");
		File akps = new File(new File(args[2]), "datatype-akp.txt");
		
		OverallDatatypeRelationsCounting counts = new OverallDatatypeRelationsCounting(datatypes, properties, akps, minimalTypesDirectory);
		
		new ParallelProcessing(sourceDirectory, "_dt_properties.nt").process(counts);
		
		/*
		String ontologyDir ="";
		int index = sourceDirectory.getAbsolutePath().lastIndexOf("/");
		ontologyDir = sourceDirectory.getAbsolutePath().substring(0,index) + "/ontology";
		PatternGraph PGMaker = new PatternGraph(ontologyDir);
		
		FileOutputStream fos = new FileOutputStream(new File("DatatypeTriple-AKPs.txt"));
		fos.write(("").getBytes());
		fos.close();

		PGMaker.readTriplesAKPs("DatatypeTriple-AKPs.txt");
		new File("DatatypeTriple-AKPs.txt").delete();
		PGMaker.stampaPatternsSuFile(args[2]+"patterns_datatype.txt");
		//PGMaker.stampaGrafoSuFile(args[2]+"patternGraph_datatype.txt");
		//PGMaker.disegna();
		*/
	    
	    counts.endProcessing();
	    
	}	
}