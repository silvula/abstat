package it.unimib.disco.summarization.dataset;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AKPObjectCount implements NTripleAnalysis{

	private MinimalTypes types;
	private HashMap<String, Long> akps;

	public AKPObjectCount(MinimalTypes minimalTypes) throws Exception {
		this.types = minimalTypes;
		this.akps = new HashMap<String, Long>();
	}
	
	public HashMap<String, Long> counts() {
		return akps;
	}

	public AKPObjectCount track(NTriple triple) {
		String object = triple.object().toString();
		String subject = triple.subject().toString();
		String property = triple.property().toString();

		ArrayList<String> AKPs = new ArrayList<String>();
		for (String subjectType : types.of(subject)) {
			for(String objectType : types.of(object)){
				String key = subjectType + "##" + property + "##" + objectType;
				AKPs.add(key);
				if (!akps.containsKey(key))
					akps.put(key, 0l);
				akps.put(key, akps.get(key) + 1);
			}
		}
		
		try{
			FileOutputStream fos = new FileOutputStream(new File("ObjectTriple-AKPs.txt"), true);
			fos.write((AKPs.toString()+"\n\n").getBytes());
			fos.close();
			}
			catch(Exception e){}
			
		return this;
	}
}