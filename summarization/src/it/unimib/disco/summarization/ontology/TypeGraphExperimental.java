package it.unimib.disco.summarization.ontology;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

import it.unimib.disco.summarization.experiments.Concept;
import it.unimib.disco.summarization.experiments.JgraphGUI;

public class TypeGraphExperimental {

	 DirectedAcyclicGraph<Concept, DefaultEdge> graph = new DirectedAcyclicGraph<Concept, DefaultEdge>(DefaultEdge.class);
	 
	 public TypeGraphExperimental(File ontology){
			OntModel ontologyModel = new Model(ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
			
			ConceptExtractor cExtract = new ConceptExtractor();
			cExtract.setConcepts(ontologyModel);
			Concepts concepts = new Concepts();
			concepts.setConcepts(cExtract.getConcepts());
			concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
			concepts.setObtainedBy(cExtract.getObtainedBy());
			OntologySubclassOfExtractor extractor = new OntologySubclassOfExtractor();
			extractor.setConceptsSubclassOf(concepts, ontologyModel);
				
			for(List<OntClass> subClasses : extractor.getConceptsSubclassOf().getConceptsSubclassOf()){
				Concept tipo = new Concept(subClasses.get(0).getURI());
				Concept supertipo = new Concept(subClasses.get(1).getURI());
				addEdgeWDepth(tipo, supertipo);
			}
			
			forceUniqueRoot();
	 }
	
	   
	public Concept returnV(Concept p){
		Set<Concept> vertices = new HashSet<Concept>();
		   vertices.addAll(graph.vertexSet());
		   for (Concept vertex : vertices)    
		   	if(p.equals(vertex))
		    	return vertex; 
		   return null;
	}
	
		
	private void addEdge(Concept tipo, Concept supertipo){
		if(!graph.containsVertex(tipo))
			graph.addVertex(tipo);
			
		if(graph.containsVertex(supertipo))
			graph.addEdge(tipo, returnV(supertipo));
		else{
			graph.addVertex(supertipo);
			graph.addEdge(tipo, supertipo);
		}
	}
	
	/*Costruisce il typegraph incrementalmente contando il DEPTH*/
	private void addEdgeWDepth(Concept tipo, Concept supertipo){
		if(graph.containsVertex(tipo)){
			Concept source = returnV(tipo);
			
			if(graph.containsVertex(supertipo)){
				Concept target = returnV(supertipo);
				graph.addEdge( source, target );
				if(supertipo.getURI().equals("http://www.w3.org/2002/07/owl#Thing"))	
					source.setDepth(1);	
				else
					source.setDepth(target.getDepth() + 1);	
				
				Set<DefaultEdge> relatedEdges = graph.edgesOf(source);
				for (DefaultEdge edge : relatedEdges){
					Concept s = graph.getEdgeSource(edge);
					Concept t = graph.getEdgeTarget(edge);
					if(t.equals(source))
						addEdgeWDepth(s, source);	
				}
			}
			else{
				graph.addVertex(supertipo);
				graph.addEdge( source, supertipo );
				if(supertipo.getURI().equals("http://www.w3.org/2002/07/owl#Thing")){	
					source.setDepth(1);
					
					Set<DefaultEdge> relatedEdges = graph.edgesOf(source);
					for (DefaultEdge edge : relatedEdges){
						Concept s = graph.getEdgeSource(edge);
						Concept t = graph.getEdgeTarget(edge);
						if(t.equals(source))
							addEdgeWDepth(s, source);	
					}
					
				}			
			}
		}
		
		else{
			graph.addVertex(tipo);
			
			if(graph.containsVertex(supertipo)){
				Concept target = returnV(supertipo);
				graph.addEdge( tipo, target );
				if(supertipo.getURI().equals("http://www.w3.org/2002/07/owl#Thing"))
					tipo.setDepth(1);
				else
					tipo.setDepth(target.getDepth() + 1);		
			}
			else{
				graph.addVertex(supertipo);
				graph.addEdge( tipo, supertipo);
				if(supertipo.getURI().equals("http://www.w3.org/2002/07/owl#Thing"))
					tipo.setDepth(1);	
			}
		}	
	}
	
	
	//Ritorna tutti i vertici orfani, ovvero senza un padre
	public HashSet<Concept> findRoots(){
		HashSet<Concept> orfani = new HashSet<Concept>();
		
		Set<Concept> vertices = new HashSet<Concept>();
	    vertices.addAll(graph.vertexSet());
	    for (Concept vertex : vertices) { 
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
	
	/*Ritorna i supertipi diretti del concetto in input
	 * SE è Thing o Literal RITORNA NULL*/
    public ArrayList<Concept> superTipo(Concept arg, String type, String positionInPattern){
    	
    	if(arg.getURI().equals("http://www.w3.org/2002/07/owl#Thing") || arg.getURI().equals("http://www.w3.org/2000/01/rdf-schema#Literal"))
            return null;
    	
    	else if(!graph.containsVertex(arg)){
    		ArrayList<Concept> output = new ArrayList<Concept>();
    		if(positionInPattern.equals("subject") || type.equals("object"))
    			output.add(returnV(new Concept("http://www.w3.org/2002/07/owl#Thing")));
    		else
    			output.add(new Concept("http://www.w3.org/2000/01/rdf-schema#Literal"));
    		return output;
    	}
    	
        else{
        	ArrayList<Concept> supertipi = new ArrayList<Concept>();
            Concept source, target;
            Set<Concept> vertices = new HashSet<Concept>();
            vertices.addAll(graph.vertexSet());
       
            for (Concept vertex : vertices) {
                if(vertex.equals(arg)){        //cioè se ho trovato il concetto nel typegraph
                    Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
                    for (DefaultEdge edge : relatedEdges) {
                        source = graph.getEdgeSource(edge);
                        target = graph.getEdgeTarget(edge);
                        if(source.equals(vertex))
                            supertipi.add(target);     
                    }
                }
            }
            
            return supertipi;
        }
    }

    private void forceUniqueRoot(){
    	for(Concept c : findRoots()){
    		if(!c.getURI().equals("http://www.w3.org/2002/07/owl#Thing")){
    			c.setDepth(1);
    			if(!graph.containsVertex(new Concept("http://www.w3.org/2002/07/owl#Thing")))
    				graph.addVertex(new Concept("http://www.w3.org/2002/07/owl#Thing"));
    			addEdge(c, returnV(new Concept("http://www.w3.org/2002/07/owl#Thing")));
    		}
    	}
    }
    
    
	
//--------------------------------------------------------- UTILS ------------------------------------------------------------------------------	
   
	//per OGNI nodo v del grafo stampa una riga per ogni padre, del tipo: "v  padre(v)". Se v è orfano stampa la riga "v"
	public void stampatypeGraphSuFile(String nomeFile){	
		try{
			FileOutputStream fos = new FileOutputStream(new File(nomeFile));
			
			Concept source, target;
			Set<Concept> vertices = new HashSet<Concept>();
			vertices.addAll(graph.vertexSet());
   
			for (Concept vertex : vertices) {
				boolean orfano = true;
				Set<DefaultEdge> relatedEdges = graph.edgesOf(vertex);
				for (DefaultEdge edge : relatedEdges) {
                    source = graph.getEdgeSource(edge);
                    target = graph.getEdgeTarget(edge);
                    if(source.equals(vertex)){
                        fos.write((source.getURI()+"$$"+source.getDepth() +"  "+ target.getURI()+"$$"+target.getDepth()+"\n").getBytes());
                        orfano = false;
                    }
                }
				if(orfano)
					fos.write((vertex.getURI()+"$$"+vertex.getDepth()+"\n").getBytes());
			}
			fos.close();
		
		}
		catch(Exception e){
			System.out.println("Eccezione stampatypeGraphSuFile");
		}
	}	
		
    
	public void disegna(){
		JgraphGUI gui = new JgraphGUI(graph);
		JFrame frame = new JFrame();
		frame.getContentPane().add(gui);
		frame.setTitle("Type Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
