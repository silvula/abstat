package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalculateCardinality {

	//metodo per il calcolo delle cardinalità
	public static void calcCardinality(File file, ArrayList<String> list, File resultFile ) throws Exception{
		HashMap<String,Integer> subject = new HashMap<String,Integer>();
		HashMap<String,Integer> object = new HashMap<String,Integer>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		int countS = 1;
		int countO = 1;
		while ((s = br.readLine()) != null ){
			if (!(s.equals(""))){
				String subj = s.split("##")[0];
				String obj = s.split("##")[2];

				if(subject.containsKey(subj)){
					int value = subject.get(subj);
					subject.put(subj, value+1);
				}else{
					subject.put(subj, countS);
				}	    			

				if(object.containsKey(obj)){
					int value = object.get(obj);
					object.put(obj, value+1);
				}else{
					object.put(obj, countO);
				}
			}
		}
		br.close();

		Collection<Integer> valuesS = new ArrayList<Integer>();
		valuesS = subject.values();
		//maxS e max N : numero massimo di oggetti distinti, fissato subject+property
		int maxS = (int)Collections.max(valuesS);
		//minS e min N : numero minimo di oggetti distinti, fissato subject+property
		int minS = (int)Collections.min(valuesS);
		int avgS = avg(valuesS);
		
		Collection<Integer> valuesO = new ArrayList<Integer>();
		valuesO = object.values();
		//maxO e max M : numero massimo di soggetti distinti, fissato property+object
		int maxO = (int)Collections.max(valuesO);
		//minO e min M : numero minimo di soggetti distinti, fissato property+object
		int minO = (int)Collections.min(valuesO);
		int avgO = avg(valuesO);
		
		int[]cardinalities = new int[6];
		cardinalities[0]=maxO;
		cardinalities[1]=avgO;
		cardinalities[2]=minO;
		cardinalities[3]=maxS;
		cardinalities[4]=avgS;
		cardinalities[5]=minS;

		String fileName = file.getName();
		if(fileName.charAt(0)=='A'){
			fileName = fileName.substring(3, fileName.length()-4);
		}else{
			fileName = fileName.substring(8, fileName.length()-4);
		}

		int code = Integer.parseInt(fileName);

		//il risultato nel file e' : AKP o Property maxM-avgM-minM-maxN-avgN-min N 
		String tmp = list.get(code)+" "+cardinalities[0]+"-"+cardinalities[1]+"-"+cardinalities[2]+"-"+cardinalities[3]+"-"+cardinalities[4]+"-"+cardinalities[5];

		FileOutputStream fos;
		fos = new FileOutputStream(new File(resultFile.getPath()), true);
		fos.write((tmp+"\n").getBytes());
		fos.close();
	}


	//metodo per parallelizzare il lavoro in ogni file Property.txt e AKP.txt
	public static void concurrentWork(File folder, final ArrayList<String> list, final File resultFile) throws Exception{
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		for(final File file : folder.listFiles()){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						calcCardinality(file, list, resultFile);
					} catch (Exception e) {
						Events.summarization().error(file, e);
					}
				}
			});
		}
		executor.shutdown();
		while(!executor.isTerminated()){}
	}

	public static int avg(Collection<Integer> values){
		int n = 0;
		int tot = 0;
		for(int el : values){
			tot = tot+el;
			n++;
		}
		float nf = (float)n;
		float totf = (float)tot;
		float avgf = totf/nf;
		int avg = Math.round(avgf);
		return avg;
	}

}
