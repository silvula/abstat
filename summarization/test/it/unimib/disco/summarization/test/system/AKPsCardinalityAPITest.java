package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import it.unimib.disco.summarization.test.web.HttpAssert;

public class AKPsCardinalityAPITest {
	
	@Test
	public void shouldWork() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/AKPsCardinality?dataset=system-test", containsString("\"AKPsCardinality\": { \"datatype\": \"http://www.w3.org/2001/XMLSchema#integer\" , \"type\": \"typed-literal\" , \"value\": \"177\""));
	}
}
