package it.unimib.disco.summarization.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;


import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;


import it.unimib.disco.summarization.ontology.ConceptExtractor;
import it.unimib.disco.summarization.ontology.Concepts;
import it.unimib.disco.summarization.ontology.Model;
import it.unimib.disco.summarization.ontology.OntologySubclassOfExtractor;

import java.io.*;

public class PatternGraph {

	 DirectedAcyclicGraph<Concept, DefaultEdge> typeGraph = new DirectedAcyclicGraph<Concept, DefaultEdge>(DefaultEdge.class);
	 DirectedAcyclicGraph<Pattern, DefaultEdge> patternGraph = new DirectedAcyclicGraph<Pattern, DefaultEdge>(DefaultEdge.class);

	public void createTypeGraph(File ontology){			
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
			addEdge(tipo, supertipo);
		}
	}
	 
	/*Legge un file txt dove per ogni relational assert nel dataset esiste una riga con i suoi AKP*/
	public void readTriplesAKPs(String AKPsFile) throws Exception{
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
						AKPs[i] = new Pattern( new Concept(s), p, new Concept(o));
					}
		    		
					contatoreIstanze(AKPs);
				}
			}
			br.close();
		}
		catch(Exception e){System.out.println(e);}  
	}
	 
	
	
 /*Entrypoint del processo di creazione del pattern graph e del conteggio delle istanze.
* Riceve in ingresso un insieme di AKP sotto forma di array */
	private void contatoreIstanze(Pattern[] patterns) {
		try{
			for(int i = 0; i<(patterns.length ); i++){
				inferisciEintegra(patterns[i]);	
				patterns[i] = returnV_patternGraph(patterns[i]);
			}
			conta(patterns);
		}
		catch(Exception e) {System.out.println(e);}
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
            
            ArrayList<Concept> subjSupertipi = SuperTipo(subj);
            if(subjSupertipi != null){
            	for(Concept subjSup : subjSupertipi){
                	Pattern newPattern1 = new Pattern(subjSup, pred, obj );
                	if(patternGraph.containsVertex(newPattern1)== false){//containVertex dice se esiste un vertice EQUIVALENTE(quindi un oggetto qualsiasi che abbia i suoi stessi parametri)
                		patternGraph.addVertex(newPattern1);
                		patternGraph.addEdge(currentP, newPattern1);
                    	queue.put(newPattern1);
                	}
                	else
                		patternGraph.addEdge(currentP, returnV_patternGraph(newPattern1));	//non voglio creare la relazione con il pattern che ho creato, ma con quello equivalente in MTG. 
            	}
            }

            ArrayList<Concept> objSupertipi = SuperTipo(obj);
            if(objSupertipi!=null){
            	for(Concept objSup : objSupertipi){
            		Pattern newPattern2 = new Pattern(subj, pred, objSup );
            		if(patternGraph.containsVertex(newPattern2)== false){
            			patternGraph.addVertex(newPattern2);
            			patternGraph.addEdge(currentP, newPattern2);
            	
            			queue.put(newPattern2);
            		}
            		else
            			patternGraph.addEdge(currentP,returnV_patternGraph(newPattern2));
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
     
      
      
      
	//------------------------------------------------------------ SECONDARI  -------------------------------------------------------------
	
	/*Ritorna il supertipo del concetto in input*/
    private ArrayList<Concept> SuperTipo(Concept arg){

        if(arg.getURI().equals("http://www.w3.org/2002/07/owl#Thing"))
            return null;
        else{
        	ArrayList<Concept> supertipi = new ArrayList<Concept>();
            Concept source, target;
            Set<Concept> vertices = new HashSet<Concept>();
            vertices.addAll(typeGraph.vertexSet());
       
            for (Concept vertex : vertices) {
                if(vertex.equals(arg)){        //cioè se ho toato il concetto nel typegraph
                    Set<DefaultEdge> relatedEdges = typeGraph.edgesOf(vertex);
                    for (DefaultEdge edge : relatedEdges) {
                        source = typeGraph.getEdgeSource(edge);
                        target = typeGraph.getEdgeTarget(edge);
                        if(source.equals(vertex))
                            supertipi.add(target);     
                    }
                }
            }
            
            return supertipi;
        }
    }
	
	
	private void addEdge(Concept tipo, Concept supertipo){
		if(!typeGraph.containsVertex(tipo))
			typeGraph.addVertex(tipo);
		
		if(typeGraph.containsVertex(supertipo))
			typeGraph.addEdge(tipo, returnV_typeGraph(supertipo));
		else{
			typeGraph.addVertex(supertipo);
			typeGraph.addEdge(tipo, supertipo);
		}
	}
	
	
	
	private Concept returnV_typeGraph(Concept p){
		Set<Concept> vertices = new HashSet<Concept>();
	    vertices.addAll(typeGraph.vertexSet());
	    for (Concept vertex : vertices)    
	    	if(p.equals(vertex))
	    		return vertex; 
	    return null;
	}
	
	
	  /*Riceve un input un Pattern. L'algoritmo cerca un vertice nel grafo che
	   *abbia le stesse caratteristiche, se lo trova torna QUEL vertice*/
	private  Pattern returnV_patternGraph(Pattern p){
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
			FileOutputStream fos = new FileOutputStream(new File(nomeFile));
			Set<Pattern> vertices = new HashSet<Pattern>();
			vertices.addAll(patternGraph.vertexSet());
			for (Pattern vertex : vertices)    
				fos.write( (vertex.getSubj().getURI()+"##"+vertex.getPred()+"##"+vertex.getObj().getURI()+"##"+ vertex.getFreq()+"##"+ vertex.getInstances()+"\n").getBytes()  );
			fos.close();
		}
		catch(Exception e){
			System.out.println("Eccezione stampaPatternsSuFile");
		}
	}
	
	
	public void stampaGrafoSuFile(String nomeFile){
		try{
			FileOutputStream fos = new FileOutputStream(new File(nomeFile));
			fos.write(patternGraph.toString().getBytes());
			fos.close();
		}
		catch(Exception e){
			System.out.println("Eccezione stampaGrafoSuFile");
		}
	}
	

	
	public void stampatypeGraphSuFile(String nomeFile){	
		try{
			FileOutputStream fos = new FileOutputStream(new File(nomeFile));
			
			Concept source, target;
			Set<Concept> vertices = new HashSet<Concept>();
			vertices.addAll(typeGraph.vertexSet());
   
			for (Concept vertex : vertices) {
        	Set<DefaultEdge> relatedEdges = typeGraph.edgesOf(vertex);
                for (DefaultEdge edge : relatedEdges) {
                    source = typeGraph.getEdgeSource(edge);
                    target = typeGraph.getEdgeTarget(edge);
                    if(source.equals(vertex))
                        fos.write((source.getURI() +"  "+ target.getURI()+"\n").getBytes());    
                }
			}
			fos.close();
		
		}
		catch(Exception e){
			System.out.println("Eccezione stampatypeGraphSuFile");
		}
	}	
		
	    
}