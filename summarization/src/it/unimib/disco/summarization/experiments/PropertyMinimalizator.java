package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.PropertyGraph;

public class PropertyMinimalizator {

	PropertyGraph propGraph;
	File akp_grezzo;
	File akp_grezzo_Update;
	File akp_Update;
	String object_akp_grezzo;
	HashMap<String, ArrayList<Pattern>> tripleWithAKPs; //come chiavi conterrà solo triple con stesso soggetto
	ArrayList<Pattern>  AKPsSet; 
	HashMap<String, Integer> propertiesRemoved;
	boolean updateAkpsGrezzo_File;
	
	public PropertyMinimalizator(File akp_grezzo, File akp_grezzo_Update, File akp_Update, File ontology, boolean updateAkpsGrezzo_File){
		this.akp_grezzo = akp_grezzo;
		this.akp_grezzo_Update = akp_grezzo_Update;
		this.akp_Update = akp_Update;
		tripleWithAKPs =  new HashMap<String, ArrayList<Pattern>>();
		AKPsSet = new ArrayList<Pattern>();
		propGraph = new PropertyGraph(ontology);
		propertiesRemoved = new HashMap<String, Integer>();
		this.updateAkpsGrezzo_File = updateAkpsGrezzo_File;
	}
	
	
	public void readAKPs_Grezzo() {
		try{
			BufferedReader br = new BufferedReader(new FileReader(akp_grezzo));
			String line;
			String subjGroup = "";
			
			while ((line = br.readLine()) != null) {
				if(!line.equals("")){
					
					//estae la tripla da line 
					int index = line.indexOf(" [");
					String triplaCorrente = line.substring(0, index);   
					String currentSubj = triplaCorrente.split("##")[0].substring(1);
					//System.out.println(currentSubj);
					line = line.substring(index+1);
					
					//ottengo gli AKPs della tripla corrente
					line = line.substring(1, line.length()-1);
					String[] stringAKPs = line.split(", ");  
					ArrayList<Pattern> AKPs = new ArrayList<Pattern>();
		    		
					for(int i=0; i<stringAKPs.length;i++){
						String[] splitted = stringAKPs[i].split("##");
						String s = splitted[0];
						String p = splitted[1];
						String o = splitted[2];
						AKPs.add( new Pattern( new Concept(s), p, new Concept(o)));
					}
					if(currentSubj.equals(subjGroup) || tripleWithAKPs.isEmpty()){
						tripleWithAKPs.put(triplaCorrente, AKPs);
						//System.out.println(tripla);
						subjGroup = currentSubj;
					}
					else{
						startMinimalization();
						tripleWithAKPs = new HashMap<String, ArrayList<Pattern>>();
						tripleWithAKPs.put(triplaCorrente, AKPs);
						subjGroup = currentSubj;
						
					}
				}
			}
			startMinimalization();  //dopo aver letto l'ultima tripla minimilizzo l'ultimo carico di triple
			scriviAKPs();
			
			if(akp_grezzo.getPath().contains("datatype"))
				updateCountFile(new File(akp_grezzo.getParent()+"/count-datatype-properties.txt"), new File("count-datatype-properties_Updated.txt"));
			else
				updateCountFile(new File(akp_grezzo.getParent()+"/count-object-properties.txt"), new File("count-object-properties_Updated.txt"));
			br.close();	
		}
		catch(Exception e){
			Events.summarization().error(akp_grezzo, e);
		}  
		
		
	}
	
