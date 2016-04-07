package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.dataset.BulkTextOutput;
import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;
import it.unimib.disco.summarization.ontology.TypeOf;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class DomainsAndRanges {

	public static void main(String[] args) throws Exception {
		Events.summarization();
		
		String dataset = args[0];
		String domain = args[1];
		String ontologyPath = args[2];
		String directory = args[3];
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		SparqlEndpoint endpoint = SparqlEndpoint.abstatLocal();
		TypeOf classifier = new TypeOf(domain);
		BenchmarkOntology ontology = new BenchmarkOntology(ontologyPath);
		
		List<Resource[]> allProperties = new SummarizedProperties(vocabulary, endpoint).all();
		HashMap<String, OntProperty> properties = new HashMap<String, OntProperty>();
		for(OntProperty property : ontology.properties()){
			properties.put(property.toString(), property);
		}
		
		BulkTextOutput out = new BulkTextOutput(new FileSystemConnector(new File(directory)), 20);
		
		
		for(Resource[] property : allProperties){
			String uri = property[1].toString();
			String type = classifier.resource(property[0].toString());
			OntProperty ontProperty = properties.get(uri.toString());
			Inferred inferred = new Inferred(vocabulary, endpoint).of(uri);
			
			String[] line = new String[]{
					escaped(uri),
					escaped(type),
					escaped(domainsOf(ontProperty)),
					escaped(rangesOf(ontProperty)),
					escaped(join(inferred.domains())),
					escaped(inferred.domains().size() + ""),
					escaped(join(inferred.ranges())),
					escaped(inferred.ranges().size() + ""),
			};
			
			out.writeLine(StringUtils.join(line, "\t"));
			
		}
		out.close();
	}

	private static String join(HashSet<String> types){
		return StringUtils.join(types, ", ");
	}
	
	private static String domainsOf(OntProperty property){
		if(property == null) return null;
		return "" + property.getDomain();
	}
	
	private static String rangesOf(OntProperty property){
		if(property == null) return null;
		return "" + property.getRange();
	}
	
	private static String escaped(String uri) {
		return "\"" + uri + "\"";
	}
}
