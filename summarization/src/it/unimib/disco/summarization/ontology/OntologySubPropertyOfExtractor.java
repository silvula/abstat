package it.unimib.disco.summarization.ontology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OntologySubPropertyOfExtractor {
	private ArrayList<List<OntProperty>> subPropertyOfs = new ArrayList<List<OntProperty>>();
	
	public void setSubPropertyOf(Properties Properties, OntModel ontologyModel){
		
		Iterator<OntProperty> IteratorExtractedProperties = Properties.getExtractedProperty().iterator();
		while(IteratorExtractedProperties.hasNext()){
			OntProperty property  = (OntProperty)IteratorExtractedProperties.next();
			String URI = property.getURI();
			
			if( URI != null ){
				try{
					ExtendedIterator<OntProperty> itSup = (ExtendedIterator<OntProperty>) property.listSuperProperties(true);
					while(itSup.hasNext()){
						OntProperty propertySup = itSup.next();
						String URISUP = propertySup.getURI();
						
						if(URISUP!= null){
							addSubPropertyOfRelation(property, propertySup);
						}
					}
				}
				catch(Exception e){
					//SPARQL Query for SubProperties
					String queryString = "PREFIX rdfs:<" + RDFS.getURI() + ">" +
										 "PREFIX ont:<" + property.getNameSpace() + ">" + 
										 "SELECT ?obj " +
										 "WHERE {" +
										 "      ont:" + property.getLocalName() + " rdfs:subPropertyOf ?obj" +
										 "      }";
					
					//Execute Query
					Query query = QueryFactory.create(queryString) ;
					QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel) ;
					
					try {
					    
						ResultSet results = qexec.execSelect();
					    
					    //Temporary Model in Order to Construct Node for External Property
					    OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 
					    
					    //Extract Relation
					    for ( ; results.hasNext() ; ){
					    	QuerySolution soln = results.nextSolution() ;

					    	Resource obj = soln.getResource("obj");
					    	String URIObj = obj.getURI();
					      
					    	//Get SubPropertyOf all Property different from the current one
					    	if( URIObj!=null && property.getURI()!=URIObj ){
					    		OntProperty propertySup = ontologyTempModel.createOntProperty(URIObj);

					    		//Save SubPropertyOf Relation (property SubPropertyOf PropertySub)
					    		addSubPropertyOfRelation(property, propertySup);
					    	}
					    }
					} finally { qexec.close() ; }
				
					
				}
				
			}
		}
	}
	
	
	//Save SubPropertyOf Relation (property SubPRopertyOf propertySup)
	public void addSubPropertyOfRelation(OntProperty property, OntProperty propertySup){
		List<OntProperty> subRelation = new ArrayList<OntProperty>();
		subRelation.add(property); //SubProperty
		subRelation.add(propertySup); //SuperProperty
		subPropertyOfs.add(subRelation);
	}
	
	public ArrayList<List<OntProperty>> getSubPropertyOfs(){
		return subPropertyOfs;
	}

}