	//viene chiamato quando un gruppo di triple con stesso soggetto è "pronto" cioè quando non ce ne sono altre con stesso sogg.
	private void startMinimalization(){
	
		HashMap<String, ArrayList<String>> simili = new HashMap<String, ArrayList<String>>(); //mappa oggetto o1 con triple di tripleWithAkps con oggetto=o1 
		for(String tripla: tripleWithAKPs.keySet()){
			
			int index = tripla.lastIndexOf("##");
			String obj = tripla.substring(index+1, tripla.length()-2);
			if(simili.containsKey(obj))
				simili.get(obj).add(tripla);
			else{
				ArrayList<String> list = new ArrayList<String>();
				list.add(tripla);
				simili.put(obj, list);
			}
		}
		
		minimalizza(simili);		
		if(this.updateAkpsGrezzo_File)
			aggiorna_akp_grezzo();
		aggiornaInsiemeAKPs();
	}
	
	
	private void minimalizza(HashMap<String, ArrayList<String>> simili){
		//per ogni gruppo
		for(String key : simili.keySet()){	
			if(simili.get(key).size()>1){   //minimalizzi solo se hai almeno 2 triple adatte
				ArrayList<String> minimalPropertyTriples = new ArrayList<String>();
				//per ogni tripla del gruppo
				for(String tripla : simili.get(key)){  
					if(minimalPropertyTriples.isEmpty())
						minimalPropertyTriples.add(tripla);
					else{
						String prop = tripla.substring(tripla.indexOf("##")+2,tripla.lastIndexOf("##"));
						ListIterator<String>  listIt = minimalPropertyTriples.listIterator();
						while(listIt.hasNext()){
							String tripla2 = listIt.next();
							String minimalProp = tripla2.substring(tripla2.indexOf("##")+2,tripla2.lastIndexOf("##"));
							
							//se prop non è superProperty di minimalProp è per forza property minimale e potrebbe esse anche subProeprty di minimalProp
							if(propGraph.pathsBetween(minimalProp, prop).isEmpty()){ 
								if(!propGraph.pathsBetween(prop, minimalProp).isEmpty()){  
									listIt.remove();  
									trackRemovedProperties(minimalProp); 
									tripleWithAKPs.remove(tripla2); //minimalizzo
									System.out.println("eliminato:  "+ tripla2);
								}
								listIt.add(tripla);
							}
							else{
								trackRemovedProperties(prop);
								tripleWithAKPs.remove(tripla); //minimalizzo
								System.out.println("eliminato:  "+ tripla);
							}
						}
					}
				}
			}	
		}
	}
	
	
	
	
	private void aggiorna_akp_grezzo() {
		try{
			FileOutputStream fos = new FileOutputStream(akp_grezzo_Update, true);
			for(String key : tripleWithAKPs.keySet()){
				String line = key + " [";
				for(Pattern akp :  tripleWithAKPs.get(key) )
					line += akp.getSubj()+"##"+akp.getPred()+"##"+akp.getObj()+", ";
				line = line.substring(0, line.length()-2) + "]\n\n";
				fos.write(line.getBytes());
			}
			fos.close();
		}catch(Exception e){
			Events.summarization().error(akp_grezzo_Update, e);
			
		};
	}
	
	
	private void aggiornaInsiemeAKPs(){
		for(String tripla : tripleWithAKPs.keySet()){
			for(Pattern pattern : tripleWithAKPs.get(tripla)){
				//ATTENZIONE in questo caso pattern è una copia di quello in lista
				if(AKPsSet.contains(pattern)){                                         
					Pattern originalPattern = AKPsSet.get(AKPsSet.indexOf(pattern)); //ottengo il pattern originale 
					originalPattern.setFreq(originalPattern.getFreq()+1);   //modifico freq del pattern originale
				}
				else{
					pattern.setFreq(1);
					AKPsSet.add(pattern);
				}	
			}
		}
		
	}
	
	private void scriviAKPs() {
		try{
			FileOutputStream fos = new FileOutputStream(akp_Update, true);
			for(Pattern akp : AKPsSet)
				fos.write((akp.getSubj().getURI()+"##"+akp.getPred()+"##"+akp.getObj().getURI()+"##"+akp.getFreq()+"\n").getBytes());
			fos.close();
		}catch(Exception e){
			Events.summarization().error(akp_Update, e);
		}
		
	}
	
	public void trackRemovedProperties(String property){
		if(this.propertiesRemoved.containsKey(property))
			propertiesRemoved.put(property, propertiesRemoved.get(property)+1);
		else
			propertiesRemoved.put(property,1);

	}
	
	
	
	private void updateCountFile(File inputFile, File outputFile) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		FileOutputStream fos = new FileOutputStream(outputFile);
		String line;
		while ((line = br.readLine()) != null) {
			if(!line.equals("")){
				String[] riga = line.split("##");
				if(propertiesRemoved.containsKey(riga[0])){
					System.out.println("----------"+riga[0]);
					int newFreq = Integer.parseInt(riga[1]) - propertiesRemoved.get(riga[0]);
					if(newFreq>0)
						fos.write((riga[0] + "##" + newFreq + "\n").getBytes());
					propertiesRemoved.remove(riga[0]);
				}
				else
					fos.write((line + "\n").getBytes());
			}
		}
		br.close();
		fos.close();
	}
	
}
