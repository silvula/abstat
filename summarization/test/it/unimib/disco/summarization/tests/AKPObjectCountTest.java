package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.utility.AKPObjectCount;

import org.junit.Test;

public class AKPObjectCountTest {

	@Test
	public void emptyContent() throws Exception {
		
		AKPObjectCount count = new AKPObjectCount(new TextInputTestDouble());
		
		assertThat(count.counts().size(), equalTo(0));
	}
	
	@Test
	public void singleAKP() throws Exception {
		
		TextInputTestDouble types = new TextInputTestDouble().withLine("1##entity##type");
		
		AKPObjectCount count = new AKPObjectCount(types).track(new TripleBuilder().withSubject("entity")
																				.withProperty("property")
																				.withObject("entity")
																.asTriple());
		
		assertThat(count.counts().get("type##property##type"), equalTo(1l));
	}
}
