package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;


import it.unimib.disco.summarization.dataset.Files;
import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.PropertyGraph;
import it.unimib.disco.summarization.ontology.TypeGraphExperimental;

public class TopPatternGraph {
	private DirectedAcyclicGraph<Pattern, DefaultEdge> topPatternGraph = new DirectedAcyclicGraph<Pattern, DefaultEdge>(DefaultEdge.class);
	private PropertyGraph propertyGraph;
	private TypeGraphExperimental typeGraph;
	private String type;
	private File output_f;
	private File patternsContribuentiRoot;
	
	public TopPatternGraph(File ontology, String type, File output_f){
		typeGraph = new TypeGraphExperimental(ontology);
		propertyGraph = new PropertyGraph(ontology);

		propertyGraph.linkToTheoreticalProperties();
		this.type = type;
		this.output_f = output_f;
		if(type.equals("datatype"))
			this.patternsContribuentiRoot = new File("datatypePatternsContribuentiRoot.txt");
		else
			this.patternsContribuentiRoot = new File("objectpePatternsContribuentiRoot.txt");
	 }
	
	
	public void readAKPs(File headAKPs_splitted_dir, String suffix) throws Exception{
		for(final File file : new Files().get(headAKPs_splitted_dir, suffix)){
			
			ArrayList<Pattern> patternsList = new ArrayList<Pattern>();
			try{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					if(!line.equals("")){
							String[] splitted = line.split("##");
							String s = splitted[0];
							String p = splitted[1];
							String o = splitted[2];
							int freq = Integer.parseInt(splitted[3]);
							int numIStanze = Integer.parseInt(splitted[4]);
							Pattern pattern = new Pattern( new Concept(s), p, new Concept(o));
							pattern.setFreq(freq);
							pattern.setInstances(numIStanze);
							
							patternsList.add(pattern);
					}
				}
				br.close();
			}
			catch(Exception e){
				Events.summarization().error(headAKPs_splitted_dir, e);
			}
			
			contatoreIstanze(patternsList);
			
		}
		
		//linkToTheTop();   //non ha effetto nell'output finale ma collega tutti i vertici alla radice
		
