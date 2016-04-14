package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.dataset.BulkTextOutput;
import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ExportPropertyVectors {

	public static void main(String[] args) throws Exception {
		final File directory = new File(args[0]);
		final String dataset = args[1];
		String subjectOrObject = args[2];
		
		final LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		final SparqlEndpoint abstat = SparqlEndpoint.abstat();

		
		final String subject;
		if(subjectOrObject.equals("subject")) subject = vocabulary.subject().toString();
		else subject = vocabulary.object().toString();
		
		
		final List<String[]> properties = new SummarizedProperties(vocabulary, abstat).all();
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(final String[] property : properties){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						exportProperty(abstat, directory, dataset, vocabulary, subject, property[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		}
		executor.shutdown();
	    while(!executor.isTerminated()){}
	}

	private static void exportProperty(SparqlEndpoint endpoint, File directory, String dataset, LDSummariesVocabulary vocabulary, String subject, String property) throws Exception {
		String vector = "select ?type ?typeOcc ?propOcc (sum(?occ) as ?akpOcc) where {" +
						   "<"+ property +"> <" + vocabulary.occurrence() + "> ?propOcc ." +
						   "?akp <" + vocabulary.predicate() + "> <"+ property +"> . " +
						   "?akp <" + subject + "> ?ls . " +
						   "?ls <" + RDFS.seeAlso + "> ?type . " +
						   "?ls <" + vocabulary.occurrence() + "> ?typeOcc . " +
						   "?akp <" + vocabulary.occurrence() + "> ?occ . " +
						    "} group by ?type ?typeOcc ?propOcc order by ?type";
		
		ResultSet v = endpoint.execute(vector);
		
		if(!v.hasNext()) return;
		HashMap<String, List<QuerySolution>> solutions = new HashMap<String, List<QuerySolution>>();
		while(v.hasNext()){
			QuerySolution result = v.next();
			Resource type = result.getResource("?type");
			
			if(!solutions.containsKey(type.toString())) solutions.put(type.toString(), new ArrayList<QuerySolution>());
			solutions.get(type.toString()).add(result);
		}
		BulkTextOutput out = new BulkTextOutput(new FileSystemConnector(new File(directory,
				property.toString()
				.replace("http://ld-summaries.org/resource/", "")
				.replace(dataset, "")
				.replace("/datatype-property/", "")
				.replace("/object-property/", "")
				.replace("/", "_"))), 20);
		for(String type : solutions.keySet()){
			long typeOcc = 0;
			long propOcc = 0;
			long akpOcc = 0;
			for(QuerySolution result : solutions.get(type)){
				typeOcc = result.getLiteral("?typeOcc").getLong();
				propOcc += result.getLiteral("?propOcc").getLong();
				akpOcc += result.getLiteral("?akpOcc").getLong();
			}
			out.writeLine(type + "|" + typeOcc + "|" + propOcc + "|" + akpOcc);
		}
		out.close();
	}
}
