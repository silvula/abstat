package it.unimib.disco.summarization.experiments;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

public class SparqlEndpoint{
	
	public static SparqlEndpoint local(){
		return new SparqlEndpoint("http://localhost:8890");
	}
	
	public static SparqlEndpoint abstat(){
		return new SparqlEndpoint("http://abstat.disco.unimib.it");
	}
	
	public static SparqlEndpoint abstatLocal(){
		return new SparqlEndpoint("192.168.99.100");
	}
	
	public static SparqlEndpoint external(String host){
		return new SparqlEndpoint(host);
	}
	
	private String host;

	private SparqlEndpoint(String host){
		this.host = host;
	}
	
	public ResultSet execute(String query) {
		Query jenaQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution sparqlService = QueryExecutionFactory.sparqlService(host + "/sparql", jenaQuery);
		return sparqlService.execSelect();
	}
}