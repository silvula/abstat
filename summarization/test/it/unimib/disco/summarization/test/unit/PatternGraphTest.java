package it.unimib.disco.summarization.test.unit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class PatternGraphTest  {
	ArrayList<String> object_minimalAKPs = new ArrayList<String>();
	ArrayList<String> datatype_minimalAKPs = new ArrayList<String>();
	String patterns_object= "";
	String patterns_datatype = "";
	
	
/*	public PatternGraphTest() throws Exception{
		File f = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/patterns_object.txt");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = reader.readLine();
		while(line!=null){
			patterns_object += line;
			line = reader.readLine();
		}
		reader.close();
		
		f = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/patterns_datatype.txt");
		reader = new BufferedReader(new FileReader(f));
		line = reader.readLine();
		while(line!=null){
			patterns_datatype += line;
			line = reader.readLine();
		}
		reader.close();
		
		f = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/object-akp.txt");
		reader = new BufferedReader(new FileReader(f));
		String riga = reader.readLine();
		while(riga!=null){
			object_minimalAKPs.add(riga);
			riga = reader.readLine();
		}
		reader.close();
		
		f = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/datatype-akp.txt");
		reader = new BufferedReader(new FileReader(f));
		
		riga = reader.readLine();
		while(riga!=null){
			datatype_minimalAKPs.add(riga);
			riga = reader.readLine();
		}
		reader.close();
	}
	
	
	@Test
	public void shouldContainsAllObjectMinimalTypePatterns() throws Exception{
		for(String minimalAKP : object_minimalAKPs)
			assertThat(patterns_object, containsString(minimalAKP));
	}
	
	@Test
	public void shouldContainsAllDatatypeMinimalTypePatterns() throws Exception{
		for(String minimalAKP : datatype_minimalAKPs)
			assertThat(patterns_datatype, containsString(minimalAKP));
	}
	
	@Test
	public void datatypePGShouldWorkWithInternalSubject() throws Exception{
		assertThat(patterns_datatype, containsString("http://dbpedia.org/ontology/City##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##1##1"));
		assertThat(patterns_datatype, containsString("http://dbpedia.org/ontology/AdministrativeRegion##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##1##1"));
		assertThat(patterns_datatype, containsString("http://dbpedia.org/ontology/Settlement##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##1##2"));
		assertThat(patterns_datatype, containsString("http://dbpedia.org/ontology/Region##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##0##1"));
		assertThat(patterns_datatype, containsString("http://dbpedia.org/ontology/PopulatedPlace##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##0##3"));
		assertThat(patterns_datatype, containsString("http://dbpedia.org/ontology/Place##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##0##3"));
		assertThat(patterns_datatype, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##0##3"));
		assertThat(patterns_datatype, containsString("http://www.ontologydesignpatterns.org/ont/d0.owl#Location##http://dbpedia.org/ontology/PopulatedPlace/areaTotal##http://dbpedia.org/datatype/squareKilometre##0##3"));
	}

	@Test
	public void objectPGshouldWorkWithBothInternalSubjectAndObject() throws Exception{
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/OfficeHolder##http://dbpedia.org/ontology/residence##http://schema.org/City##1##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/OfficeHolder##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Settlement##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/City##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/OfficeHolder##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/PopulatedPlace##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Settlement##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Agent##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/City##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/OfficeHolder##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Place##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/PopulatedPlace##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Agent##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Settlement##0##1"));
		assertThat(patterns_object, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/City##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/OfficeHolder##http://dbpedia.org/ontology/residence##http://www.ontologydesignpatterns.org/ont/d0.owl#Location##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Person##http://dbpedia.org/ontology/residence##http://www.ontologydesignpatterns.org/ont/d0.owl#Location##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Agent##http://dbpedia.org/ontology/residence##http://www.ontologydesignpatterns.org/ont/d0.owl#Location##0##1"));
		assertThat(patterns_object, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/residence##http://www.ontologydesignpatterns.org/ont/d0.owl#Location##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/OfficeHolder##http://dbpedia.org/ontology/residence##http://www.w3.org/2002/07/owl#Thing##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Person##http://dbpedia.org/ontology/residence##http://www.w3.org/2002/07/owl#Thing##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Agent##http://dbpedia.org/ontology/residence##http://www.w3.org/2002/07/owl#Thing##0##1"));
		assertThat(patterns_object, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/residence##http://www.w3.org/2002/07/owl#Thing##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Place##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Agent##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Place##0##1"));
		assertThat(patterns_object, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Place##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Agent##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/PopulatedPlace##0##1"));
		assertThat(patterns_object, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/PopulatedPlace##0##1"));
		assertThat(patterns_object, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Settlement##0##1"));
		 
	}
	


	
	@Test
	public void objectPGshouldWorkWithExternalSubject() throws Exception{
		assertThat(patterns_object, containsString("http://schema.org/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/City##1##1"));
		assertThat(patterns_object, containsString("http://schema.org/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Settlement##0##1"));
		assertThat(patterns_object, containsString("http://schema.org/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/PopulatedPlace##0##1"));
		assertThat(patterns_object, containsString("http://schema.org/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Place##0##1"));
		assertThat(patterns_object, containsString("http://schema.org/Person##http://dbpedia.org/ontology/residence##http://www.ontologydesignpatterns.org/ont/d0.owl#Location##0##1"));
		assertThat(patterns_object, containsString("http://schema.org/Person##http://dbpedia.org/ontology/residence##http://www.w3.org/2002/07/owl#Thing##0##1"));
	}
	
	
	@Test
	public void objectPGshouldWorkWithExternalObject() throws Exception{
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/OfficeHolder##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Wikidata:Q532##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Person##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Wikidata:Q532##0##1"));
		assertThat(patterns_object, containsString("http://dbpedia.org/ontology/Agent##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Wikidata:Q532##0##1"));
		assertThat(patterns_object, containsString("http://www.w3.org/2002/07/owl#Thing##http://dbpedia.org/ontology/residence##http://dbpedia.org/ontology/Wikidata:Q532##0##1"));
	}*/
}