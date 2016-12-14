package it.unimib.disco.summarization.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;


public class WriteInstancesToRDF {


	public static void main (String args []) throws IOException{
		
		String csvFilePath = args[0];
		String outputFilePath = args[1];
		String dataset = args[2];
//		String domain = args[3];
		String type = args[4];
		
		Model model = ModelFactory.createDefaultModel();
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(model, dataset);
		
		for (Row row : readCSV(csvFilePath)){

			try{
				Resource globalSubject = model.createResource(row.get(Row.Entry.SUBJECT));
				Property globalPredicate = model.createProperty(row.get(Row.Entry.PREDICATE));
				Resource globalObject = vocabulary.selfOrUntyped(row.get(Row.Entry.OBJECT));
				Literal statistic2 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE2)));

				Resource localSubject = vocabulary.asLocalResource(globalSubject.getURI());
				
				Resource localPredicate = null;
				if(type.equals("object")) 
					localPredicate = vocabulary.asLocalObjectProperty(globalPredicate.getURI());
				
				if(type.equals("datatype")) 
					localPredicate = vocabulary.asLocalDatatypeProperty(globalPredicate.getURI());
				
				
				Resource localObject = vocabulary.asLocalResource(globalObject.getURI());
				
				Resource akpInstance = vocabulary.akpInstance(localSubject.getURI(), localPredicate.getURI(), localObject.getURI());
			
				//add statements to model
				model.add(akpInstance, vocabulary.numberOfInstances(), statistic2);
			}
			catch(Exception e){
				Events.summarization().error("file" + csvFilePath + " row" + row, e);
			}

		}
		
		OutputStream output = new FileOutputStream(outputFilePath);
		model.write( output, "N-Triples", null ); // or "RDF/XML", etc.

		output.close();


	}

	public static List<Row> readCSV(String rsListFile) throws IOException {
		List<Row> allFacts = new ArrayList<Row>();

		String cvsSplitBy = "##";

		for(String line : FileUtils.readLines(new File(rsListFile))){
			try{
				String[] row = line.split(cvsSplitBy);
				Row r = new Row();

				if (row[0].contains("http") && Integer.parseInt(row[3])>0){
					r.add(Row.Entry.SUBJECT, row[0]);
					r.add(Row.Entry.PREDICATE, row[1]);
					r.add(Row.Entry.OBJECT, row[2]);
					r.add(Row.Entry.SCORE2, row[4]);

					allFacts.add(r);
				}
			}
			catch(Exception e){
				Events.summarization().error("file" + rsListFile + " line " + line, e);
			}
		}
		return allFacts;
	}
}