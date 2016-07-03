package it.unimib.disco.summarization.ontology;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;


public class PropertyGraph {

	DirectedAcyclicGraph<String, DefaultEdge> graph = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
	
	public PropertyGraph(File ontology){			
		OntModel ontologyModel = new Model(ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
			
		PropertyExtractor pExtract = new PropertyExtractor();
		pExtract.setProperty(ontologyModel);
		Properties properties = new Properties();
		properties.setProperty(pExtract.getProperty());
		properties.setExtractedProperty(pExtract.getExtractedProperty());
		properties.setCounter(pExtract.getCounter());
		OntologySubPropertyOfExtractor extractor = new OntologySubPropertyOfExtractor();
		extractor.setSubPropertyOf(properties, ontologyModel);	
		
		for(List<OntProperty> subProperties : extractor.getSubPropertyOfs())
			addEdge(subProperties.get(0).getURI(), subProperties.get(1).getURI());
		
	}
	
	
	public List<List<String>> pathsBetween(String leaf, String root){
		ArrayList<List<String>> paths = new ArrayList<List<String>>();
		if(graph.containsVertex(leaf) && graph.containsVertex(root)){
			inOrderTraversal(leaf, root, new ArrayList<String>(), paths);
		}
		return paths;
	}
	
	private void inOrderTraversal(String leaf, String root, List<String> currentPath, List<List<String>> paths){
		ArrayList<String> path = new ArrayList<String>(currentPath);
		path.add(leaf);
		if(leaf.equals(root)){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperType : graph.outgoingEdgesOf(leaf)){
			String superType = graph.getEdgeTarget(edgeToSuperType);
			inOrderTraversal(superType, root, path, paths);
		}
	}
	
	
	public String findRoot(){
		HashSet<String> orfani = new HashSet<String>();
		String root = null;
		
		//Ricavo i vertici orfani 
		Set<String> vertices = new HashSet<String>();
	    vertices.addAll(graph.vertexSet());
	    for (String vertex : vertices) { 
	    	boolean isOrphan = true;
	    	Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
			for (DefaultEdge edge : relatedEdges) {
				if(graph.getEdgeSource(edge).equals(vertex))
					isOrphan = false;
			}
			if(isOrphan){
				orfani.add(vertex);
				System.out.println(vertex);
			}
	    }
	    
	    int max=0;
	    for(String orfano : orfani){
	    	int cont=0;
	    	for(String vertex : vertices)
	    		if(!orfani.contains(vertex))   //solo se vertex non è uno degli orfani
	    			if(!pathsBetween(vertex, orfano).isEmpty())
	    				cont++;
	    	if(cont>max){
	    		root = orfano;
	    		max=cont;
	    	}
	    }  
	    return root;   
	}
	
	
	public boolean isVertexAndHasFather(String c){
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
		

	public void verificaCammino() throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("Cammino.txt"));
		Set<String> vertices = new HashSet<String>();
	    vertices.addAll(graph.vertexSet());
	    for (String vertex : vertices) { 
	    	ArrayList<String> list1 = new ArrayList<String>();	
			list1.add(vertex);

			boolean haPadre;
			boolean primoPadre=true;
			do{
				haPadre=false;
				primoPadre=true;
				Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
				String target=null;
				for (DefaultEdge edge : relatedEdges) {
					if(vertex.equals( graph.getEdgeSource(edge))){
						String tar = graph.getEdgeTarget(edge);
						if(isVertexAndHasFather(tar)){
							if(primoPadre){
								haPadre=true;
								target = tar;
								String targetStringa = target;
								list1.add(targetStringa);
								//vertex = target;
								primoPadre=false;
							}
							else
								System.out.println("------------------"+vertex+"----------------------");
						}
					}
				}
				vertex=target;
			}while(haPadre);
			fos.write((list1.toString()+"\n").getBytes());
	    }
	    fos.close();
	}
		
	
	public void discendenza() throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("DiscendenzaPredicati.txt"));
		Set<String> vertices = new HashSet<String>();
	    vertices.addAll(graph.vertexSet());
	    for (String vertex : vertices) { 
	    	List<List<String>> paths = pathsToFartherAncestors(vertex);
	    	for(List<String> path : paths){
	    		fos.write((path.toString()+"\n").getBytes());
	    	}
	    }
	    fos.close();
	}
	
	public List<List<String>> pathsToFartherAncestors(String node){
		ArrayList<List<String>> paths = new ArrayList<List<String>>();
		if(graph.containsVertex(node)){
			inOrderTraversal(node, new ArrayList<String>(), paths);
		}
		return paths;
	}
	
	private void inOrderTraversal(String node, List<String> currentPath, List<List<String>> paths){
		ArrayList<String> path = new ArrayList<String>(currentPath);
		path.add(node);
		if(graph.outgoingEdgesOf(node).isEmpty()){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperProperty : graph.outgoingEdgesOf(node)){
			String superProperty = graph.getEdgeTarget(edgeToSuperProperty);
			inOrderTraversal(superProperty, path, paths);
		}
	}


	public void stampaPadriProperties() throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("PadriProperties.txt"));
		Set<String> vertices = new HashSet<String>();
	    vertices.addAll(graph.vertexSet());
	    for (String vertex : vertices) {   
	    	Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
	    	fos.write(("\n"+ vertex).getBytes());
        	for (DefaultEdge edge : relatedEdges) {
        		if(vertex.equals( graph.getEdgeSource(edge) )){
        			String target = graph.getEdgeTarget(edge);
        			fos.write(("##"+target).getBytes());
        		}
        	}
	    }
	    fos.close();
	}
	
	
	public String returnV_graph(String p){
		Set<String> vertices = new HashSet<String>();
	    vertices.addAll(graph.vertexSet());
	    for (String vertex : vertices)    
	    	if(p.equals(vertex))
	    		return vertex; 
	    return null;
	}
	
	public void addEdge(String tipo, String supertipo){
		if(!graph.containsVertex(tipo))
			graph.addVertex(tipo);
		
		if(graph.containsVertex(supertipo))
			graph.addEdge(tipo, returnV_graph(supertipo));
		else{
			graph.addVertex(supertipo);
			graph.addEdge(tipo, supertipo);
		}
	}
	
	public DirectedAcyclicGraph<String, DefaultEdge> getGraph(){
		return graph;
	}

}
