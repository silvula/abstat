package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.PropertyGraph;
import it.unimib.disco.summarization.ontology.TypeGraphExperimental;


public class PGSpecial {

	private DirectedAcyclicGraph<Pattern, DefaultEdge> PG = new DirectedAcyclicGraph<Pattern, DefaultEdge>(DefaultEdge.class);
	private PropertyGraph propertyGraph;
	private TypeGraphExperimental typeGraph;
	private String type;
	
	
	public PGSpecial(File ontology, String type){
		typeGraph = new TypeGraphExperimental(ontology);
		propertyGraph = new PropertyGraph(ontology);
		this.type = type;
		
		propertyGraph.linkToTheoreticalProperties();
	 }
	

	public void addPatterns(File file) {
		HashSet<Pattern> file_patterns = new HashSet<Pattern>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			
			//ogni pattern letto in file viene aggiunto al PG
			while ((line = br.readLine()) != null) {
				if(!line.equals("")){
					String[] splitted = line.split("##");
					String s = splitted[0];
					String p = splitted[1];
					String o = splitted[2];
					int freq = Integer.parseInt(splitted[3]);
					int numIstanze = Integer.parseInt(splitted[4]);
						
					Concept sConcept = typeGraph.returnV(new Concept(s));
					if(sConcept ==  null)
						sConcept = new Concept(s);
						
					Concept oConcept = typeGraph.returnV(new Concept(o));
					if(oConcept ==  null)
						oConcept = new Concept(o);
						
					Property property = propertyGraph.returnV( propertyGraph.createProperty(p));
					if(property == null){
						property = propertyGraph.createProperty(p);
                        ////per evitare i null//    PG.getPropertyGraph().getGraph().addVertex(property);
						propertyGraph.linkExternalProperty(property, type); 
					}
					
					Pattern pattern = new Pattern(sConcept, p, oConcept);
					pattern.setFreq(freq);
					pattern.setInstances(numIstanze);
					PG.addVertex(pattern);
					
					file_patterns.add(pattern);
				}
			}
			
			br.close();
		}
		catch(Exception e){
			Events.summarization().error(file, e);
		} 
		
		//ogni pattern è mandato all'algoritmo per inferenza su predicati & aggiornamento verticale dei contatori
		for(Pattern p : file_patterns)
			func(p);
	}
	 

	public void func(Pattern pattern) {

		try{
			inferisciEintegra_vertical(pattern);	
		}
		catch(Exception e){
			Events.summarization().error("Inference error: "+ pattern.toString(), e);
		}
		pattern = returnV(pattern);

		try{
			conta(pattern);
		}
		catch(Exception e){
			Events.summarization().error("Counting instances number error: "+ pattern, e);
		}		
	}

    
	/*Collega un pattern con tutti i suoi superpattern in "verticale", se ne genera nuovi li aggiunge e continua a inferire fino ai top properties*/
	private void inferisciEintegra_vertical(Pattern p) throws Exception{
	    PG.addVertex(p);   ///////SUPERFLUO?
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
	        superProperties = propertyGraph.superPropertiesFull(pred, type);
	        for(String superProp : superProperties){
	        	Pattern newPattern = new Pattern(subj, superProp, obj );
	                		
	        	if(PG.containsVertex(newPattern)== false){  
	        		PG.addVertex(newPattern);
	                PG.addEdge(currentP, newPattern);
	                queue.put(newPattern);
	            }
	            else
	                PG.addEdge(currentP,returnV(newPattern));
	        }
	    }
	}
	

/*aggiorna la frequenza dei pattern in input e aggiorna i contatori di istantze ditutti i superpattern di un di AKP.
 *N.B. L'inferenza è stata fatta in verticale, quindi ovviamente la rel. di superpattern in orizzontale non è 
 *rappresentata e quindi non può avvenire un incremento in orizzontale*/
  private void conta(Pattern pattern) throws Exception {
		  incremento(pattern);
    	  ripristinaColori(pattern);
  }
 
 /*Aggiorna numIstanze di tutti i superpattern di p di colore bianco */
  private void incremento(Pattern p) throws Exception{
      if(PG.containsVertex(p) && p.getColor().equals("B")){
          LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();   
          p.setColor("G");
          queue.put(p);
          int aumento = p.getInstances();
          while(queue.size() != 0){
              Pattern currentP = queue.poll();
              
              for(DefaultEdge edge : PG.outgoingEdgesOf(currentP)){
            	  Pattern target = PG.getEdgeTarget(edge);
            	  if (target.getColor().equals("B") && !queue.contains(target)){
                      target.setColor("G");
                      target.setInstances(target.getInstances() + aumento);
                      queue.put(target); 
                  }
              }
              currentP.setColor("N");  
              
          }
         
      }
  }
  

/* Ripristina il colore di tutti i superpattern di un dato pattern p*/
  private  void ripristinaColori(Pattern p) throws Exception{
      if(PG.containsVertex(p)){
          LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();
          queue.put(p);  
          while(queue.size() != 0){
              Pattern currentP = queue.poll();
              if(!currentP.getColor().equals("B")){
            	  
            	  for(DefaultEdge edge : PG.outgoingEdgesOf(currentP)){
                	  Pattern target = PG.getEdgeTarget(edge);
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

  
//------------------------------------------------------------ SECONDARI  -------------------------------------------------------------

	
	/*Riceve un input un Pattern. L'algoritmo cerca un vertice nel grafo che
	*abbia le stesse caratteristiche, se lo trova torna QUEL vertice*/
	private  Pattern returnV(Pattern p){
		Set<Pattern> vertices = new HashSet<Pattern>();
	    vertices.addAll(PG.vertexSet());
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
			vertices.addAll(PG.vertexSet());
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
			vertices.addAll(PG.vertexSet());
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


}
