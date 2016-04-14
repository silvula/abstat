package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SummarizedProperties{
	
	private LDSummariesVocabulary vocabulary;
	private SparqlEndpoint endpoint;

	public SummarizedProperties(LDSummariesVocabulary vocabulary,SparqlEndpoint endpoint) {
		this.vocabulary = vocabulary;
		this.endpoint = endpoint;
	}

	public List<String[]> all() {
		String allProperties = "select distinct ?property ?uri ?occurrence from <" + vocabulary.graph() + "> " + 
								"where { " +
								"?property a <" + vocabulary.property() + "> . " +
								"?property <" + RDFS.seeAlso + "> ?uri ." +
								"?property <" + vocabulary.occurrence() + "> ?occurrence ." +
								"}";
		ResultSet allPropertiesResults = endpoint.execute(allProperties);
		List<String[]> properties = new ArrayList<String[]>();
		while(allPropertiesResults.hasNext()){
			QuerySolution next = allPropertiesResults.next();
			properties.add(new String[]{
					next.getResource("?property").toString(),
					next.getResource("?uri").toString(),
					Integer.toString(next.getLiteral("?occurrence").getInt())
			});
		}
		return properties;
	}
}