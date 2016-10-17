package it.unimib.disco.summarization.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.PropertyGraph;
import it.unimib.disco.summarization.ontology.TypeGraphExperimental;

import java.io.*;

public class PatternGraph {

	private DirectedAcyclicGraph<Pattern, DefaultEdge> patternGraph2 = new DirectedAcyclicGraph<Pattern, DefaultEdge>(DefaultEdge.class);
	private PropertyGraph propertyGraph;
	private TypeGraphExperimental typeGraph;
	private String type;
	private boolean full_inference;
	 

	public PatternGraph(File ontology, String type, boolean full_inference){
		typeGraph = new TypeGraphExperimental(ontology);
		propertyGraph = new PropertyGraph(ontology);
		
		if(full_inference)
			propertyGraph.linkToTheoreticalProperties();
		
		this.type = type;
		this.full_inference = full_inference;
	 }
	 
	
	
    /*Entrypoint del processo di creazione del pattern graph e del conteggio delle istanze.
     * Riceve in ingresso un insieme di AKP sotto forma di array */
	public void contatoreIstanze(Pattern[] patterns) {
		for(int i = 0; i<(patterns.length); i++){
			try{
				inferisciEintegra(patterns[i]);	
			}
			catch(Exception e){
				Events.summarization().error("Inference error: "+ patterns[i].toString(), e);
			}
			
			patterns[i] = returnV(patterns[i]);
		}
		
		try{
			conta(patterns);
		}
		catch(Exception e){
			Events.summarization().error("Counting instances number error: "+ patterns, e);
		}		
	}
	
	    
    /*Dato un Pattern INTEGRA il grafo dei suoi superpattern con il grafo PatternGraph*/
    private void inferisciEintegra(Pattern p) throws Exception{
    	if(!patternGraph2.containsVertex(p)){
        	patternGraph2.addVertex(p);
            String  pred;
            Concept subj, obj;
           
            LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();
            queue.put(p);
            while(queue.size() != 0 ){
                Pattern currentP = queue.poll();
                subj = currentP.getSubj();
                obj = currentP.getObj();
                pred = currentP.getPred();
                
                
                if(full_inference){
                	
	                ArrayList<String> superProperties;
	                superProperties = propertyGraph.superPropertiesFull(pred, type);
	                for(String superProp : superProperties){
	                	Pattern newPattern3 = new Pattern(subj, superProp, obj );
	                		
	                	if(patternGraph2.containsVertex(newPattern3)== false){
	                		patternGraph2.addVertex(newPattern3);
	                		patternGraph2.addEdge(currentP, newPattern3);
	                		queue.put(newPattern3);
	                	}
	                	else
	                		patternGraph2.addEdge(currentP,returnV(newPattern3));
	                }
	                
                }
                
                
                ArrayList<Concept> subjSupertipi = typeGraph.superTipo(subj, type, "subject");
                if(subjSupertipi != null){
                	for(Concept subjSup : subjSupertipi){
                    	Pattern newPattern1 = new Pattern(subjSup, pred, obj );
                		
                    	if(patternGraph2.containsVertex(newPattern1)== false){//containVertex dice se esiste un vertice EQUIVALENTE(quindi un oggetto qualsiasi che abbia i suoi stessi parametri)
                    		patternGraph2.addVertex(newPattern1);
                    		patternGraph2.addEdge(currentP, newPattern1);
                        	queue.put(newPattern1);
                    	}
                    	else
                    		patternGraph2.addEdge(currentP, returnV(newPattern1));	//non voglio creare la relazione con il pattern che ho creato, ma con quello equivalente in MTG.         
                	}
                }

                ArrayList<Concept> objSupertipi = typeGraph.superTipo(obj, type, "object");
                if(objSupertipi!=null){
                	for(Concept objSup : objSupertipi){
                		Pattern newPattern2 = new Pattern(subj, pred, objSup );
                		
                		if(patternGraph2.containsVertex(newPattern2)== false){
                    		patternGraph2.addVertex(newPattern2);
                			patternGraph2.addEdge(currentP, newPattern2);
                	
                			queue.put(newPattern2);
                		}
                		else
                			patternGraph2.addEdge(currentP,returnV(newPattern2));
                	}
                }
                
                
            }
        
    	}
    }
	
    /*aggiorna la frequenza dei pattern in input e aggiorna i contatori di istantze di
     *tutti i superpattern di un insieme di AKP*/
      private void conta(Pattern[] patterns) throws Exception {
    	  for (Pattern AKP : patterns)
    		  incremento(AKP);
          for (Pattern AKP : patterns)
        	  ripristinaColori(AKP);
      }
     
