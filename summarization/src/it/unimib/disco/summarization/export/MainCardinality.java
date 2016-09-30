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

		//File homedir = new File(System.getProperty("user.home"));
		
		//String path =homedir+"/workspace";
		String path = args[0];
		
		//creo la cartella per accogliere i file AKP
		File folderAkps = new File(path+"/Akps");
		//boolean created = folderAkps.mkdir();
		//cleanFolder(folderAkps, created);
		
		//creo la cartella per accogliere i file Property
		File folderProps = new File(path+"/Properties");
		//created = folderProps.mkdir();
		//cleanFolder(folderProps, created);
		
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
			
		
		File globalCard = createFile(path+"/globalCardinalities.txt");
		//File globalCard = new File(path+"/globalCardinalities.txt");
		writeResults(folderProps, listP, globalCard);
		
		File patternCard = createFile(path+"/patternCardinalities.txt");
		//File patternCard = new File(path+"/patternCardinalities.txt");
		writeResults(folderAkps, listAKP, patternCard);
			
	}
	
	public static File createFile(String fileName){

		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		else{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	/*public static void cleanFolder(File folder, boolean created){
		if(!created){
			if(folder.exists()){
				File files[]=folder.listFiles();
				for(File f : files){
					f.delete();
				}
			}
			folder.mkdir();
		}
	}*/
	
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
