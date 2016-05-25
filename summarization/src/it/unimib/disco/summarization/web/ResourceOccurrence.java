package it.unimib.disco.summarization.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import it.unimib.disco.summarization.experiments.SparqlEndpoint;

public class ResourceOccurrence implements Api{
	public InputStream get(RequestParameters request) throws Exception{
		String URI  = request.get("URI");
		
		String queryString = "SELECT ?freq " +
			    "WHERE { " +
			    "        <"+ URI +"> <http://ld-summaries.org/ontology/occurrence> ?freq" +
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
