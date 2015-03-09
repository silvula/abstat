package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class WriteConceptsToRDF {
	public static void main (String args []) throws IOException{

		for (int j=0; j<args.length; j++){
			Model model = ModelFactory.createDefaultModel();
			String csvFilePath = args[j];

		//Get all of the rows
		List<Row> rows = readCSV(csvFilePath);

		for (int i=1;i<rows.size();i++){

			Resource subject = model.createResource(rows.get(i).get(Row.Entry.SUBJECT));
			Resource signature = model.createResource("http://schemasummaries.org/ontology/Signature");
			Property has_statistic1 = model.createProperty("http://schemasummaries.org/ontology/has_frequency");
			Property has_statistic2 = model.createProperty("http://schemasummaries.org/ontology/has_percentage_minimalType");
			Literal statistic1 = model.createTypedLiteral(Integer.parseInt(rows.get(i).get(Row.Entry.SCORE1)));
			Literal statistic2 = model.createTypedLiteral(Double.parseDouble(rows.get(i).get(Row.Entry.SCORE2)));

			// creating a statement doesn't add it to the model
			Statement stmt = model.createStatement( subject, RDF.type, signature );
			Statement stmt_stat1 = model.createStatement( subject, has_statistic1, statistic1 );
			Statement stmt_stat2 = model.createStatement( subject, has_statistic2, statistic2 );

			model.add(stmt);
			model.add(stmt_stat1);
			model.add(stmt_stat2);
		}
		String file_name= args[j].substring(args[j].lastIndexOf("/"), args[j].lastIndexOf(".")); 

		File directory = new File (".");

		OutputStream output = new FileOutputStream(directory.getAbsolutePath()+"/output/"+file_name+".nt");
		model.write( output, "N-Triples", null ); // or "RDF/XML", etc.
		output.close();

		}
	}

	public static List<Row> readCSV(String rsListFile) throws IOException {
		List<Row> allFacts = new ArrayList<Row>();

		BufferedReader br = null;
		String line  =  "";
		String cvsSplitBy = "##";

		try {

			br = new BufferedReader(new FileReader(rsListFile));
			while ((line = br.readLine()) != null) {

				String[] row = line.split(cvsSplitBy);
				Row r = new Row();
				if (row[0].contains("http")){
					r.add(Row.Entry.SUBJECT, row[0]);
					r.add(Row.Entry.SCORE1, row[1]);
					r.add(Row.Entry.SCORE2, row[2]);

					allFacts.add(r);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return allFacts;
	}

}