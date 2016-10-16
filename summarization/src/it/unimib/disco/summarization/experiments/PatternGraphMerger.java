package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import it.unimib.disco.summarization.export.Events;


public class PatternGraphMerger {
	private File ontology;
	private File outputDir;
	
	public PatternGraphMerger(File ontology, File outputDir){
		this.ontology = ontology;
		this.outputDir = outputDir;
	}
	
	
	public void process(File dir) throws Exception {
		double startTime = System.currentTimeMillis();
		
		//inizializzazione PG
		String type;
		if(dir.getName().contains("datatype"))  type = "datatype"; 
		else  type = "object";
		PGSpecial PGS = new PGSpecial(ontology, type);
		
		//itera su ogni file presente in dir, li salva in listofFiles. Ricava la maxDepth tra tutti i file.
		ArrayList<File> listofFiles = new ArrayList<File>( Arrays.asList( dir.listFiles() ) );
		int maxDepth =-1;
		for(File f : listofFiles){
			String nome = f.getName();
			int currentDepth = Integer.parseInt( nome.substring(nome.indexOf("Depth")+5, nome.lastIndexOf("_")) );
			if( currentDepth > maxDepth )
				maxDepth = currentDepth;
		}
		
		//manda i file a addPatterns() in ordine crescente di depth
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
		
		//dopo costruzione del PG speciale stampo in file separati i pattern definitivi e gli HEADpattern
		PGS.stampaPatternsSuFile(outputDir + "/patterns_splitMode_"+ type +".txt", outputDir + "/HEADpatterns_"+type+"_unmerged.txt");
		
		Events.summarization().info( (System.currentTimeMillis() - startTime)/1000 +"s  ..." + dir.getName()+ "     MERGE"); 	
	}

	

	//gli HEADpatterns fino a questo punto non sono definitivi, quindi vanno processati per ricavare quelli definitivi
	public void mergeHeadPatterns(String type) throws Exception{
		ArrayList<Pattern> HEADpatterns = new ArrayList<Pattern>();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(new File(outputDir+"/HEADpatterns_"+type+"_unmerged.txt")));
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
		
		//scriviamo su file gli HEADpatterns definitivi
		FileOutputStream fos = new FileOutputStream(new File(outputDir+"/patterns_splitMode_"+type+".txt"), true);
		for(Pattern HEADpattern : HEADpatterns)
			fos.write( (HEADpattern.getSubj()+"##"+ HEADpattern.getPred() + "##"+HEADpattern.getObj()+"##"+ HEADpattern.getFreq()+"##"+ HEADpattern.getInstances()+"\n").getBytes()  );
		fos.close();
	}
	
	    
}
	

