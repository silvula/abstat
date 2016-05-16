package it.unimib.disco.summarization.test.system;

import it.unimib.disco.summarization.test.system.StringMatcher;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.hamcrest.Matchers.allOf;
import java.util.ArrayList;
import org.junit.Test;
import it.unimib.disco.summarization.test.web.HttpAssert;

public class QueryWithParamsTest {
	
	@Test
	public void ShouldReturnJson() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&format=json",
				startsWith("{\n  \"head\":"));
	}
	
	@Test
	public void ShouldReturnRDF() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&format=rdf",
				startsWith("<rdf:RDF"));
	}
	
	@Test
	public void JsonShouldShowSubjectsPredicatesObjects() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&format=json",
				allOf(containsString("\"subj\": {"),
					  containsString("\"pred\": {"),
					  containsString("\"obj\": {")));	
	}

	
	@Test
	public void JsonShouldShowOccurrences() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&format=json",
				allOf(containsString("\"akp_frequency\": {"),
					  containsString("\"subj_frequency\": {"),
					  containsString("\"pred_frequency\": {"),
					  containsString("\"obj_frequency\": {")));				     
	}
	
	@Test
	public void RDFShouldShowSubjectsPredicatesObjectsOccurrences() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&format=rdf",
				allOf(containsString("<rdf:subject"),
					  containsString("<rdf:predicate"),	
					  containsString("<rdf:object"),
					  containsString("<j.0:occurrence")));
	}
	
	@Test
	public void RDFShouldDescribeSubjectsPredicatesObjects() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&limit=100&format=rdf",
				allOf(StringMatcher.containsStringNTimes("<rdf:Description rdf:about=\"http://ld-summaries.org/resource/system-test/dbpedia.org/", 5),
					  StringMatcher.containsStringNTimes("<rdf:Description rdf:about=\"http://ld-summaries.org/resource/system-test/schema.org/", 2),
					  StringMatcher.containsStringNTimes("<rdf:Description rdf:about=\"http://ld-summaries.org/resource/system-test/datatype-property/",  10),
					  StringMatcher.containsStringNTimes("<rdf:Description rdf:about=\"http://ld-summaries.org/resource/system-test/object-property/",  2),
					  StringMatcher.containsStringNTimes("<rdf:Description rdf:about=\"http://ld-summaries.org/resource/system-test/www.w3.org/",  3)));			     
	}
	

	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	
	@Test
	public void ShouldWorkWithNoSubjectNoPredicateNoObject() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test",
				StringMatcher.containsStringNTimes("\"akp\":",  10)); 
	}
	
	@Test
	public void ShouldWorkWithOneSubject() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&limit=100",
				allOf(containsString("0530efaaae323350ec7ff78a7aa6ba17"),
				      containsString("616d8e9153f5e3d8a571da0f42d33786"),
				      containsString("b2db6267552f6b143d9e6fd03b19ac5b"),
				      containsString("a805a463ab8f6c75717db50060111be6"),
				      containsString("f391c882967ad638f44551cf5af391e8")));
	}
	
	@Test
	public void ShouldWorkWithOnePredicate() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&predicate=http://ld-summaries.org/resource/system-test/datatype-property/xmlns.com/foaf/0.1/name&limit=100",
				allOf(containsString("0b9b3400bfd9866446ef4831f939e57d"),
				      containsString("32155a6ec85f8a47cc08e64b81f14ec9"),
				      containsString("a29de1478d67d6623343d96c13ede06e"),
				      containsString("f1cd692e47e879e502523323a3e98e65")));
	}
	
	@Test
	public void ShouldWorkWithOneObject() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&objectType=http://ld-summaries.org/resource/system-test/www.w3.org/2001/XMLSchema%23double&limit=100",
				allOf(containsString("118212a12d8a74578e1bd6d9c328cb37"),
				      containsString("43510d70807bdb7af700e05f288330b7"),
				      containsString("8d9ebb9f65be0c7568e4734f6f689446"),
				      containsString("b1102b1a96be840651c0fff913b5fcd3"),
				      containsString("ffb6f37e41e4dafd6f536f83273106fc")));
	}
	
	@Test
	public void ShouldWorkWithManySubjects() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/dbpedia.org/ontology/OfficeHolder,"
				                                 + "http://ld-summaries.org/resource/system-test/schema.org/Place,"
				                                 + "http://ld-summaries.org/resource/system-test/dbpedia.org/ontology/City&limit=100",
				allOf(containsString("411b34cecefe41219eb76b4b47090116"),
				      containsString("d2519e017eb65fb830e3ade0d7bc5925"),
				      containsString("616d8e9153f5e3d8a571da0f42d33786"),
				      containsString("8d9ebb9f65be0c7568e4734f6f689446"),
				      containsString("9c48265862e9a7206513cbd89065b9f2"),
				      containsString("4ef2b72e609b72d2c8e4115f6dd6b1ba")));
	}
	
	@Test
	public void ShouldWorkWithManyPredicates() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&predicate=http://ld-summaries.org/resource/system-test/datatype-property/dbpedia.org/ontology/areaLand,"
				                                + "http://ld-summaries.org/resource/system-test/datatype-property/dbpedia.org/ontology/birthDate,"
				                                + "http://ld-summaries.org/resource/system-test/datatype-property/dbpedia.org/ontology/maximumElevation&limit=100",
				allOf(containsString("f9af633417bfa9f80236046b2b5bd54a"),
				      containsString("e0204f826da9dc854ca484661dc594ea"),
				      containsString("916944a4fc4062b11c7c5ac811568fdb"),
				      containsString("f210e475321f650136586130e5386a52"),
				      containsString("f513e16dc752bb24235868f1f2a1d7c1")));
	}
	
	@Test
	public void ShouldWorkWithManyObjects() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&objectType=http://ld-summaries.org/resource/system-test/www.w3.org/2000/01/rdf-schema%23Literal,"
												+ "http://ld-summaries.org/resource/system-test/dbpedia.org/ontology/Wikidata:Q532,"
												+ "http://ld-summaries.org/resource/system-test/dbpedia.org/ontology/Settlement&limit=100",
				allOf(containsString("a29de1478d67d6623343d96c13ede06e"),
				      containsString("a3296156e41a55c0fd27d81c5eddf6f9"),
				      containsString("71a97e6c542f4fd97eb92800e2acf3b3"),
				      containsString("bfe2aad274e6d7afd8556f5914bfc737"),
				      containsString("c579121e9a063d71c8c35e616ef5ff19"),
				      containsString("e0ac0275b2afc52dd53a4d36ade64016")));
	}
	
	@Test
	public void ShouldWorkWithOneSubjectAndManyPredicates() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&predicate=http://ld-summaries.org/resource/system-test/object-property/dbpedia.org/ontology/isPartOf,"
												+ "http://ld-summaries.org/resource/system-test/object-property/dbpedia.org/ontology/capital,"
												+ "http://ld-summaries.org/resource/system-test/datatype-property/xmlns.com/foaf/0.1/name&limit=100",
				allOf(containsString("27c0c09abfda4f249dd0ccf0081689e3"),
				      containsString("f12c4dc1f6eb0466d388b32fd9de29a6"),
				      containsString("b65632dd56df6404bf4e4ee6b862bd0e"),
				      containsString("9fbabb5f46493976d8276df44a4086cd"),
				      containsString("854f487795ba71930f93f1968f2e18ec")));
	}
	
	@Test
	public void ShouldWorkWithManySubjectsPredicates() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place,"
												+ "http://ld-summaries.org/resource/system-test/dbpedia.org/ontology/Settlement"
												+ "&predicate=http://ld-summaries.org/resource/system-test/object-property/dbpedia.org/ontology/isPartOf,"
												+ "http://ld-summaries.org/resource/system-test/datatype-property/www.w3.org/2003/01/geo/wgs84_pos%23long,"
												+ "http://ld-summaries.org/resource/system-test/datatype-property/www.w3.org/2003/01/geo/wgs84_pos%23lat,"
												+ "http://ld-summaries.org/resource/system-test/datatype-property/xmlns.com/foaf/0.1/name&limit=100",
				allOf(containsString("f12c4dc1f6eb0466d388b32fd9de29a6"),
				      containsString("b1c0183969c0b3fb35e6c3a11532c103"),
				       containsString("f391c882967ad638f44551cf5af391e8"),
				       containsString("9ad2a506658005b1e1c0e823b11e0a01"),
				      containsString("854f487795ba71930f93f1968f2e18ec"),
				      containsString("a1792e5711c7ac1207b7d5a7d5a993c3")));
	}
	
	
	@Test
	public void ShouldWorkWithObjectWithoutOccurrences() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&limit=100",
				containsString("AKP/b2db6267552f6b143d9e6fd03b19ac5b"));			      
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	@Test
	public void limitShouldWorkWithNoInput() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place", 
				StringMatcher.containsStringNTimes("\"akp\":", 10));
	}
	
	
	@Test
	public void limitShouldWorkWithValidInput() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&limit=12", 
				StringMatcher.containsStringNTimes("\"akp\":", 12));
	}
	
	@Test
	public void limitShouldWorkWithInvalidInput1() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&limit=2", 
				StringMatcher.containsStringNTimes("\"akp\":", 10));
		
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&limit=200", 
				StringMatcher.containsStringNTimes("\"akp\":", 18));
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@Test
	public void shouldRespectIncreasingOrder() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		list.add("\"value\": \"3\"");
		list.add("\"value\": \"6\"");
		
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&rankingFunction=akp_frequency,asc&limit=100", 
				stringContainsInOrder(list));
	}
	
	@Test
	public void shouldRespectDescendingOrder() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		list.add("\"value\": \"6\"");
		list.add("\"value\": \"3\"");
		
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&rankingFunction=akp_frequency,desc&limit=100", 
				stringContainsInOrder(list));
	}
	

	@Test
	public void RDFshouldRespectIncreasingOrder() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		list.add("\"http://www.w3.org/2001/XMLSchema#int\">2<");
		list.add("\"http://www.w3.org/2001/XMLSchema#int\">3<");
		list.add("\"http://www.w3.org/2001/XMLSchema#int\">6<");
		
		new HttpAssert("http://localhost").body("/api/v1/queryWithParams?dataset=system-test&subjectType=http://ld-summaries.org/resource/system-test/schema.org/Place&rankingFunction=akp_frequency,asc&limit=100&format=rdf", 
				stringContainsInOrder(list));
	}
	
}
