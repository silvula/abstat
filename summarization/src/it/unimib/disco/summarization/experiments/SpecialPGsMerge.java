package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import it.unimib.disco.summarization.export.Events;



public class SpecialPGsMerge {
	private File ontology;
	private File outputDir;
	
	public SpecialPGsMerge(File ontology, File outputDir){
		this.ontology = ontology;
		this.outputDir = outputDir;
	}
	
	
	public void process(File file) throws Exception {
		double startTime = System.currentTimeMillis();
		ArrayList<File> listofFiles = new ArrayList<File>( Arrays.asList( file.listFiles() ) );
		
		String type;
		if(file.getName().contains("datatype"))  type = "datatype"; 
		else  type = "object";
		
		PGSpecial PGS = new PGSpecial(ontology, type);
		int maxDepth =-1;
		
		for(File f : listofFiles){
			String nome = f.getName();
			int currentDepth = Integer.parseInt( nome.substring(nome.indexOf("Depth")+5, nome.lastIndexOf("_")) );
			if( currentDepth > maxDepth )
				maxDepth = currentDepth;
		}
		for(int i=0; i<=maxDepth; i++){
			Iterator<File> itr = listofFiles.iterator();
			while(itr.hasNext()){
				File f = itr.next();
				String nome = f.getName();
				int currentDepth = Integer.parseInt( nome.substring(nome.indexOf("Depth")+5, nome.lastIndexOf("_")) );
				if(currentDepth == i){
					PGS.addPatterns(f);
					itr.remove();
				}
			}
		}
		String outputFile_name = file.getName().replace("_"+type, "_merged" + "_"+type + ".txt");
		
		PGS.stampaPatternsSuFile(outputDir + "/" + outputFile_name, outputDir + "/../HEADpatterns-"+type+"_parts/"+ file.getName());
		
		double endTime   = System.currentTimeMillis();
		Events.summarization().info( (endTime - startTime)/1000 +"s  ..." + file.getName()+ "     SPECIAL"); 	
	}

	

	public void mergeHeadPatterns(String akps_dir, String type) throws Exception{
		ArrayList<Pattern> HEADpatterns = new ArrayList<Pattern>();
		for(File file : new File(akps_dir+"/HEADpatterns-"+ type + "_parts").listFiles()){
			String line;
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if(!line.equals("")){
					String[] splitted = line.split("##");
					String s = splitted[0];
					String p = splitted[1];
					String o = splitted[2];
					int freq = Integer.parseInt(splitted[3]);
					int numIstanze = Integer.parseInt(splitted[4]);
						

					
					Pattern pattern = new Pattern(new Concept(s), p, new Concept(o));
					pattern.setFreq(freq);
					pattern.setInstances(numIstanze);
					
					if(HEADpatterns.contains(pattern)){
						Pattern original_p = HEADpatterns.get(HEADpatterns.indexOf(pattern));
						pattern.setInstances(pattern.getInstances() + original_p.getInstances());
						HEADpatterns.remove(original_p);
						HEADpatterns.add(pattern);
					}
					else
						HEADpatterns.add(pattern);
				}
			}
			br.close();
		}
		
		FileOutputStream fos = new FileOutputStream(new File(akps_dir+"/HEADpatterns_" + type + ".txt"), true);
		for(Pattern HEADpattern : HEADpatterns)
			fos.write( (HEADpattern.getSubj()+"##"+ HEADpattern.getPred() + "##"+HEADpattern.getObj()+"##"+ HEADpattern.getFreq()+"##"+ HEADpattern.getInstances()+"\n").getBytes()  );
		fos.close();
	}
	
	    
}
	

