package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import it.unimib.disco.summarization.test.web.HttpAssert;

public class ResourceOccurrenceAPITest {
	
	@Test
	public void shouldWorkWithAKP() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/resourceOccurrence?URI=http://ld-summaries.org/resource/system-test/AKP/a29de1478d67d6623343d96c13ede06e", 
				containsString("\"freq\": { \"datatype\": \"http://www.w3.org/2001/XMLSchema#int\" , \"type\": \"typed-literal\" , \"value\": \"6\""));
	}
	
	@Test
	public void shouldWorkWithConcept() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/resourceOccurrence?URI=http://ld-summaries.org/resource/system-test/schema.org/Place", 
				containsString("\"freq\": { \"datatype\": \"http://www.w3.org/2001/XMLSchema#int\" , \"type\": \"typed-literal\" , \"value\": \"3\""));
	}
	
	@Test
	public void shouldWorkWithDataType() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/resourceOccurrence?URI=http://ld-summaries.org/resource/system-test/www.w3.org/2001/XMLSchema%23double", 
				containsString("\"freq\": { \"datatype\": \"http://www.w3.org/2001/XMLSchema#int\" , \"type\": \"typed-literal\" , \"value\": \"8\""));
	}
	
	@Test
	public void shouldWorkWithProperty() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/resourceOccurrence?URI=http://ld-summaries.org/resource/system-test/object-property/dbpedia.org/ontology/birthPlace", 
				containsString("\"freq\": { \"datatype\": \"http://www.w3.org/2001/XMLSchema#int\" , \"type\": \"typed-literal\" , \"value\": \"2\""));
	}
	
}
