package it.unimib.disco.summarization.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import it.unimib.disco.summarization.experiments.SparqlEndpoint;

public class AKPsCardinality implements Api {
	public InputStream get(RequestParameters request) throws Exception{
		String dataset  = request.get("dataset");
		
		String queryString = "SELECT (count(?akp) as ?AKPsCardinality) " +
				"FROM <http://ld-summaries.org/"+dataset+"> "+
			    "WHERE { " +
			    "        ?akp <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://ld-summaries.org/ontology/AbstractKnowledgePattern> . " +
			    "} ";
		
		SparqlEndpoint localEndpoint = SparqlEndpoint.local();
		ResultSet results = localEndpoint.execute(queryString);	
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(out, results);
		byte[] data = out.toByteArray();
		ByteArrayInputStream istream = new ByteArrayInputStream(data);
		
		return istream;
	}
}
