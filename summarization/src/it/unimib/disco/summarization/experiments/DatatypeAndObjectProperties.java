package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.dataset.BulkTextOutput;
import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;
import it.unimib.disco.summarization.ontology.TypeOf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DatatypeAndObjectProperties {

	public static void main(String[] args) throws Exception {
		Events.summarization();

		String dataset = args[0];
		String domainName = args[1];
		String ontologyPath = args[2];
		String directory = args[3];

		TypeOf classifier = new TypeOf(domainName);
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		SparqlEndpoint endpoint = SparqlEndpoint.abstat();

		HashMap<String, OntProperty> properties = ontologyProperties(ontologyPath);
		BulkTextOutput out = new BulkTextOutput(new FileSystemConnector(new File(directory)), 20);

		HashMap<String,ArrayList<String>> datatypeVsObjectProp= new HashMap<String,ArrayList<String>>();

		for(String[] summarizedProperty : new SummarizedProperties(vocabulary, endpoint).all()){

			String datasetProperty = summarizedProperty[0];
			String ontologyProperty = summarizedProperty[1];
			String propertyOccurrence = summarizedProperty[2];

			ArrayList<String> usageDtvsObjeProperty = new ArrayList<String>();


			if(classifier.resource(datasetProperty).equals("external")) continue;
			else{

				if(properties.get(ontologyProperty).isDatatypeProperty()){

					usageDtvsObjeProperty.add(0,"Datatype");

					if(datasetProperty.contains("object-property")){

						usageDtvsObjeProperty.add(1, "0");
						usageDtvsObjeProperty.add(2, propertyOccurrence);
					}
					else{

						usageDtvsObjeProperty.add(1, propertyOccurrence);
						usageDtvsObjeProperty.add(2, "0");
					}
				}
				else if(properties.get(ontologyProperty).isObjectProperty()){

					usageDtvsObjeProperty.add(0,"ObjectType");

					if(datasetProperty.contains("datatype-property")){
						usageDtvsObjeProperty.add(1, propertyOccurrence);
						usageDtvsObjeProperty.add(2, "0");
					}
					else
					{
						usageDtvsObjeProperty.add(1, "0");
						usageDtvsObjeProperty.add(2, propertyOccurrence);
					}
				}
				datatypeVsObjectProp.put(datasetProperty, usageDtvsObjeProperty);
				
				
			}
			


		}

		for (String property: datatypeVsObjectProp.keySet()){

			out.writeLine(property+"\t"+datatypeVsObjectProp.get(property).get(0)+"\t"+datatypeVsObjectProp.get(property).get(1)+"\t"+datatypeVsObjectProp.get(property).get(2));
			out.close();
		}
	}

	private static HashMap<String, OntProperty> ontologyProperties(String ontologyPath) {
		BenchmarkOntology ontology = new BenchmarkOntology(ontologyPath);
		HashMap<String, OntProperty> properties = new HashMap<String, OntProperty>();
		for(OntProperty property : ontology.properties()){
			properties.put(property.toString(), property);
		}
		return properties;
	}
}
