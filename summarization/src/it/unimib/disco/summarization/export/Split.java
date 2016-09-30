package it.unimib.disco.summarization.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Split {

	//metodo per splittare per AKP
	public void SplitInAKP(String grezza, String folder, ArrayList<String> list){
		
		//isolo la tripla
		String tripla = grezza.substring(grezza.indexOf("<")+1, grezza.lastIndexOf(">"));
		
		String[] AKP = grezza.substring( grezza.indexOf("> [")+3, grezza.lastIndexOf("]")).split(", ");
		
		for(String akp : AKP){
			int codeAKP = 0;
			if(!(list.contains(akp))){
				list.add(akp);
			}
			codeAKP = list.indexOf(akp);	
			
			//creo un file per ogni AKP e ci inserisco la tripla corrispondente
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(new File(folder+"/Akps/AKP"+Integer.toString(codeAKP)+".txt"), true);
				try {
					fos.write((tripla+"\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	//metodo per splittare per Property
	public void SplitInProperties(String grezza, String folder, ArrayList<String> list){
		
		//isolo la tripla
		String tripla = grezza.substring(grezza.indexOf("<")+1, grezza.lastIndexOf(">"));
		//isolo la property
		String property = tripla.split("##")[1];
		
		int codeProperty = 0;
		if(!(list.contains(property))){
			list.add(property);
		}
		codeProperty = list.indexOf(property);	
		
		//creo un file per ogni property e ci inserisco la tripla corrispondente
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(folder+"/Properties/Property"+Integer.toString(codeProperty)+".txt"), true);
			try {
				fos.write((tripla+"\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
			
	}
	
	
}
