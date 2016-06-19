package it.unimib.disco.summarization.export;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.summarization.experiments.PropertyMinimalizator;

public class CalculatePropertyMinimalization {

	public static void main(String[] args){

		Events.summarization();
		
		
		File folder = new File(args[0]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		String patterns_DirPath = args[1];
		
		PropertyMinimalizator onDatatype= new PropertyMinimalizator(new File(patterns_DirPath + "/datatype-akp_grezzo.txt"),
				new File(patterns_DirPath + "/datatype-akp_grezzo_Updated.txt"),
				new File(patterns_DirPath + "/datatype-akp_Updated.txt"),
				ontology, true);
		
		onDatatype.readAKPs_Grezzo();
		
		
		PropertyMinimalizator onObject= new PropertyMinimalizator(new File(patterns_DirPath + "/object-akp_grezzo.txt"),
				new File(patterns_DirPath + "/object-akp_grezzo_Updated.txt"),
				new File(patterns_DirPath + "/object-akp_Updated.txt"),
				ontology, true);
		
		onObject.readAKPs_Grezzo();
	}
}
