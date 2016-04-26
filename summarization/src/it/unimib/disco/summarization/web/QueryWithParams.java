package it.unimib.disco.summarization.web;

import com.hp.hpl.jena.query.*;
import java.io.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import it.unimib.disco.summarization.experiments.SparqlEndpoint;

public class QueryWithParams implements Api{

	public InputStream get(RequestParameters request) throws Exception{
		String dataset  = request.get("dataset");
		String s = request.get("subjectType");
		String p = request.get("predicate");
		String o = request.get("objectType");
		String limit = request.get("limit");
		String rankFunc = request.get("rankingFunction");
		
		String[] subjectType = null;
		String[] predicate = null;
		String[] objectType = null;
		String[] rankingFunction  = null;
		if(s!=null)  subjectType = s.split(",");
		if(p!=null)  predicate = p.split(",");
		if(o!=null)  objectType = o.split(",");
		if(rankFunc!=null) rankingFunction = rankFunc.split(",");
		
		
		String queryString = buildQuery(dataset, subjectType, predicate, objectType, limit, rankingFunction);
		SparqlEndpoint localEndpoint = SparqlEndpoint.local();
		ResultSet results = localEndpoint.execute(queryString);

		ByteArrayOutputStream out = new ByteArrayOutputStream();	
		ResultSetFormatter.outputAsJSON(out, results);
		byte[] data = out.toByteArray();
		ByteArrayInputStream istream = new ByteArrayInputStream(data);
		

		return istream;
	}
	
	
	private String buildQuery(String dataset, String[] subjectType, String[] predicate, String[] objectType, String limit, String[] rankingFunction){
		String query;
		
		//comune a tutti i casi
		query = "SELECT DISTINCT  ?s ?occurrences " +
				"FROM <http://ld-summaries.org/"+dataset+"> "+
			    "WHERE { " +
				"       ?s  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://ld-summaries.org/ontology/AbstractKnowledgePattern>. " + 
				"		?s  <http://ld-summaries.org/ontology/occurrence> ?occurrences. ";
		
		
		//gestisce i subjectType
		if(subjectType!=null)
			for(int i=0; i<subjectType.length; i++){
				if(i!=0)
					query += " UNION ";
				query += "{  ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> <" + subjectType[i] + ">.} ";			
			}
		
		//gestisce i predicate
		if(predicate!=null)
			for(int i=0; i<predicate.length; i++){
				if(i!=0)
					query += " UNION ";
				query += "{  ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <" + predicate[i] + ">.} ";
			}
			
		//gestisce gli objectType
		if(objectType!=null)
			for(int i=0; i<objectType.length; i++){
				if(i!=0)
					query += " UNION ";
				query += "{  ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <" + objectType[i] + ">.} ";				
			}
		

		query += " }";
		
		//gestisce l'ordine dell'output
		if(rankingFunction!=null){
			if(rankingFunction[0].equals("frequency")){
				if(rankingFunction[1].equals("desc"))
					query += "ORDER BY DESC(?occurrences)";
				else
					query += "ORDER BY ASC(?occurrences)";
			}
		}
		
		//gestisce limiti di numerosità output
		if(limit!=null){
			int lim = Integer.parseInt(limit);
			if(lim >=10 && lim<=100)
				query += "LIMIT "+limit;
			else if(lim<10)
				query += " LIMIT 10";
			else if(lim>100)
				query += " LIMIT 100";
		}
		else 
			query += " LIMIT 10";
	
		
		
		return query;
	}
}