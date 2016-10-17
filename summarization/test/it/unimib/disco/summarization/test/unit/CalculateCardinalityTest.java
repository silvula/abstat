package it.unimib.disco.summarization.test.unit;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import it.unimib.disco.summarization.export.CalculateCardinality;

public class CalculateCardinalityTest extends TestWithTemporaryData{

	@Test
	public void testCalcCardinalityProperties() throws Exception{
		ArrayList<String> listP = new ArrayList<String>();
		listP.add("http://dbpedia.org/ontology/birthPlace");
		listP.add("http://dbpedia.org/ontology/capital");
		
		String content0 = "http://dbpedia.org/resource/Barack_Obama##http://dbpedia.org/ontology/birthPlace##http://dbpedia.org/resource/Hawaii"+ "\n" +"http://dbpedia.org/resource/Barack_Obama##http://dbpedia.org/ontology/birthPlace##http://dbpedia.org/resource/Honolulu";
		String content1 = "http://dbpedia.org/resource/Hawaii##http://dbpedia.org/ontology/capital##http://dbpedia.org/resource/Honolulu";
		
		CalculateCardinality.calcCardinality(temporary.namedFile(content0, "Property"+listP.indexOf("http://dbpedia.org/ontology/birthPlace")+".txt"), listP, temporary.namedFile("", "globalCardinalities.txt"));
		CalculateCardinality.calcCardinality(temporary.namedFile(content1, "Property"+listP.indexOf("http://dbpedia.org/ontology/capital")+".txt"), listP, temporary.namedFile("", "globalCardinalities.txt"));
		
		BufferedReader br = new BufferedReader(new FileReader(temporary.path()+"/globalCardinalities.txt"));
		String s;
		while ((s = br.readLine()) != null ){
			
			String property = s.split(" ")[0];
			boolean isProperty = false;
			for(String p : listP){
				if(p.equals(property)){
					isProperty = true;
				}
			}
			assertTrue(isProperty);
			
			String cardinalities = s.split(" ")[1];
			int countSeparator = 0;
			for(int i = 0; i < cardinalities.length(); i++){
				if(cardinalities.charAt(i)=='-'){
					countSeparator++;
				}
			}
			assertThat(countSeparator, equalTo(5));
			
		}
		br.close();
		
	}

	@Test
	public void testCalcCardinalityAkps() throws Exception{
		ArrayList<String> listAKP = new ArrayList<String>();
		listAKP.add("http://schema.org/Person##http://dbpedia.org/ontology/birthPlace##http://schema.org/Place");
		listAKP.add("http://schema.org/Place##http://dbpedia.org/ontology/capital##http://schema.org/Place");
		
		String content0 = "http://dbpedia.org/resource/Barack_Obama##http://dbpedia.org/ontology/birthPlace##http://dbpedia.org/resource/Hawaii"+ "\n" +"http://dbpedia.org/resource/Barack_Obama##http://dbpedia.org/ontology/birthPlace##http://dbpedia.org/resource/Honolulu";
		String content1 = "http://dbpedia.org/resource/Hawaii##http://dbpedia.org/ontology/capital##http://dbpedia.org/resource/Honolulu";
		
		CalculateCardinality.calcCardinality(temporary.namedFile(content0, "Akp"+listAKP.indexOf("http://schema.org/Person##http://dbpedia.org/ontology/birthPlace##http://schema.org/Place")+".txt"), listAKP, temporary.namedFile("", "patternCardinalities.txt"));
		CalculateCardinality.calcCardinality(temporary.namedFile(content1, "Akp"+listAKP.indexOf("http://schema.org/Place##http://dbpedia.org/ontology/capital##http://schema.org/Place")+".txt"), listAKP, temporary.namedFile("", "patternCardinalities.txt"));
		
		BufferedReader br = new BufferedReader(new FileReader(temporary.path()+"/patternCardinalities.txt"));
		String s;
		while ((s = br.readLine()) != null ){
			
			String akp = s.split(" ")[0];
			boolean isAKP = false;
			for(String a : listAKP){
				if(a.equals(akp)){
					isAKP = true;
				}
			}
			assertTrue(isAKP);
			
			String cardinalities = s.split(" ")[1];
			int countSeparator = 0;
			for(int i = 0; i < cardinalities.length(); i++){
				if(cardinalities.charAt(i)=='-'){
					countSeparator++;
				}
			}
			assertThat(countSeparator, equalTo(5));
			
		}
		br.close();
		
	}
	
}
