package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;


import it.unimib.disco.summarization.export.Events;

public class AKPsPartitioner {

	private File akps_file;
	private File output_directory;
	
	public AKPsPartitioner(File input_f, File output_d){
		akps_file = input_f;
		output_directory = output_d;
	}
	
	
	public void partition() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(akps_file));
		String line;
		while ((line = br.readLine()) != null) {
			String[] tripleParts = line.split("##");
			String predicate = tripleParts[1];
			File outputFile = new File (output_directory+"/"+ predicate.substring(predicate.indexOf("://")+3).replace("/", "_"));
			FileOutputStream fos = new FileOutputStream(outputFile, true);
			fos.write((line+"\n").getBytes());
			fos.close();
		}
	}
	
	public static void main(String[] args) throws Exception{
		//Events.summarization();
		
		File akps_file = new File(args[0]);
		File output_directory = new File(args[1]);
		AKPsPartitioner partitioner = new AKPsPartitioner(akps_file, output_directory);
		partitioner.partition();
	}
	
}