package it.unimib.disco.summarization.export;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.summarization.dataset.ParallelProcessing;
import it.unimib.disco.summarization.experiments.AKPsPartitioner;
import it.unimib.disco.summarization.experiments.TopPatternGraph;
import it.unimib.disco.summarization.experiments.TriplesRetriever;

public class SplittedPatternInference {
	
	public static void main(String[] args) throws Exception{
		
		Events.summarization();

		String akps_dir = args[0];
		File akps_Grezzo_splitted_dir = new File(args[1]);
		File headAKPs_splitted_dir = new File(args[2]);

		File folder = new File(args[3]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();

		
//-----------------------------------------------------------      Base of patternGraph      -------------------------------------------------------------------------------		
		
	
		AKPsPartitioner splitter = new AKPsPartitioner(ontology);
		splitter.AKPs_Grezzo_partition(new File(akps_dir+"/datatype-akp_grezzo.txt"), akps_Grezzo_splitted_dir, "_datatype");
		splitter.AKPs_Grezzo_partition(new File(akps_dir+"/object-akp_grezzo.txt"), akps_Grezzo_splitted_dir, "_object");
		
		
		TriplesRetriever retriever = new TriplesRetriever(ontology, new File(akps_dir));
		new ParallelProcessing(akps_Grezzo_splitted_dir, "_datatype.txt").process(retriever);
		new ParallelProcessing(akps_Grezzo_splitted_dir, "_object.txt").process(retriever);
		
		
		retriever = null;       ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////77
		
//-----------------------------------------------------------      Top of patternGraph      -------------------------------------------------------------------------------		
		
		splitter.AKPsPartion(new File(akps_dir + "/headPatterns_datatype.txt"), headAKPs_splitted_dir, "_datatype");
		splitter.AKPsPartion(new File(akps_dir + "/headPatterns_object.txt"), headAKPs_splitted_dir, "_object");
		
		splitter = null;         //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////77
		
		TopPatternGraph topPGDatatype = new TopPatternGraph(ontology, "datatype", new File(akps_dir + "/patterns_splitMode_datatype.txt"));
		topPGDatatype.readAKPs(headAKPs_splitted_dir, "_datatype.txt");
		
		topPGDatatype = null;  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////77
		
		TopPatternGraph topPGObject = new TopPatternGraph(ontology, "object",  new File(akps_dir + "/patterns_splitMode_object.txt"));
		topPGObject.readAKPs(headAKPs_splitted_dir, "_object.txt");
		
	}
	
}
