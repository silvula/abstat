package it.unimib.disco.summarization.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.BufferedReader;
import java.util.ArrayList;

public class MainCardinality {

	public static void main(String[] args) throws Exception{

		String path = args[0];
		
		//creo la cartella per accogliere i file AKP
		File folderAkps = new File(path+"/Akps");

		//creo la cartella per accogliere i file Property
		File folderProps = new File(path+"/Properties");
		
		//creo gli ArrayList, uno per gli AKP, uno per le Property
		ArrayList<String> listP = new ArrayList<String>();
		ArrayList<String> listAKP = new ArrayList<String>();
		
		try{
			BufferedReader br1 = new BufferedReader(new FileReader(path+"/object-akp_grezzo.txt"));
			String tripla_grezza;
			while ((tripla_grezza = br1.readLine()) != null ){
				if (!(tripla_grezza.equals(""))){
					
					Split split = new Split();
					//metodo per splittare per property
					split.SplitInProperties(tripla_grezza, path, listP);
					//metodo per splittare per AKP
					split.SplitInAKP(tripla_grezza, path, listAKP);

				}
			}
			br1.close();
			
			BufferedReader br2 = new BufferedReader(new FileReader(path+"/datatype-akp_grezzo.txt"));
			while ((tripla_grezza = br2.readLine()) != null ){
				if (!(tripla_grezza.equals(""))){
					
					Split split = new Split();
					//metodo per splittare per property
					split.SplitInProperties(tripla_grezza, path, listP);
					//metodo per splittare per AKP
					split.SplitInAKP(tripla_grezza, path, listAKP);

				}
			}
			br2.close();
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		File globalCard = new File(path+"/globalCardinalities.txt");
		globalCard.createNewFile();
		writeResults(folderProps, listP, globalCard);
		
		File patternCard = new File(path+"/patternCardinalities.txt");
		patternCard.createNewFile();
		writeResults(folderAkps, listAKP, patternCard);
		
		File mapAkps = new File(path+"/mapAkps.txt");
		mapAkps.createNewFile();
		for(String akp : listAKP){
			String index = Integer.toString(listAKP.indexOf(akp));
			String map = "AKP"+index+".txt : "+akp;
			 FileOutputStream fos;
			 try {
				fos = new FileOutputStream(new File(mapAkps.getPath()), true);

				try {
					fos.write((map+"\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
					e.printStackTrace();
			}
		}
			
	}
	
	public static void writeResults(File folder, ArrayList<String> list, File resultFile ){
		for(File f : folder.listFiles()){
			 CalculateCardinality calc = new CalculateCardinality();
			 int[]cardinalities = calc.CalcCardinality(f);
			 String fileName = f.getName();
			 if(fileName.charAt(0)=='A'){
				fileName = fileName.substring(3, fileName.length()-4);
			 }else{
				fileName = fileName.substring(8, fileName.length()-4);
			 }

			 int code = Integer.parseInt(fileName);

			 //il risultato nel file e' : AKP o Property max M-min M-max N-min N 
			 String tmp = list.get(code)+" "+cardinalities[0]+"-"+cardinalities[1]+"-"+cardinalities[2]+"-"+cardinalities[3];
			    
			 FileOutputStream fos;
			 try {
				fos = new FileOutputStream(new File(resultFile.getPath()), true);

				try {
					fos.write((tmp+"\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
					e.printStackTrace();
			}
			 
		}
	}
	


}
