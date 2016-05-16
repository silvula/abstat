package it.unimib.disco.summarization.dataset;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class AKPDatatypeCount implements NTripleAnalysis{

	private MinimalTypes types;
	private HashMap<String, Long> akps;
	
	public AKPDatatypeCount(InputFile minimalTypes) throws Exception {
		this.types = new PartitionedMinimalTypes(minimalTypes);
		this.akps = new HashMap<String, Long>();
	}
	
	public HashMap<String, Long> counts() {
		return akps;
	}

	public AKPDatatypeCount track(NTriple triple) {
		String datatype = triple.dataType();
		String subject = triple.subject().toString();
		String property = triple.property().toString();
		
		ArrayList<String> AKPs = new ArrayList<String>();
		for(String type : types.of(subject)){
			String key = type + "##" + property + "##" + datatype;
			AKPs.add(key);	
			if(!akps.containsKey(key)) 
				akps.put(key, 0l);
			akps.put(key, akps.get(key) + 1);
		}
		
		try{
			FileOutputStream fos = new FileOutputStream(new File("DatatypeTriple-AKPs.txt"), true);
			fos.write((AKPs.toString()+"\n\n").getBytes());
			fos.close();
		}
		catch(Exception e){}
		
		
		return this;
	}
}