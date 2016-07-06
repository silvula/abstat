package it.unimib.disco.summarization.export;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.summarization.dataset.ParallelProcessing;
import it.unimib.disco.summarization.experiments.AKPsPartitioner;
import it.unimib.disco.summarization.experiments.TriplesRetriever;

public class SplittedPatternInference {
	
	public static void main(String[] args) throws Exception{
		Events.summarization();
		
		String akps_dir = args[0];
		File akps_splitted_directory = new File(args[1]);
		
		
		new AKPsPartitioner(new File(akps_dir+"/datatype-akp_grezzo.txt"), akps_splitted_directory).partition();
		new AKPsPartitioner(new File(akps_dir+"/object-akp_grezzo.txt"), akps_splitted_directory).partition();
		
		File folder = new File(args[2]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		
		TriplesRetriever retriever = new TriplesRetriever(ontology, new File(akps_dir));
		new ParallelProcessing(akps_splitted_directory, "_datatype.txt").process(retriever);
		new ParallelProcessing(akps_splitted_directory, "_object.txt").process(retriever);

	}
}
