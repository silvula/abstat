package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class CalculateCardinality {

	public int[] CalcCardinality(File file){
	    HashMap<String,Integer> subject = new HashMap<String,Integer>();
	    HashMap<String,Integer> object = new HashMap<String,Integer>();
	    
	    try{
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
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
	    	
	    Collection<Integer> valuesS = new ArrayList<Integer>();
	    valuesS = subject.values();
	    //maxS e max N : numero massimo di oggetti distinti, fissato subject+property
	    int maxS = (int)Collections.max(valuesS);
	    //minS e min N : numero minimo di oggetti distinti, fissato subject+property
	    int minS = (int)Collections.min(valuesS);
	    	
	    Collection<Integer> valuesO = new ArrayList<Integer>();
	    valuesO = object.values();
	    //maxO e max M : numero massimo di soggetti distinti, fissato property+object
	    int maxO = (int)Collections.max(valuesO);
	    //minO e min M : numero minimo di soggetti distinti, fissato property+object
	    int minO = (int)Collections.min(valuesO);
	    int[]cardinalities = new int[4];
	    cardinalities[0]=maxO;
	    cardinalities[1]=minO;
	    cardinalities[2]=maxS;
	    cardinalities[3]=minS;
	    return cardinalities;
	    
	}
	
}
