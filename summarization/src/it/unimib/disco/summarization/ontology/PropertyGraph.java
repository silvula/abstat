package it.unimib.disco.summarization.ontology;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import it.unimib.disco.summarization.experiments.Property;
import it.unimib.disco.summarization.export.Events;



public class PropertyGraph {

	DirectedAcyclicGraph<Property, DefaultEdge> graph = new DirectedAcyclicGraph<Property, DefaultEdge>(DefaultEdge.class);
	OntModel ontologyModel;
	
	public PropertyGraph(File ontology) {			
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
		for (OntProperty property : properties.getExtractedProperty()){
			Property prop = new Property(property);
			if(!graph.containsVertex(prop))
				graph.addVertex(prop);
		}
		
		//link tra due predicati che rispettano la relazione subPropertyOf
		for(List<OntProperty> subProperties : extractor.getSubPropertyOfs())
			addEdgeWDepth( new Property(subProperties.get(0)), new Property(subProperties.get(1)) );
		
	}
	
	
	public void linkToTheoreticalProperties(){
		for(Property prop : findRoots()){
			if(prop.getOntProp().isDatatypeProperty()){
				Property topDataProperty = new Property( ontologyModel.createOntProperty("http://www.w3.org/2002/07/owl#topDataProperty"), 0);
				addEdgeWDepth(prop, topDataProperty );
			}
			else{
				Property topObjectProperty = new Property( ontologyModel.createOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty"), 0); 
				addEdgeWDepth(prop, topObjectProperty);
			}
		}
	}
	

	public void linkExternalProperty(Property prop, String type){
		if(type.equals("datatype"))
			addEdgeWDepth(prop, returnV("http://www.w3.org/2002/07/owl#topDataProperty"));
		else
			addEdgeWDepth(prop, returnV("http://www.w3.org/2002/07/owl#topObjectProperty"));
		
	}
	
	public List<List<Property>> pathsBetween(String v1, String v2){
		Property vertex = new Property( ontologyModel.createOntProperty(v1) );
		Property orfano = new Property( ontologyModel.createOntProperty(v2) );
		
		ArrayList<List<Property>> paths = new ArrayList<List<Property>>();
		if(graph.containsVertex(vertex) && graph.containsVertex(orfano)){
			inOrderTraversal(vertex, orfano, new ArrayList<Property>(), paths);
		}
		return paths;
	}
	
	
	private void inOrderTraversal(Property vertex, Property orfano, List<Property> currentPath, ArrayList<List<Property>> paths){
		ArrayList<Property> path = new ArrayList<Property>(currentPath);
		path.add(vertex);
		if(vertex.equals(orfano)){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperType : graph.outgoingEdgesOf(vertex)){
			Property superType = graph.getEdgeTarget(edgeToSuperType);
			inOrderTraversal(superType, orfano, path, paths);
		}
	}
	
	

    
    //Ritorna i superpredicati diretti del predicato in input
    public ArrayList<String> superPropertiesFull(String arg, String type){
    	
    	if(!graph.containsVertex( new Property( ontologyModel.createOntProperty(arg)) )){
    		ArrayList<String> output = new ArrayList<String>();
    		if(type.equals("object"))
    			output.add("http://www.w3.org/2002/07/owl#topObjectProperty");
    		else
    			output.add("http://www.w3.org/2002/07/owl#topDataProperty");
    		return output;
    	}  	
    	
        else{
        	ArrayList<String> superprops = new ArrayList<String>();
          	Property source, target;
            Set<Property> vertices = new HashSet<Property>();
            vertices.addAll(graph.vertexSet());
       
            for (Property vertex : vertices) {
                if(vertex.getURI().equals(arg)){        //cioè se ho trovato il concetto nel propertyGraph
                    Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
                    for (DefaultEdge edge : relatedEdges) {
                        source = graph.getEdgeSource(edge);
                        target = graph.getEdgeTarget(edge);
                        if(source.equals(vertex))
                            superprops.add(target.getURI());     
                    }
                }
            }
            
            if(type.equals("datatype") && superprops.contains("http://www.w3.org/2002/07/owl#topObjectProperty")){
            	superprops.remove("http://www.w3.org/2002/07/owl#topObjectProperty");
            	superprops.add("http://www.w3.org/2002/07/owl#topDataProperty");
            	Events.summarization().info(arg + " è un ObjectProperty usato come DatatypeProperty !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            if(type.equals("object") && superprops.contains("http://www.w3.org/2002/07/owl#topDataProperty")){
            	superprops.remove("http://www.w3.org/2002/07/owl#topDataProperty");
            	superprops.add("http://www.w3.org/2002/07/owl#topObjectProperty");
            	Events.summarization().info(arg + " è un DatatypeProperty  usato come ObjectProperty !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            	
  
            return superprops;
        }
    }
	
    
	public DirectedAcyclicGraph<Property, DefaultEdge> getGraph(){
		return graph;
	}
	
	
	public List<ArrayList<Property>> pseudoStronglyConnectedSets(){
		HashMap<Property, HashSet<Property>> map = new HashMap<Property, HashSet<Property>>();
		List<HashSet<Property>> rootFamilies = new ArrayList<HashSet<Property>>();
		
		for(Property vertex : graph.vertexSet()){
			
			HashSet<Property> fartherAncestors = new HashSet<Property>();
			for(List<Property> path : pathsToFartherAncestors(vertex)){
				//ricavo il root di path e lo inserisco tra i root di vertex
				Property ancestor = path.get(path.size()-1);
				fartherAncestors.add(ancestor);
				
				//mappa il root di path con i nodi di path, compreso root
				HashSet<Property> set = new HashSet<Property>();
				for(Property prop : path){
					set.add(prop);
				}
				if(map.containsKey(ancestor)){
					HashSet<Property> value = map.get(ancestor);
					value.addAll(set);
					map.put(ancestor, value);
				}
				else
					map.put(ancestor, set);
			}
			
			
			HashSet<HashSet<Property>> daUnire = new HashSet<HashSet<Property>>();
			for(Property root : fartherAncestors){
				for(HashSet<Property> rootFamily : rootFamilies)
					if(rootFamily.contains(root)){
						daUnire.add(rootFamily);
						}
			}
	
			
			if(daUnire.size()==0){
				rootFamilies.add(fartherAncestors);
			}
			else{
				HashSet<Property> famMerged = new HashSet<Property>();
				famMerged.addAll(fartherAncestors);
				for(HashSet<Property> set : daUnire)
					famMerged.addAll(set);
				rootFamilies.removeAll(daUnire);
				rootFamilies.add(famMerged);
			}
		}
		
		//CONTROLLO CORRETTEZZA MAP:
		/*for(OntProperty key : map.keySet()){
			for(OntProperty prop : map.get(key)){
				boolean corretto = false;
				for(List<OntProperty> path : pathsToFartherAncestors(prop)){
					OntProperty ancestor = path.get(path.size()-1);
					if(key.equals(ancestor))
						corretto = true;
					else
						System.out.println( prop+ " questo prop è in un altro value della mappa?" + map.get(ancestor).contains(prop) + "con chiave "+ ancestor);
						
				}
				if(!corretto)
					System.out.println("map("+key+") contiene " + prop +" e key non è suo ancestor");
			}
		}*/
		
		/*
		//CONTROLLO CORRETTEZZA ROOTFAMILIES:
		for(HashSet<OntProperty> family : rootFamilies){
			for(OntProperty root : family){
				int cont = 0;
				for(HashSet<OntProperty> family2 : rootFamilies){
					if(family2.contains(root))
						cont++;
				}
				if(cont !=1)
					System.out.println(root + " questo root è presente in "+ cont +" sets di rootFamilies");
			}
		
		}
		
	//CONTROLLO CORRETTEZZA ROOTFAMILIES 2:
		for(OntProperty root : findRoots()){
			boolean presente = false;
			for(HashSet<OntProperty> family : rootFamilies){
				if(family.contains(root))
					presente=true;
			}
			if(!presente)
				System.out.println(root + "Questo vertex-root non è presente in nessuna rootFamily");
		}
		
		*/
		
		ArrayList<ArrayList<Property>> pseudoSCS = new  ArrayList<ArrayList<Property>>();
		for(HashSet<Property> rootFamily : rootFamilies){
			HashSet<Property> connectedSet = new HashSet<Property>();
			for(Property root : rootFamily){
				connectedSet.addAll(map.get(root));
				connectedSet.add(root);
			}
			ArrayList<Property> connectedSetArrayList = new ArrayList<Property>();
			connectedSetArrayList.addAll(connectedSet);
			pseudoSCS.add(connectedSetArrayList);
		}


		return pseudoSCS;
		
	}
	
	
//------------------------------------------------------------ SECONDARI ----------------------------------------------------------------------	
	
	public Property returnV(Property p){
		Set<Property> vertices = new HashSet<Property>();
	    vertices.addAll(graph.vertexSet());
	    for (Property vertex : vertices)    
	    	if(p.equals(vertex))
	    		return vertex; 
	    return null;
	}
	
	public Property returnV(String p){
		return returnV( new Property(ontologyModel.createOntProperty(p) ));
	}
	
	
	public Property createProperty(String propertyURI){
		return new Property( ontologyModel.createOntProperty(propertyURI));
	}
	
	/*
	public void addEdge(Property property, Property superProperty){
		if(!graph.containsVertex(property))
			graph.addVertex(property);
		
		if(graph.containsVertex(superProperty))
			graph.addEdge(property, returnV_graph(superProperty));
		else{
			graph.addVertex(superProperty);
			graph.addEdge(property, superProperty);
		}
	}
	*/
	
	//Costruisce il typegraph incrementalmente contando il DEPTH
	private void addEdgeWDepth(Property property, Property superProperty){
		if(graph.containsVertex(property)){
			Property source = returnV(property);
			
			if(graph.containsVertex(superProperty)){
				Property target = returnV(superProperty);
				graph.addEdge( source, target );
				if(superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topDataProperty") || superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topObjectProperty"))	
					source.setDepth(1);	
				else
					source.setDepth(target.getDepth() + 1);	
				
				Set<DefaultEdge> relatedEdges = graph.edgesOf(source);
				for (DefaultEdge edge : relatedEdges){
					Property s = graph.getEdgeSource(edge);
					Property t = graph.getEdgeTarget(edge);
					if(t.equals(source))
						addEdgeWDepth(s, source);	
				}
			}
			else{
				graph.addVertex(superProperty);
				graph.addEdge( source, superProperty );
				if(superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topDataProperty") || superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topObjectProperty")){	
					source.setDepth(1);
					
					Set<DefaultEdge> relatedEdges = graph.edgesOf(source);
					for (DefaultEdge edge : relatedEdges){
						Property s = graph.getEdgeSource(edge);
						Property t = graph.getEdgeTarget(edge);
						if(t.equals(source))
							addEdgeWDepth(s, source);	
					}
					
				}			
			}
		}
		
		else{
			graph.addVertex(property);
			
			if(graph.containsVertex(superProperty)){
				Property target = returnV(superProperty);
				graph.addEdge( property, target );
				if(superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topDataProperty") || superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topObjectProperty"))
					property.setDepth(1);
				else
					property.setDepth(target.getDepth() + 1);		
			}
			else{
				graph.addVertex(superProperty);
				graph.addEdge( property, superProperty);
				if(superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topDataProperty") || superProperty.getURI().equals("http://www.w3.org/2002/07/owl#topObjectProperty"))
					property.setDepth(1);	
			}
		}	
	}
	
	//Ritorna tutti i vertici orfani, ovvero senza un padre
	public HashSet<Property> findRoots(){
		HashSet<Property> orfani = new HashSet<Property>();
		
		Set<Property> vertices = new HashSet<Property>();
	    vertices.addAll(graph.vertexSet());
	    for (Property vertex : vertices) { 
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
	
	
	public List<ArrayList<String>> convertPseudoSCS(List<ArrayList<Property>> pseudoSCS){
		List<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
		for(ArrayList<Property> set : pseudoSCS){
			ArrayList<String> setCopy = new ArrayList<String>();
			for(Property prop : set){
				setCopy.add(prop.getURI());
			}
			out.add(setCopy);
		}
		return out;
	}
	
	
//--------------------------------------------------------- UTILS ------------------------------------------------------------------------------
	
	public void discendenza() throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("DiscendenzaPredicati.txt"));
		Set<Property> vertices = new HashSet<Property>();
	    vertices.addAll(graph.vertexSet());
	    for (Property vertex : vertices) { 
	    	List<List<Property>> paths = pathsToFartherAncestors(vertex);
	    	for(List<Property> path : paths){
	    		fos.write((path.toString()+"\n").getBytes());
	    	}
	    }
	    fos.close();
	}
	
	public List<List<Property>> pathsToFartherAncestors(Property vertex){
		ArrayList<List<Property>> paths = new ArrayList<List<Property>>();
		if(graph.containsVertex(vertex)){
			inOrderTraversal(vertex, new ArrayList<Property>(), paths);
		}
		return paths;
	}
	
	private void inOrderTraversal(Property vertex, List<Property> currentPath, List<List<Property>> paths){
		ArrayList<Property> path = new ArrayList<Property>(currentPath);
		path.add(vertex);
		if(graph.outgoingEdgesOf(vertex).isEmpty()){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperProperty : graph.outgoingEdgesOf(vertex)){
			Property superProperty = graph.getEdgeTarget(edgeToSuperProperty);
			inOrderTraversal(superProperty, path, paths);
		}
	}


	public void stampaPadriProperties() throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("PadriProperties.txt"));
		Set<Property> vertices = new HashSet<Property>();
	    vertices.addAll(graph.vertexSet());
	    for (Property vertex : vertices) {   
	    	Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
	    	fos.write(("\n"+ vertex.toString()).getBytes());
        	for (DefaultEdge edge : relatedEdges) {
        		if(vertex.equals( graph.getEdgeSource(edge) )){
        			Property target = graph.getEdgeTarget(edge);
        			fos.write(("##"+target.toString()).getBytes());
        		}
        	}
	    }
	    fos.close();
	}
	
/*	
	public void verificaCorrettezza(){
		List<HashSet<Property>> pseudoSCS = pseudoStronglyConnectedSets();
		for(Property vertex : graph.vertexSet()){
			int cont=0;
			for(HashSet<Property> set : pseudoSCS){
				if(set.contains(vertex))
					cont++;
				
				if(set.size()==1){
					Property prop = (Property)set.toArray()[0];
					Set<DefaultEdge> edges = graph.edgesOf(returnV(prop));
					if(edges.size()!=0)
						System.out.println("SINGOLO HA ARCHI!: "+ edges.size()+ "  "+ prop);
				}
			}
			
			if(cont!=1)
				System.out.println("DIVERSO DA 1: "+ cont +"  "+ vertex.getURI());
		}
	}*/
	
/*	public void verificaCorrettezza2(){
		List<HashSet<Property>> pseudoSCS = pseudoStronglyConnectedSets();
		for(HashSet<Property> set : pseudoSCS){
			for(Property prop : set){
				for(List<Property> path  : pathsToFartherAncestors(prop)){
					Object[] array = path.toArray();
					if(!set.contains( (OntProperty)array[array.length-1] ))
						System.out.println(prop +"   NON contiene il suo root "+ " "+array[array.length-1].toString() +"  nel suo set");
				}
			}
		}
	}*/
	
	
}
