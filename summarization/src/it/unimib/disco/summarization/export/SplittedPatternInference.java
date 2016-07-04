package it.unimib.disco.summarization.export;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.summarization.dataset.ParallelProcessing;
import it.unimib.disco.summarization.experiments.AKPsPartitioner;
import it.unimib.disco.summarization.experiments.TriplesRetriever;

public class SplittedPatternInference {
	
	public static void main(String[] args) throws Exception{
		
		
		File akps_file = new File(args[0]);
		File akps_splitted_directory = new File(args[1]);
		
		//File akps_splitted_directory = new File("/home/renzo/partizioni");
		
		AKPsPartitioner partitioner = new AKPsPartitioner(akps_file, akps_splitted_directory);
		partitioner.partition();
		
		//File folder = new File("/home/renzo/rAlvaPrincipe/abstat/data/datasets/system-test/ontology/dbpedia_2014.owl");
		File folder = new File(args[2]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		
		TriplesRetriever retriever = new TriplesRetriever(ontology);
		new ParallelProcessing(akps_splitted_directory, ".txt").process(retriever);
	}
}
