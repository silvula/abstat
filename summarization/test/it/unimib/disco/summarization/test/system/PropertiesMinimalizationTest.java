package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

import org.junit.Test;

public class PropertiesMinimalizationTest {

	String object_minimalAKPs= "";
	
	public PropertiesMinimalizationTest() throws Exception{
		File f = new File(Paths.get("").toAbsolutePath().toString() + "/../data/summaries/system-test/patterns/object-akp.txt");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = reader.readLine();
		while(line!=null){
			object_minimalAKPs += line;
			line = reader.readLine();
		}
		reader.close();
	}
	
	
	
	@Test
	public void shouldContainsTheMinimalizedOnPropertyAKP() throws Exception{
		assertThat(object_minimalAKPs, containsString("http://dbpedia.org/ontology/TelevisionStation##http://dbpedia.org/ontology/locationCity##http://dbpedia.org/ontology/Place##1"));
	}
	
	
	@Test
	public void shouldNOTContainsTheNOTMinimalizedOnPropertyAKP() throws Exception{
		assertThat(object_minimalAKPs, not(containsString("http://dbpedia.org/ontology/TelevisionStation##http://dbpedia.org/ontology/location##http://dbpedia.org/ontology/Place##1")));
	}
	
}
