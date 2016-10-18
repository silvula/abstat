package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;

public class CardinalitiesTest {
	
	ArrayList<String> akps = new ArrayList<String>();
	ArrayList<String> props = new ArrayList<String>();
	String patternCardinalities = "";
	String globalCardinalities = "";
	HashMap<String, String> resultGlobalCard = new HashMap<String, String>();
	HashMap<String, String> resultPatternCard = new HashMap<String, String>();
	
	public CardinalitiesTest() throws Exception{
		
		File globalCard = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/globalCardinalities.txt");
		BufferedReader br1 = new BufferedReader(new FileReader(globalCard));
		String cardP;
		while ((cardP = br1.readLine()) != null ){
			if (!(cardP.equals(""))){
				globalCardinalities = globalCardinalities + cardP + " ";
				String pr = cardP.split(" ")[0];
				String card = cardP.split(" ")[1];
				resultGlobalCard.put(pr, card);
			}
		}
		br1.close();
		
		File patternCard = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/patternCardinalities.txt");
		BufferedReader br2 = new BufferedReader(new FileReader(patternCard));
		String cardAKP;
		while ((cardAKP = br2.readLine()) != null ){
			if (!(cardAKP.equals(""))){
				patternCardinalities = patternCardinalities + cardAKP + " ";
				String pa = cardAKP.split(" ")[0];
				String card = cardAKP.split(" ")[1];
				resultPatternCard.put(pa, card);
			}
		}
		br2.close();
		
		File objAkp = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/object-akp_grezzo.txt");
		BufferedReader br3 = new BufferedReader(new FileReader(objAkp));
		String string;
		while ((string = br3.readLine()) != null ){
			if (!(string.equals(""))){
				
				String tripla = string.substring(string.indexOf("<")+1, string.lastIndexOf(">"));
				
				String property = tripla.split("##")[1];
				if(!(props.contains(property)))
					props.add(property);
				
				String[] AKP = string.substring( string.indexOf("> [")+3, string.lastIndexOf("]")).split(", ");
				for(String akp : AKP){
					if(!(akps.contains(akp))){
						akps.add(akp);
					}
				}
			}
		}
		br3.close();
		
		File datatypeAkp = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/datatype-akp_grezzo.txt");
		BufferedReader br4 = new BufferedReader(new FileReader(datatypeAkp));
		while ((string = br4.readLine()) != null ){
			if (!(string.equals(""))){
				
				String tripla = string.substring(string.indexOf("<")+1, string.lastIndexOf(">"));
				
				String property = tripla.split("##")[1];
				if(!(props.contains(property)))
					props.add(property);
				
				String[] AKP = string.substring( string.indexOf("> [")+3, string.lastIndexOf("]")).split(", ");
				for(String akp : AKP){
					if(!(akps.contains(akp))){
						akps.add(akp);
					}
				}
			}
		}
		br4.close();
		
	}


	@Test
	public void shouldContainAllAKP() throws Exception {
		for(String akp : akps)
			assertThat(patternCardinalities, containsString(akp));
	}
	
	@Test
	public void shouldContainAllProperties() throws Exception {
		for(String prop : props)
			assertThat(patternCardinalities, containsString(prop));
	}
	
	
	@Test
	public void shouldCheckGlobalCardinalities() throws Exception {
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/areaLand 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://www.w3.org/2003/01/geo/wgs84_pos#long 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/birthPlace 1-1-1-2-2-2"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/areaWater 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/birthDate 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/areaTotal 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/maximumElevation 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/residence 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/PopulatedPlace/populationDensity 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/PopulatedPlace/areaTotal 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://www.w3.org/2003/01/geo/wgs84_pos#lat 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://xmlns.com/foaf/0.1/name 1-1-1-2-2-2"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/isPartOf 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/minimumElevation 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/locationCity 1-1-1-1-1-1"));
		assertThat(globalCardinalities, containsString("http://dbpedia.org/ontology/capital 1-1-1-1-1-1"));
	}
	