     /*Aggiorna la frequenza della radice e il contatore di tutti i superpattern di p di colroe bianco */
      private void incremento(Pattern p) throws Exception{
          if(patternGraph2.containsVertex(p) && p.getColor().equals("B")){
              p.setFreq(p.getFreq()+1);
              LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();   
              p.setColor("G");
              p.setInstances(p.getInstances()+1);
              queue.put(p);
              while(queue.size() != 0){
                  Pattern currentP = queue.poll();
                  
                  for(DefaultEdge edge : patternGraph2.outgoingEdgesOf(currentP)){
                	  Pattern target = patternGraph2.getEdgeTarget(edge);
                      if (target.getColor().equals("B") && !queue.contains(target)){
                          target.setColor("G");
                          target.setInstances(target.getInstances()+1);
                          queue.put(target); 
                      }
                  }
                  currentP.setColor("N");  
                  
              }
             
          }
      }
      
    
    /* Ripristina il colore di tutti i superpattern di un dato pattern p
     *l'input p deve essere un vertice del grafo, NON un suo fac-simile(altrimenti non colora p)*/
      private  void ripristinaColori(Pattern p) throws Exception{
          if(patternGraph2.containsVertex(p)){
              LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();
              queue.put(p);  
              while(queue.size() != 0){
                  Pattern currentP = queue.poll();
                  if(!currentP.getColor().equals("B")){
                          
                	  for(DefaultEdge edge : patternGraph2.outgoingEdgesOf(currentP)){
                    	  Pattern target = patternGraph2.getEdgeTarget(edge);
                    	  if (!target.getColor().equals("B") && !queue.contains(target))
                              queue.put(target);
                      }
   
                  }
                  currentP.setColor("B");
              }
          }
      }
     
     
      
      public PropertyGraph getPropertyGraph(){
    	  return propertyGraph;
      }

      
      public TypeGraphExperimental getTypeGraph(){
    	  return typeGraph;
      }
      
      
	//------------------------------------------------------------ SECONDARI  -------------------------------------------------------------
    
	
	/*Riceve un input un Pattern. L'algoritmo cerca un vertice nel grafo che
	*abbia le stesse caratteristiche, se lo trova torna QUEL vertice*/
	private  Pattern returnV(Pattern p){
		Set<Pattern> vertices = new HashSet<Pattern>();
	    vertices.addAll(patternGraph2.vertexSet());
	    for (Pattern vertex : vertices)    
	    	if(p.equals(vertex))
	    		return vertex; 
	    return null;
	}
	

	
	//----------------------------------------------------------- UTILS -------------------------------------------------------------------------
	public void stampaPatternsSuFile(String patterns_file, String HEADpatterns_file){
		try{
			FileOutputStream fos = new FileOutputStream(new File(patterns_file), true);
			FileOutputStream fosHEADpatterns = new FileOutputStream(new File(HEADpatterns_file), true);
			Set<Pattern> vertices = new HashSet<Pattern>();
			vertices.addAll(patternGraph2.vertexSet());
			for (Pattern vertex : vertices){
				Property pred = propertyGraph.returnV(vertex.getPred());
				
				if(pred.getURI().contains("http://www.w3.org/2002/07/owl#top"))
					fosHEADpatterns.write( (vertex.getSubj()+"##"+ pred + "##"+vertex.getObj()+"##"+ vertex.getFreq()+"##"+ vertex.getInstances()+"\n").getBytes()  );
				else
					fos.write( (vertex.getSubj()+"##"+ pred + "##"+vertex.getObj()+"##"+ vertex.getFreq()+"##"+ vertex.getInstances()+"\n").getBytes()  );
			}
			fos.close();
			fosHEADpatterns.close();
		}
		catch(Exception e){
			Events.summarization().error("Inferred patterns print error", e);
		}
	}
	
	public void stampaPatternsSuFile(String patterns_file){
		try{
			FileOutputStream fos = new FileOutputStream(new File(patterns_file), true);
			Set<Pattern> vertices = new HashSet<Pattern>();
			vertices.addAll(patternGraph2.vertexSet());
			for (Pattern vertex : vertices){
				Property pred = propertyGraph.returnV(vertex.getPred());
				fos.write( (vertex.getSubj()+"##"+ pred + "##"+vertex.getObj()+"##"+ vertex.getFreq()+"##"+ vertex.getInstances()+"\n").getBytes()  );
			}
			fos.close();
		}
		catch(Exception e){
			Events.summarization().error("Inferred patterns print error", e);
		}
	}

	
	
	public void disegna(){
		JgraphGUI gui = new JgraphGUI(patternGraph2);
		JFrame frame = new JFrame();
		frame.getContentPane().add(gui);
		frame.setTitle("Minimal Pattern Base Transitive Closure Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	//Ritorna tutti i vertici orfani, ovvero senza un padre
	public HashSet<Pattern> findRoots(){
		HashSet<Pattern> orfani = new HashSet<Pattern>();
		
		Set<Pattern> vertices = new HashSet<Pattern>();
	    vertices.addAll(patternGraph2.vertexSet());
	    for (Pattern vertex : vertices) { 
	    	boolean isOrphan = true;
	    	Set<DefaultEdge> relatedEdges = patternGraph2.edgesOf(vertex);
			for (DefaultEdge edge : relatedEdges) {
				if(patternGraph2.getEdgeSource(edge).equals(vertex))
					isOrphan = false;
			}
			if(isOrphan)
				orfani.add(vertex);
	    }
	    return orfani;   
	}
		
	    
}