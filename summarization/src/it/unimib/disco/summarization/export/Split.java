package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class Split {

	//metodo per splittare per AKP
	public static void splitInAKP(String grezza, String folder, ArrayList<String> list)throws Exception{

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
			fos = new FileOutputStream(new File(folder+"/Akps/AKP"+Integer.toString(codeAKP)+".txt"), true);
			fos.write((tripla+"\n").getBytes());
			fos.close();
		}

	}

	//metodo per splittare per Property
	public static void splitInProperties(String grezza, String folder, ArrayList<String> list) throws Exception{

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
		fos = new FileOutputStream(new File(folder+"/Properties/Property"+Integer.toString(codeProperty)+".txt"), true);
		fos.write((tripla+"\n").getBytes());
		fos.close();

	}

	public static void readFromFiles(File file, String path, ArrayList<String> listP, ArrayList<String> listAKP) throws Exception{

		BufferedReader br = new BufferedReader(new FileReader(file));
		String tripla_grezza;
		while ((tripla_grezza = br.readLine()) != null ){
			if (!(tripla_grezza.equals(""))){
				//metodo per splittare per property
				splitInProperties(tripla_grezza, path, listP);
				//metodo per splittare per AKP
				splitInAKP(tripla_grezza, path, listAKP);
			}
		}
		br.close();

	}


}
