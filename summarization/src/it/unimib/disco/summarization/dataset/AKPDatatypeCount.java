package it.unimib.disco.summarization.dataset;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import it.unimib.disco.summarization.export.Events;


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

		write_akps_grezzo(subject, property, triple.object().toString(), AKPs.toString());

		return this;
	}
	
	
	public void write_akps_grezzo(String subject, String property, String object, String akpsList){
		try{
			FileOutputStream fos = new FileOutputStream("datatype-akp_grezzo.txt", true);
			//il replaceAll serve in caso la tripla abbia \n e quindi venga interpretato come "a capo". 
			//Ciò da problemi in multithreading: il primo  e il secondo pezzo sono scritti non contiguamente! probabilmente perchè assegnati a thread diversi
			String riga = "<"+subject+"##"+ property+"##"+ object.replaceAll("\n", " ")+"> " + akpsList;
			riga +="\n\n";
			fos.write(riga.getBytes());
			fos.close();
		}
		catch(Exception e){
			Events.summarization().error("datatype-akp_grezzo.txt", e);
		}
	}
}