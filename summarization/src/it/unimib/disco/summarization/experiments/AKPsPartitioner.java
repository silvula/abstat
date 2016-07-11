package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;



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
			if(!line.equals("")){
				int begin = line.indexOf("<");
				int end = line.indexOf(">");
				String tripla = line.substring(begin, end);
				String predicate = tripla.split("##")[1];
				
				String suffix;
				if(akps_file.getName().contains("datatype"))
					suffix = "_datatype";
				else
					suffix = "_object";
				File outputFile = new File (output_directory+"/"+ predicate.substring(predicate.indexOf("://")+3).replace("/", "-")+ suffix + ".txt");
				FileOutputStream fos = new FileOutputStream(outputFile, true);
				fos.write((line+"\n\n").getBytes());
				fos.close();
			}
		}
		br.close();
	}
	
}