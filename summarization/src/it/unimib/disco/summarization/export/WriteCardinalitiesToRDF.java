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
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;
import it.unimib.disco.summarization.ontology.RDFTypeOf;

public class WriteCardinalitiesToRDF {


	public static void main (String args []) throws IOException{
		
		String csvFilePath = args[0];
		String outputFilePath = args[1];
		String dataset = args[2];
		String domain = args[3];
		String type = args[4];
		
		Model model = ModelFactory.createDefaultModel();
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(model, dataset);
		RDFTypeOf typeOf = new RDFTypeOf(domain);
		
		for (Row row : readCSV(csvFilePath)){

			try{
				Resource globalSubject = model.createResource(row.get(Row.Entry.SUBJECT));
				Property globalPredicate = model.createProperty(row.get(Row.Entry.PREDICATE));
				Resource globalObject = vocabulary.selfOrUntyped(row.get(Row.Entry.OBJECT));
				Literal statistic3 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE3)));
				Literal statistic4 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE4)));
				Literal statistic5 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE5)));
				Literal statistic6 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE6)));
				Literal statistic7 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE7)));
				Literal statistic8 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE8)));

				Resource localSubject = vocabulary.asLocalResource(globalSubject.getURI());
				
				Resource localPredicate = null;
				Resource internal = null;
				if(type.equals("object")) {
					localPredicate = vocabulary.asLocalObjectProperty(globalPredicate.getURI());
					internal = typeOf.objectAKP(globalSubject.getURI(), globalObject.getURI());
				}
				if(type.equals("datatype")) {
					localPredicate = vocabulary.asLocalDatatypeProperty(globalPredicate.getURI());
					internal = typeOf.datatypeAKP(globalSubject.getURI());
				}
				
				Resource localObject = vocabulary.asLocalResource(globalObject.getURI());
				
				Resource akpInstance = vocabulary.akpInstance(localSubject.getURI(), localPredicate.getURI(), localObject.getURI());
			
				model.add(akpInstance, vocabulary.max_M_Cardinality(), statistic3);
				model.add(akpInstance, vocabulary.avg_M_Cardinality(), statistic4);
				model.add(akpInstance, vocabulary.min_M_Cardinality(), statistic5);
				model.add(akpInstance, vocabulary.max_N_Cardinality(), statistic6);
				model.add(akpInstance, vocabulary.avg_N_Cardinality(), statistic7);
				model.add(akpInstance, vocabulary.min_N_Cardinality(), statistic8);
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
				String[] parts = line.split(" ");
				String[] row = parts[0].split(cvsSplitBy);
				String[] rowCard = parts[1].split("-");
				Row r = new Row();

				if (row[0].contains("http")){
					r.add(Row.Entry.SUBJECT, row[0]);
					r.add(Row.Entry.PREDICATE, row[1]);
					r.add(Row.Entry.OBJECT, row[2]);
					r.add(Row.Entry.SCORE3, rowCard[0]);
					r.add(Row.Entry.SCORE4, rowCard[1]);
					r.add(Row.Entry.SCORE5, rowCard[2]);
					r.add(Row.Entry.SCORE6, rowCard[3]);
					r.add(Row.Entry.SCORE7, rowCard[4]);
					r.add(Row.Entry.SCORE8, rowCard[5]);

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