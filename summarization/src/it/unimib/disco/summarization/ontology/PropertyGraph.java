package it.unimib.disco.summarization.ontology;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import it.unimib.disco.summarization.experiments.JgraphGUI;



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
			//System.out.println(subProperties.get(0) + " ## " + subProperties.get(1));
		}
	}
	
	
	public void linkToTheoreticalProperties(){
		for(OntProperty prop : findRoots()){
			if(prop.isDatatypeProperty())
				addEdge(prop, ontologyModel.createOntProperty("http://www.w3.org/2002/07/owl#topDataProperty") );
			else
				addEdge(prop, ontologyModel.createOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty") );
		}
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
	
	
	
    //Ritorna i superpredicati diretti del predicato in input
    public ArrayList<String> superProperties(String arg){
        	ArrayList<String> supertipi = new ArrayList<String>();
          	OntProperty source, target;
            Set<OntProperty> vertices = new HashSet<OntProperty>();
            vertices.addAll(graph.vertexSet());
       
            for (OntProperty vertex : vertices) {
                if(vertex.getURI().equals(arg)){        //cioè se ho trovato il concetto nel propertyGraph
                    Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
                    for (DefaultEdge edge : relatedEdges) {
                        source = graph.getEdgeSource(edge);
                        target = graph.getEdgeTarget(edge);
                        if(source.equals(vertex))
                            supertipi.add(target.getURI());     
                    }
                }
            }
            
            return supertipi;
        
    }
    
    //Ritorna i superpredicati diretti del predicato in input
    public ArrayList<String> superPropertiesFull(String arg, String type){
    	if(!graph.containsVertex(this.ontologyModel.createOntProperty(arg))){
    		ArrayList<String> output = new ArrayList<String>();
    		if(type.equals("object"))
    			output.add("http://www.w3.org/2002/07/owl#topObjectProperty");
    		else
    			output.add("http://www.w3.org/2002/07/owl#topDataProperty");
    		return output;
    	}
    
    	else if(arg.equals("universalProperty"))
            return null;
    	
        else
        	return superProperties(arg);
    }
	
    
	public DirectedAcyclicGraph<OntProperty, DefaultEdge> getGraph(){
		return graph;
	}
	
	
	public List<HashSet<OntProperty>> pseudoStronglyConnectedSets(){
		HashMap<OntProperty, HashSet<OntProperty>> map = new HashMap<OntProperty, HashSet<OntProperty>>();
		List<HashSet<OntProperty>> rootFamilies = new ArrayList<HashSet<OntProperty>>();
		
		//HashSet<ArrayList<OntProperty>> rootsOfVertices = new HashSet<ArrayList<OntProperty>>();//ogni lista mi dice i valori di tali root in map in realtà stanno nello stesso grafo
		for(OntProperty vertex : graph.vertexSet()){
			
			HashSet<OntProperty> fartherAncestors = new HashSet<OntProperty>();
			for(List<OntProperty> path : pathsToFartherAncestors(vertex)){
				//ricavo il root di path e lo inserisco tra i root di vertex
				OntProperty ancestor = path.get(path.size()-1);
				fartherAncestors.add(ancestor);
				
				//mappa il root di path con i nodi di path, compreso root
				HashSet<OntProperty> set = new HashSet<OntProperty>();
				for(OntProperty prop : path){
					set.add(prop);
				}
				if(map.containsKey(ancestor)){
					HashSet<OntProperty> value = map.get(ancestor);
					value.addAll(set);
					map.put(ancestor, value);
				}
				else
					map.put(ancestor, set);
			}
			
			
			HashSet<HashSet<OntProperty>> daUnire = new HashSet<HashSet<OntProperty>>();
			for(OntProperty root : fartherAncestors){
				for(HashSet<OntProperty> rootFamily : rootFamilies)
					if(rootFamily.contains(root)){
						daUnire.add(rootFamily);
						}
			}
	
			
			if(daUnire.size()==0){
				rootFamilies.add(fartherAncestors);
			}
			else{
				HashSet<OntProperty> famMerged = new HashSet<OntProperty>();
				famMerged.addAll(fartherAncestors);
				for(HashSet<OntProperty> set : daUnire)
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
		
		ArrayList<HashSet<OntProperty>> pseudoSCS = new  ArrayList<HashSet<OntProperty>>();
		for(HashSet<OntProperty> rootFamily : rootFamilies){
			HashSet<OntProperty> connectedSet = new HashSet<OntProperty>();
			for(OntProperty root : rootFamily){
				connectedSet.addAll(map.get(root));
				connectedSet.add(root);
			}
			pseudoSCS.add(connectedSet);
		}


		return pseudoSCS;
		
	}
	
	
//------------------------------------------------------------ SECONDARI ----------------------------------------------------------------------	
	
	public OntProperty returnV_graph(OntProperty p){
		Set<OntProperty> vertices = new HashSet<OntProperty>();
	    vertices.addAll(graph.vertexSet());
	    for (OntProperty vertex : vertices)    
	    	if(p.equals(vertex))
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
	
	//Ritorna tutti i vertici orfani, ovvero senza un padre
	public HashSet<OntProperty> findRoots(){
		HashSet<OntProperty> orfani = new HashSet<OntProperty>();
		
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
	
	
	public List<HashSet<String>> convertPseudoSCS(List<HashSet<OntProperty>> pseudoSCS){
		List<HashSet<String>> out = new ArrayList<HashSet<String>>();
		for(HashSet<OntProperty> set : pseudoSCS){
			HashSet<String> setCopy = new HashSet<String>();
			for(OntProperty prop : set){
				setCopy.add(prop.getURI());
			}
			out.add(setCopy);
		}
		return out;
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
	
	public void disegna(){
		JgraphGUI gui = new JgraphGUI(graph);
		JFrame frame = new JFrame();
		frame.getContentPane().add(gui);
		frame.setTitle("Property Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	public void verificaCorrettezza(){
		List<HashSet<OntProperty>> pseudoSCS = pseudoStronglyConnectedSets();
		for(OntProperty vertex : graph.vertexSet()){
			int cont=0;
			for(HashSet<OntProperty> set : pseudoSCS){
				if(set.contains(vertex))
					cont++;
				
				if(set.size()==1){
					OntProperty prop = (OntProperty)set.toArray()[0];
					Set<DefaultEdge> edges = graph.edgesOf(this.returnV_graph(prop));
					if(edges.size()!=0)
						System.out.println("SINGOLO HA ARCHI!: "+ edges.size()+ "  "+ prop);
				}
			}
			
			if(cont!=1)
				System.out.println("DIVERSO DA 1: "+ cont +"  "+ vertex.getURI());
		}
	}
	
	public void verificaCorrettezza2(){
		List<HashSet<OntProperty>> pseudoSCS = pseudoStronglyConnectedSets();
		for(HashSet<OntProperty> set : pseudoSCS){
			for(OntProperty prop : set){
				for(List<OntProperty> path  : pathsToFartherAncestors(prop)){
					Object[] array = path.toArray();
					if(!set.contains( (OntProperty)array[array.length-1] ))
						System.out.println(prop +"   NON contiene il suo root "+ " "+array[array.length-1].toString() +"  nel suo set");
				}
			}
		}
	}
	
}