	@Test
	public void shouldCheckPatternCardinalities() throws Exception {
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/isPartOf##http://schema.org/Place 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/capital##http://schema.org/Place 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://dbpedia.org/ontology/TelevisionStation##http://dbpedia.org/ontology/locationCity##http://dbpedia.org/ontology/Place 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/minimumElevation##http://www.w3.org/2001/XMLSchema#double 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://xmlns.com/foaf/0.1/Person##http://xmlns.com/foaf/0.1/name##http://www.w3.org/2000/01/rdf-schema#Literal 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://www.w3.org/2003/01/geo/wgs84_pos#lat##http://www.w3.org/2001/XMLSchema#float 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/PopulatedPlace/populationDensity##http://dbpedia.org/datatype/inhabitantsPerSquareKilometre 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://xmlns.com/foaf/0.1/Person##http://dbpedia.org/ontology/residence##http://schema.org/Place 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/maximumElevation##http://www.w3.org/2001/XMLSchema#double 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://xmlns.com/foaf/0.1/Person##http://dbpedia.org/ontology/birthDate##http://www.w3.org/2001/XMLSchema#date 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/areaWater##http://www.w3.org/2001/XMLSchema#double 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://xmlns.com/foaf/0.1/Person##http://dbpedia.org/ontology/birthPlace##http://schema.org/Place 1-1-1-2-2-2"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://www.w3.org/2003/01/geo/wgs84_pos#long##http://www.w3.org/2001/XMLSchema#float 1-1-1-1-1-1"));
		assertThat(patternCardinalities, containsString("http://schema.org/Place##http://dbpedia.org/ontology/areaLand##http://www.w3.org/2001/XMLSchema#double 1-1-1-1-1-1"));
	}
	
	@Test
	public void shouldCheckMinGlobalCardinalities(){
		Collection<String> card = resultGlobalCard.values();
		for(String c : card){
			int minM = Integer.parseInt(c.split("-")[2]);
			int minN = Integer.parseInt(c.split("-")[5]);
			boolean checkMin = false;
			if(minM>=1 && minN>=1){
				checkMin = true;
			}
			assertTrue(checkMin);
		}
	}
	
	@Test
	public void shouldCheckMinPatternCardinalities(){
		Collection<String> card = resultPatternCard.values();
		for(String c : card){
			int minM = Integer.parseInt(c.split("-")[2]);
			int minN = Integer.parseInt(c.split("-")[5]);
			boolean checkMin = false;
			if(minM>=1 && minN>=1){
				checkMin = true;
			}
			assertTrue(checkMin);
		}
	}
	
	@Test
	public void shouldCheckMaxCardinalities(){
		for(String p : props){
			String cardP = resultGlobalCard.get(p);
			ArrayList<String> singleP = new ArrayList<String>();
			for(String a : akps){
				String property = a.split("##")[1];
				if(p.equals(property)){
					singleP.add(a);
				}
			}
			ArrayList<String> cardA = new ArrayList<String>();
			for(String a : singleP){
				String sCard = resultPatternCard.get(a);
				cardA.add(sCard);
			}
			
			int maxMP = Integer.parseInt(cardP.split("-")[0]);
			int maxNP = Integer.parseInt(cardP.split("-")[3]);
			
			for(String c : cardA){
				int maxMA = Integer.parseInt(c.split("-")[0]);
				int maxNA = Integer.parseInt(c.split("-")[3]);
				boolean checkMax = false;
					if(maxMP>=maxMA && maxNP>=maxNA){
						checkMax=true;
					}
				assertTrue(checkMax);
			}
			
		}
	}
	
	@Test
	public void shouldCheckAvgPatternCard(){
		Collection<String> card = resultPatternCard.values();
		for(String c : card){
			int maxM = Integer.parseInt(c.split("-")[0]);
			int avgM = Integer.parseInt(c.split("-")[1]);
			int minM = Integer.parseInt(c.split("-")[2]);
			int maxN = Integer.parseInt(c.split("-")[3]);
			int avgN = Integer.parseInt(c.split("-")[4]);
			int minN = Integer.parseInt(c.split("-")[5]);
			boolean checkAvgM = false;
			if(avgM>=minM && maxM>=avgM){
				checkAvgM = true;
			}
			assertTrue(checkAvgM);
			
			boolean checkAvgN = false;
			if(avgN>=minN && maxN>=avgN){
				checkAvgN = true;
			}
			assertTrue(checkAvgN);
		}
	}
	
	@Test
	public void shouldCheckAvgGlobalCard(){
		Collection<String> card = resultGlobalCard.values();
		for(String c : card){
			int maxM = Integer.parseInt(c.split("-")[0]);
			int avgM = Integer.parseInt(c.split("-")[1]);
			int minM = Integer.parseInt(c.split("-")[2]);
			int maxN = Integer.parseInt(c.split("-")[3]);
			int avgN = Integer.parseInt(c.split("-")[4]);
			int minN = Integer.parseInt(c.split("-")[5]);
			boolean checkAvgM = false;
			if(avgM>=minM && maxM>=avgM){
				checkAvgM = true;
			}
			assertTrue(checkAvgM);
			
			boolean checkAvgN = false;
			if(avgN>=minN && maxN>=avgN){
				checkAvgN = true;
			}
			assertTrue(checkAvgN);
		}
	}

}
