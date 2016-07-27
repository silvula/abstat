package it.unimib.disco.summarization.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntProperty;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.PropertyGraph;
import it.unimib.disco.summarization.ontology.TypeGraphExperimental;

import java.io.*;

public class PatternGraph {

	private DirectedAcyclicGraph<Pattern, DefaultEdge> patternGraph = new DirectedAcyclicGraph<Pattern, DefaultEdge>(DefaultEdge.class);
	private PropertyGraph propertyGraph;
	private TypeGraphExperimental typeGraph;
	private String type;
	private HashSet<String> rootProperties = new HashSet<String>();
	private boolean splitMode;
	 

	public PatternGraph(File ontology, String type, boolean splitMode){
		typeGraph = new TypeGraphExperimental(ontology);
		propertyGraph = new PropertyGraph(ontology);
		
		//se sto costruendo solo la base del patterngraph in splitMode
		if(splitMode)
			for(OntProperty root : propertyGraph.findRoots())
				rootProperties.add(root.getURI());
		//se invece sto costruendo direttamente tutto il pattergraph tenendolo in memoria
		else
			propertyGraph.linkToTheoreticalProperties();

		this.type = type;
		this.splitMode = splitMode;
	 }
	
	
	/*Legge un file txt dove per ogni relational assert nel dataset esiste una riga con i suoi AKP*/
	public void readTriplesAKPs(String AKPsFile) {
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(AKPsFile)));
			String line;
			
			while ((line = br.readLine()) != null) {
				if(!line.equals("")){
					int index = line.indexOf(" [")+1;
					line = line.substring(index);   //per togliere il relational assertion dalla riga.
					
					line =line.substring(1, line.length()-1);
					String[] stringAKPs = line.split(", ");  
					Pattern[] AKPs = new Pattern[stringAKPs.length];
		    		
					for(int i=0; i<stringAKPs.length;i++){
						String[] splitted = stringAKPs[i].split("##");
						String s = splitted[0];
						String p = splitted[1];
						String o = splitted[2];
						
						Concept sConcept = typeGraph.returnV_typeGraph(new Concept(s));
						if(sConcept ==  null){
							sConcept = new Concept(s);
							if(!s.equals("http://www.w3.org/2002/07/owl#Thing"))
								sConcept.setDepth(1);
						}
							
						Concept oConcept = typeGraph.returnV_typeGraph(new Concept(o));
						if(oConcept ==  null){
							oConcept = new Concept(o);
							if(!o.equals("http://www.w3.org/2002/07/owl#Thing") && !o.equals("http://www.w3.org/2000/01/rdf-schema#Literal"))
								oConcept.setDepth(1);
						}
									
						AKPs[i] = new Pattern( sConcept, p, oConcept);
					}
		    		
					contatoreIstanze(AKPs);
				}
			}
			br.close();
		}
		catch(Exception e){
			Events.summarization().error(AKPsFile, e);
		}  
	}
	 
	
	
    /*Entrypoint del processo di creazione del pattern graph e del conteggio delle istanze.
     * Riceve in ingresso un insieme di AKP sotto forma di array */
	public void contatoreIstanze(Pattern[] patterns) {
		for(int i = 0; i<(patterns.length ); i++){
			try{
				inferisciEintegra(patterns[i]);	
			}
			catch(Exception e){
				Events.summarization().error("Inference error: "+ patterns[i].toString(), e);
			}
			
			patterns[i] = returnV_graph(patterns[i]);
		}
		
		try{
			conta(patterns);
		}
		catch(Exception e){
			Events.summarization().error("Counting instances number error: "+ patterns, e);
		}		
	}
	
	    
    /*Dato un Pattern INTEGRA il grafo dei suoi superpattern con il grafo patternGraph*/
    private void inferisciEintegra(Pattern p) throws Exception{
    	patternGraph.addVertex(p);
        String  pred;
        Concept subj, obj;
       
        LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();
        queue.put(p);
        while(queue.size() != 0 ){
            Pattern currentP = queue.poll();
            subj = currentP.getSubj();
            obj = currentP.getObj();
            pred = currentP.getPred();
            
            
            ArrayList<String> superProperties;
            if(splitMode)
            	superProperties = propertyGraph.superProperties(pred);
            else
            	superProperties = propertyGraph.superPropertiesFull(pred, type);
            
            if(superProperties!=null){
            	
            	//aggiorno rootProperties
            	if(splitMode)
            		if(superProperties.size()==0)
            			this.rootProperties.add(pred);
            	
            	for(String superProp : superProperties){
            		Pattern newPattern3 = new Pattern(subj, superProp, obj );
            		
            		if(patternGraph.containsVertex(newPattern3)== false){
            			patternGraph.addVertex(newPattern3);
            			patternGraph.addEdge(currentP, newPattern3);
            			queue.put(newPattern3);
            		}
            		else{
            			patternGraph.addEdge(currentP,returnV_graph(newPattern3));
            		}
            	}
            }
            
            
            ArrayList<Concept> subjSupertipi = typeGraph.superTipo(subj, type, "subject");
            if(subjSupertipi != null){
            	for(Concept subjSup : subjSupertipi){
                	Pattern newPattern1 = new Pattern(subjSup, pred, obj );
            		
                	if(patternGraph.containsVertex(newPattern1)== false){//containVertex dice se esiste un vertice EQUIVALENTE(quindi un oggetto qualsiasi che abbia i suoi stessi parametri)
                		patternGraph.addVertex(newPattern1);
                		patternGraph.addEdge(currentP, newPattern1);
                    	queue.put(newPattern1);
                	}
                	else{
                		patternGraph.addEdge(currentP, returnV_graph(newPattern1));	//non voglio creare la relazione con il pattern che ho creato, ma con quello equivalente in MTG. 
                	}
            	}
            }

            ArrayList<Concept> objSupertipi = typeGraph.superTipo(obj, type, "object");
            if(objSupertipi!=null){
            	for(Concept objSup : objSupertipi){
            		Pattern newPattern2 = new Pattern(subj, pred, objSup );
            		
            		if(patternGraph.containsVertex(newPattern2)== false){
                		patternGraph.addVertex(newPattern2);
            			patternGraph.addEdge(currentP, newPattern2);
            	
            			queue.put(newPattern2);
            		}
            		else{
            			patternGraph.addEdge(currentP,returnV_graph(newPattern2));
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
          if(patternGraph.containsVertex(p) && p.getColor().equals("B")){
              p.setFreq(p.getFreq()+1);
              LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();   
              p.setColor("G");
              p.setInstances(p.getInstances()+1);
              queue.put(p);
              while(queue.size() != 0){
                  Pattern currentP = queue.poll();
                  Set<DefaultEdge> relatedEdges = patternGraph.edgesOf(currentP);
                 
                  for (DefaultEdge edge : relatedEdges) {
                      if(currentP.equals( patternGraph.getEdgeSource(edge) )){
                          Pattern target = patternGraph.getEdgeTarget(edge);
                          if (target.getColor().equals("B") && !queue.contains(target)){
                              target.setColor("G");
                              target.setInstances(target.getInstances()+1);
                              queue.put(target); 
                          }
                      }
                  }
                  currentP.setColor("N");
              }
             
          }
      }
      
    
    /* Ripristina il colore di tutti i superpattern di un dato pattern p
     *l'input p deve essere un vertice del grafo, NON un suo fac-simile(altrimenti non colora p)*/
      private  void ripristinaColori(Pattern p) throws Exception{
          if(patternGraph.containsVertex(p)){
              LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();
              queue.put(p);  
              while(queue.size() != 0){
                  Pattern currentP = queue.poll();
                  if(!currentP.getColor().equals("B")){
                 
                      Set<DefaultEdge> relatedEdges = patternGraph.edgesOf(currentP);
                      for (DefaultEdge edge : relatedEdges) {
                          if(currentP.equals( patternGraph.getEdgeSource(edge) )){
                              Pattern target = patternGraph.getEdgeTarget(edge);                  
                              if (!target.getColor().equals("B") && !queue.contains(target))
                                  queue.put(target);
                          }
                      }
                  }
                  currentP.setColor("B");
              }
          }
      }
     
      
      public void getHeadPatterns(File output_dir ) throws Exception{
    	  FileOutputStream fos;
    	  if(this.type.equals("datatype"))
    		  fos = new FileOutputStream(new File(output_dir.getAbsolutePath() + "/headPatterns_datatype.txt"), true);
    	  else
    		  fos = new FileOutputStream(new File(output_dir.getAbsolutePath() + "/headPatterns_object.txt"), true);
    	  
    	  for(Pattern vertex : this.patternGraph.vertexSet()){
    		  String pred = vertex.getPred();
    		  if(rootProperties.contains(pred))
    				  fos.write( (vertex.getSubj().getURI()+"##"+vertex.getPred()+"##"+vertex.getObj().getURI()+"##"+ vertex.getFreq()+"##"+ vertex.getInstances()+"\n").getBytes());
    	  }
    	  fos.close();
      }

      
	//------------------------------------------------------------ SECONDARI  -------------------------------------------------------------
    
	
	/*Riceve un input un Pattern. L'algoritmo cerca un vertice nel grafo che
	*abbia le stesse caratteristiche, se lo trova torna QUEL vertice*/
	private  Pattern returnV_graph(Pattern p){
		Set<Pattern> vertices = new HashSet<Pattern>();
	    vertices.addAll(patternGraph.vertexSet());
	    for (Pattern vertex : vertices)    
	    	if(p.equals(vertex))
	    		return vertex; 
	    return null;
	}
	

	
	//----------------------------------------------------------- UTILS -------------------------------------------------------------------------
	public void stampaPatternsSuFile(String nomeFile){
		try{
			FileOutputStream fos = new FileOutputStream(new File(nomeFile), true);
			Set<Pattern> vertices = new HashSet<Pattern>();
			vertices.addAll(patternGraph.vertexSet());
			for (Pattern vertex : vertices)    
				fos.write( (vertex.getSubj().toString()+"##"+vertex.getPred()+"##"+vertex.getObj().toString()+"##"+ vertex.getFreq()+"##"+ vertex.getInstances()+"\n").getBytes()  );
			fos.close();
		}
		catch(Exception e){
			Events.summarization().error("Inferred patterns print error", e);
		}
	}
	
	
	public void stampaGrafoSuFile(String nomeFile){
		try{
			FileOutputStream fos = new FileOutputStream(new File(nomeFile));
			fos.write(patternGraph.toString().getBytes());
			fos.close();
		}
		catch(Exception e){
			Events.summarization().error("PatternGraph print error", e);
		}
	}
	
	
	public void disegna(){
		JgraphGUI gui = new JgraphGUI(patternGraph);
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
	    vertices.addAll(patternGraph.vertexSet());
	    for (Pattern vertex : vertices) { 
	    	boolean isOrphan = true;
	    	Set<DefaultEdge> relatedEdges = patternGraph.edgesOf(vertex);
			for (DefaultEdge edge : relatedEdges) {
				if(patternGraph.getEdgeSource(edge).equals(vertex))
					isOrphan = false;
			}
			if(isOrphan)
				orfani.add(vertex);
	    }
	    return orfani;   
	}
	
	
	    
}