package it.unimib.disco.summarization.ontology;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;



public class PropertyGraph {

	DirectedAcyclicGraph<OntProperty, DefaultEdge> graph = new DirectedAcyclicGraph<OntProperty, DefaultEdge>(DefaultEdge.class);
	OntModel ontologyModel;
	
	public PropertyGraph(File ontology){			
		ontologyModel = new Model(ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
			
		PropertyExtractor pExtract = new PropertyExtractor();
		pExtract.setProperty(ontologyModel);
		Properties properties = new Properties();
		properties.setProperty(pExtract.getProperty());
		properties.setExtractedProperty(pExtract.getExtractedProperty());
		properties.setCounter(pExtract.getCounter());
		OntologySubPropertyOfExtractor extractor = new OntologySubPropertyOfExtractor();
		extractor.setSubPropertyOf(properties, ontologyModel);	
		
		
		//utile in caso ci siano predicati senza figli e senza padri: il for dopo non li rappresenterebbe nel property graph
		for (OntProperty prop : properties.getExtractedProperty()){
			if(!graph.containsVertex(prop))
				graph.addVertex(prop);
		}
		
		//link tra due predicati che rispettano la relazione subPropertyOf
		for(List<OntProperty> subProperties : extractor.getSubPropertyOfs()){
			addEdge(subProperties.get(0), subProperties.get(1));
			System.out.println(subProperties.get(0) + " ## " + subProperties.get(1));
		}
		
		linkToTheoreticalProperties();
	}
	
	public HashSet<OntProperty> findRoots(){
		HashSet<OntProperty> orfani = new HashSet<OntProperty>();
		
		//Ricavo i vertici orfani 
		Set<OntProperty> vertices = new HashSet<OntProperty>();
	    vertices.addAll(graph.vertexSet());
	    for (OntProperty vertex : vertices) { 
	    	boolean isOrphan = true;
	    	Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
			for (DefaultEdge edge : relatedEdges) {
				if(graph.getEdgeSource(edge).equals(vertex))
					isOrphan = false;
			}
			if(isOrphan)
				orfani.add(vertex);
	    }
	    return orfani;   
	}

	
	public List<List<OntProperty>> pathsBetween(String v1, String v2){
		OntProperty vertex = ontologyModel.createOntProperty(v1);
		OntProperty orfano = ontologyModel.createOntProperty(v2);
		
		ArrayList<List<OntProperty>> paths = new ArrayList<List<OntProperty>>();
		if(graph.containsVertex(vertex) && graph.containsVertex(orfano)){
			inOrderTraversal(vertex, orfano, new ArrayList<OntProperty>(), paths);
		}
		return paths;
	}
	
	
	private void inOrderTraversal(OntProperty vertex, OntProperty orfano, List<OntProperty> currentPath, ArrayList<List<OntProperty>> paths){
		ArrayList<OntProperty> path = new ArrayList<OntProperty>(currentPath);
		path.add(vertex);
		if(vertex.equals(orfano)){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperType : graph.outgoingEdgesOf(vertex)){
			OntProperty superType = graph.getEdgeTarget(edgeToSuperType);
			inOrderTraversal(superType, orfano, path, paths);
		}
	}
	
		
	public boolean isVertexAndHasFather(OntProperty c){
		if(graph.containsVertex(c)){
			boolean haPadre = false;
			Set<DefaultEdge> relatedEdges = graph.edgesOf(c);
			for (DefaultEdge edge : relatedEdges) 
				if(c.equals( graph.getEdgeSource(edge)))
					haPadre = true;	
			return haPadre;
		}
		return false;	
	}
	
	
	public OntProperty returnV_graph(OntProperty p){
		Set<OntProperty> vertices = new HashSet<OntProperty>();
	    vertices.addAll(graph.vertexSet());
	    for (OntProperty vertex : vertices)    
	    	if(p.equals(vertex)) /////////////// equals o == ?
	    		return vertex; 
	    return null;
	}
	
	public void addEdge(OntProperty property, OntProperty superProperty){
		if(!graph.containsVertex(property))
			graph.addVertex(property);
		
		if(graph.containsVertex(superProperty))
			graph.addEdge(property, returnV_graph(superProperty));
		else{
			graph.addVertex(superProperty);
			graph.addEdge(property, superProperty);
		}
	}
	
	public void linkToTheoreticalProperties(){
		for(OntProperty prop : findRoots()){
			if(prop.isDatatypeProperty())
				addEdge(prop, ontologyModel.createOntProperty("http://www.w3.org/2002/07/owl#topDataProperty") );
			else
				addEdge(prop, ontologyModel.createOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty") );
		}
		if(ontologyModel.getOntProperty("http://www.w3.org/2002/07/owl#topDataProperty")!= null)
			addEdge(ontologyModel.getOntProperty("http://www.w3.org/2002/07/owl#topDataProperty"),ontologyModel.createOntProperty("universalProperty") );
		if(ontologyModel.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty")!= null)
			addEdge(ontologyModel.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty"),ontologyModel.createOntProperty("universalProperty") );
	}
	
	
	public DirectedAcyclicGraph<OntProperty, DefaultEdge> getGraph(){
		return graph;
	}
	
	
//--------------------------------------------------------- UTILS ------------------------------------------------------------------------------
	public void discendenza() throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("DiscendenzaPredicati.txt"));
		Set<OntProperty> vertices = new HashSet<OntProperty>();
	    vertices.addAll(graph.vertexSet());
	    for (OntProperty vertex : vertices) { 
	    	List<List<OntProperty>> paths = pathsToFartherAncestors(vertex);
	    	for(List<OntProperty> path : paths){
	    		fos.write((path.toString()+"\n").getBytes());
	    	}
	    }
	    fos.close();
	}
	
	public List<List<OntProperty>> pathsToFartherAncestors(OntProperty vertex){
		ArrayList<List<OntProperty>> paths = new ArrayList<List<OntProperty>>();
		if(graph.containsVertex(vertex)){
			inOrderTraversal(vertex, new ArrayList<OntProperty>(), paths);
		}
		return paths;
	}
	
	private void inOrderTraversal(OntProperty vertex, List<OntProperty> currentPath, List<List<OntProperty>> paths){
		ArrayList<OntProperty> path = new ArrayList<OntProperty>(currentPath);
		path.add(vertex);
		if(graph.outgoingEdgesOf(vertex).isEmpty()){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperProperty : graph.outgoingEdgesOf(vertex)){
			OntProperty superProperty = graph.getEdgeTarget(edgeToSuperProperty);
			inOrderTraversal(superProperty, path, paths);
		}
	}


	public void stampaPadriProperties() throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("PadriProperties.txt"));
		Set<OntProperty> vertices = new HashSet<OntProperty>();
	    vertices.addAll(graph.vertexSet());
	    for (OntProperty vertex : vertices) {   
	    	Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
	    	fos.write(("\n"+ vertex.getURI()).getBytes());
        	for (DefaultEdge edge : relatedEdges) {
        		if(vertex.equals( graph.getEdgeSource(edge) )){
        			OntProperty target = graph.getEdgeTarget(edge);
        			fos.write(("##"+target.getURI()).getBytes());
        		}
        	}
	    }
	    fos.close();
	}
	
}
