package it.unimib.disco.summarization.web;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import it.unimib.disco.summarization.experiments.SparqlEndpoint;

public class QueryWithParams implements Api{

	public InputStream get(RequestParameters request) throws Exception{
		String dataset  = request.get("dataset");
		String s = request.get("subjectType");
		String p = request.get("predicate");
		String o = request.get("objectType");
		String limit = request.get("limit");
		String rankFunc = request.get("rankingFunction");
		String format = request.get("format");
		
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
		
		if(format!=null && format.equals("rdf")){
			ResultSet results2 = localEndpoint.execute(queryString);
			String outputRDF = buildRDFOutput(results, results2);
			return IOUtils.toInputStream(outputRDF);
		}
		else{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(out, results);
			byte[] data = out.toByteArray();
			ByteArrayInputStream istream = new ByteArrayInputStream(data);
			return istream;
		}
	}
	
	
	private String buildQuery(String dataset, String[] subjectType, String[] predicate, String[] objectType, String limit, String[] rankingFunction){
		String query;
		
		//comune a tutti i casi
		query = "SELECT DISTINCT  ?akp ?subj ?pred ?obj ?akp_frequency ?subj_frequency ?pred_frequency ?obj_frequency " +
				"FROM <http://ld-summaries.org/"+dataset+"> "+
			    "WHERE { " +
				"       ?akp  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://ld-summaries.org/ontology/AbstractKnowledgePattern>. " + 
				"       ?akp  <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ?subj. " +
				"       ?akp  <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> ?pred. " +
				"       ?akp  <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> ?obj. " +
				"       ?akp  <http://ld-summaries.org/ontology/occurrence> ?akp_frequency. " +
				"       OPTIONAL{ ?subj  <http://ld-summaries.org/ontology/occurrence> ?subj_frequency. } " +
				"       OPTIONAL{ ?pred  <http://ld-summaries.org/ontology/occurrence> ?pred_frequency. } " +
				"       OPTIONAL{ ?obj  <http://ld-summaries.org/ontology/occurrence> ?obj_frequency. } " ;
		
		
		//gestisce i subjectType
		if(subjectType!=null)
			for(int i=0; i<subjectType.length; i++){
				if(i!=0)
					query += " UNION ";
				query += "{  ?akp <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> <" + subjectType[i] + ">.} ";			
			}
		
		//gestisce i predicate
		if(predicate!=null)
			for(int i=0; i<predicate.length; i++){
				if(i!=0)
					query += " UNION ";
				query += "{  ?akp <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> <" + predicate[i] + ">.} ";
			}
			
		//gestisce gli objectType
		if(objectType!=null)
			for(int i=0; i<objectType.length; i++){
				if(i!=0)
					query += " UNION ";
				query += "{  ?akp <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> <" + objectType[i] + ">.} ";				
			}
		

		query += " }";
		
		//gestisce l'ordine dell'output
		if(rankingFunction!=null){
			if(rankingFunction.length>1){
				if(rankingFunction[1].equals("desc"))
					query += "ORDER BY DESC(?"+rankingFunction[0]+")";
				else
					query += "ORDER BY ASC(?"+rankingFunction[0]+")";
			}
			else
				query += "ORDER BY DESC(?"+rankingFunction[0]+")";
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
	
	
	/* Questo metodo fa ciò che dovrebbe fare ResultSetFormatter.outputAsJSON ma in modo corretto*/
	private String buildRDFOutput(ResultSet res, ResultSet res2) throws Exception{
		//ricavo stringa con output in json
		ByteArrayOutputStream out = new ByteArrayOutputStream();	
		ResultSetFormatter.outputAsJSON(out, res);
		String outputJson = out.toString();
		out.close();
		
		
		//costruisco modello RDF con i risultati della query
		Model model = ModelFactory.createDefaultModel();
		Property hasSubj = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject");
		Property hasPred = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate");
		Property hasObj = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#object");
		Property hasOccurrences = model.createProperty("http://ld-summaries.org/ontology/occurrence");
		
		while(res2.hasNext()){
			QuerySolution soln = res2.nextSolution();
			Resource akp = model.createResource(soln.getResource("akp").getURI());
			Resource subj = model.createResource(soln.getResource("subj").getURI());
			Resource pred = model.createResource(soln.getResource("pred").getURI());
			Resource obj = model.createResource(soln.getResource("obj").getURI());
			if(soln.get("obj_frequency")!=null)
				obj.addProperty(hasOccurrences,soln.get("obj_frequency"));
			if(soln.get("pred_frequency")!=null)
				pred.addProperty(hasOccurrences,soln.get("pred_frequency"));
			if(soln.get("subj_frequency")!=null)
				subj.addProperty(hasOccurrences,soln.get("subj_frequency"));
			
			akp.addProperty(hasOccurrences,soln.get("akp_frequency"));
			akp.addProperty(hasObj, soln.getResource("obj"));
			akp.addProperty(hasPred, soln.getResource("pred"));
			akp.addProperty(hasSubj, soln.getResource("subj"));
		}
		
		
		//ricavo stringa con output in RDF
		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		model.write(out2);
		String outputRDF = out2.toString();
		out2.close();
		
		
		//ottengo l'ordine secondo cui devo ordinare gli AKP nell'output RDF
		ArrayList<String> akpsOrdinati = new ArrayList<String>();
		while(outputJson.contains("AKP/")){
			int i = outputJson.indexOf("AKP/")+4;
			outputJson = outputJson.substring(i);
			int f = outputJson.indexOf("\" }");
			akpsOrdinati.add(outputJson.substring(0,f));		
		}
		
		//spezzo outputRDF in tutte le sue description e salvo in descriptionsRDF 
		String inizio = outputRDF.substring(0, outputRDF.indexOf("<rdf:Description"));
		ArrayList<String> descriptionsRDF = new ArrayList<String>();
		while(outputRDF.contains("<rdf:Description")){
			int i = outputRDF.indexOf("<rdf:Description");
			int f = outputRDF.indexOf("</rdf:Description>") + 18;
			descriptionsRDF.add(outputRDF.substring(i,f));
			outputRDF = outputRDF.substring(f);
		}
		
		//Metto in cima a outputRDFFinal tutte le description di AKPs, nell'ordine specificato da akpsOrdinati
		String outputRDFFinal = inizio;
		for( String akpJson : akpsOrdinati){
			Iterator<String> it = descriptionsRDF.iterator();
			while(it.hasNext()){
				String descriptRDF =it.next();
				if(descriptRDF.contains(akpJson)){
					outputRDFFinal += descriptRDF + "\n  ";
					it.remove();
				}
			}
		}
		
		//Infine tutte le descrizioni che non riguardano akps
		for(String descriptRDF : descriptionsRDF)
				outputRDFFinal += descriptRDF + "\n  ";
		outputRDFFinal = outputRDFFinal.substring(0,outputRDFFinal.length()-2)+"</rdf:RDF>";
		

		/*FileOutputStream fos = new FileOutputStream(new File("outputRDF.txt"));
		fos.write(outputRDFFinal.getBytes());*/
		
		return outputRDFFinal;
		//return outputRDF;
	}
	
	
}