		stampaPatternsSuFile();	
	}
	
	
	public void contatoreIstanze(ArrayList<Pattern> patterns) throws Exception {
		for(int i = 0; i<(patterns.size() ); i++){
			try{
				inferisciEintegra(patterns.get(i));	
			}
			catch(Exception e){
				Events.summarization().error("Inference error: "+ patterns.get(i).toString(), e);
			}
		}
		
		for(Pattern vertex : topPatternGraph.vertexSet())
			returnV_graph(vertex).setColor("B");
		for(Pattern p : patterns)
			topPatternGraph.removeVertex(returnV_graph(p));
			
	}
	
	
    /*Viene fatta infrenza solo sui predicati, */
    private void inferisciEintegra(Pattern p) throws Exception{
    	FileOutputStream fos = new FileOutputStream(this.patternsContribuentiRoot, true);
    	
    	topPatternGraph.addVertex(p);
        String  pred;
        Concept subj, obj;
       
        LinkedBlockingQueue<Pattern> queue = new LinkedBlockingQueue<Pattern>();
        queue.put(p);
        while(queue.size() != 0 ){
            Pattern currentP = queue.poll();
            subj = currentP.getSubj();
            obj = currentP.getObj();
            pred = currentP.getPred();
            
            
            ArrayList<String> superProperties = null;
            if(type.equals("datatype")){
            	if(!pred.equals("http://www.w3.org/2002/07/owl#topDataProperty")){
            		superProperties = new ArrayList<String>();
            		superProperties.add("http://www.w3.org/2002/07/owl#topDataProperty");
            	}
            }
            else{
            	if(!pred.equals("http://www.w3.org/2002/07/owl#topObjectProperty")){
            		superProperties = new ArrayList<String>();
            		superProperties.add("http://www.w3.org/2002/07/owl#topObjectProperty");
            	}
            }
            
   //         ArrayList<String> superProperties = propertyGraph.superPropertiesFull(pred, type);
            if(superProperties!=null){
            	for(String superProp : superProperties){
            		Pattern newPattern3 = new Pattern(subj, superProp, obj );
            		
            		
            		
    		
            		if(topPatternGraph.containsVertex(newPattern3)== false){

            			newPattern3.setFreq(0);
                		newPattern3.setInstances(currentP.getInstances());
                		newPattern3.setColor("N");
              
            			topPatternGraph.addVertex(newPattern3);
            			topPatternGraph.addEdge(currentP, newPattern3);
            			
           // 			if(currentP.getSubj().getURI().equals("http://www.w3.org/2002/07/owl#Thing") && (currentP.getObj().getURI().equals("http://www.w3.org/2002/07/owl#Thing") || currentP.getObj().getURI().equals("http://www.w3.org/2000/01/rdf-schema#Literal")) )
            //    			fos.write((currentP.getSubj()+"##"+currentP.getPred()+"##"+currentP.getObj()+"##"+currentP.getFreq()+"##"+currentP.getInstances()+"\n").getBytes());

            		}
            		else{
            			if(!returnV_graph(newPattern3).getColor().equals("N")){
            				returnV_graph(newPattern3).setColor("N");
            				returnV_graph(newPattern3).setInstances( returnV_graph(newPattern3).getInstances() + currentP.getInstances());
            				
           // 				if(currentP.getSubj().getURI().equals("http://www.w3.org/2002/07/owl#Thing") && (currentP.getObj().getURI().equals("http://www.w3.org/2002/07/owl#Thing") || currentP.getObj().getURI().equals("http://www.w3.org/2000/01/rdf-schema#Literal")) )
            //        			fos.write((currentP.getSubj()+"##"+currentP.getPred()+"##"+currentP.getObj()+"##"+currentP.getFreq()+"##"+currentP.getInstances()+"\n").getBytes());
            			}
            			topPatternGraph.addEdge(currentP,returnV_graph(newPattern3));
            			
            			
            		}
            	}
            }
            
        }
        fos.close();
    }
    
    public void linkToTheTop(){
    	for(Pattern currentP : topPatternGraph.vertexSet()){
    		String  pred;
    	    Concept subj, obj;
            subj = currentP.getSubj();
            obj = currentP.getObj();
            pred = currentP.getPred();
            
    		ArrayList<Concept> subjSupertipi = typeGraph.superTipo(subj, type, "subject");
            if(subjSupertipi != null){
            	for(Concept subjSup : subjSupertipi){
                	Pattern newPattern1 = new Pattern(subjSup, pred, obj );
            		
                	if(topPatternGraph.containsVertex(newPattern1))
                		topPatternGraph.addEdge(currentP, returnV_graph(newPattern1));         	
            	}
            }

            ArrayList<Concept> objSupertipi = typeGraph.superTipo(obj, type, "object");
            if(objSupertipi!=null){
            	for(Concept objSup : objSupertipi){
            		Pattern newPattern2 = new Pattern(subj, pred, objSup );
            		
            		if(topPatternGraph.containsVertex(newPattern2))
            			topPatternGraph.addEdge(currentP,returnV_graph(newPattern2));
            	}
            }
            
    	}
              
    }
	
	
	//------------------------------------------------------------ SECONDARI  -------------------------------------------------------------
    
	
	/*Riceve un input un Pattern. L'algoritmo cerca un vertice nel grafo che
	*abbia le stesse caratteristiche, se lo trova torna QUEL vertice*/
	private  Pattern returnV_graph(Pattern p){
		Set<Pattern> vertices = new HashSet<Pattern>();
	    vertices.addAll(topPatternGraph.vertexSet());
	    for (Pattern vertex : vertices)    
	    	if(p.equals(vertex))
	    		return vertex; 
	    return null;
	}
	
	
	//----------------------------------------------------------- UTILS -------------------------------------------------------------------------
	public void stampaPatternsSuFile(){
		try{
			FileOutputStream fos = new FileOutputStream(output_f, true);
			Set<Pattern> vertices = new HashSet<Pattern>();
			vertices.addAll(topPatternGraph.vertexSet());
			for (Pattern vertex : vertices)    
				fos.write( (vertex.getSubj().getURI()+"##"+vertex.getPred()+"##"+vertex.getObj().getURI()+"##"+ vertex.getFreq()+"##"+ vertex.getInstances()+"\n").getBytes()  );
			fos.close();
		}
		catch(Exception e){
			Events.summarization().error("Inferred patterns print error", e);
		}
	}
	
	//Ritorna tutti i vertici orfani, ovvero senza un padre
	public HashSet<Pattern> findRoots(){
		HashSet<Pattern> orfani = new HashSet<Pattern>();
		
		Set<Pattern> vertices = new HashSet<Pattern>();
	    vertices.addAll(topPatternGraph.vertexSet());
	    for (Pattern vertex : vertices) { 
	    	boolean isOrphan = true;
	    	Set<DefaultEdge> relatedEdges = topPatternGraph.edgesOf(vertex);
			for (DefaultEdge edge : relatedEdges) {
				if(topPatternGraph.getEdgeSource(edge).equals(vertex))
					isOrphan = false;
			}
			if(isOrphan)
				orfani.add(vertex);
	    }
	    return orfani;   
	}
	
	
	public void disegna(){
		JgraphGUI gui = new JgraphGUI(topPatternGraph);
		JFrame frame = new JFrame();
		frame.getContentPane().add(gui);
		frame.setTitle("Minimal Pattern Base Transitive Closure Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	
}
