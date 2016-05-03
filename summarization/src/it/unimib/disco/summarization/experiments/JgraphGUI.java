package it.unimib.disco.summarization.experiments;

import java.awt.*;
import java.awt.geom.*;
import java.util.Map;
 
import javax.swing.*;
 
import org.jgraph.*;
import org.jgraph.graph.*;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import org.jgrapht.graph.DefaultEdge;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
 
 
public class JgraphGUI extends JApplet{
   
    private static final long serialVersionUID = 3256444702936019250L;
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(5000, 7000);
 
    private JGraphModelAdapter<Pattern, DefaultEdge> jgAdapter;
    ListenableGraph<Pattern, DefaultEdge> g;
   
    public JgraphGUI(DirectedGraph grafo){
        g = new ListenableSimpleDirectedGraph<Pattern, DefaultEdge>(grafo);
        jgAdapter = new JGraphModelAdapter<Pattern, DefaultEdge>(g);    
        this.init();
    }
 
   
    public void init(){
        JGraph jgraph = new JGraph(jgAdapter);
        adjustDisplaySettings(jgraph);getContentPane();
        getContentPane().add(new JScrollPane(jgraph));
        resize(DEFAULT_SIZE);
       
        final  JGraphFastOrganicLayout hir = new JGraphFastOrganicLayout();
        final JGraphFacade graphFacade = new JGraphFacade(jgraph);      
        hir.run(graphFacade);
                final Map nestedMap = graphFacade.createNestedMap(true, true);
                jgraph.getGraphLayoutCache().edit(nestedMap);
 
    }
 
    private void adjustDisplaySettings(JGraph jg){
        jg.setPreferredSize(DEFAULT_SIZE);
 
        Color c = DEFAULT_BG_COLOR;
        String colorStr = null;
 
        try {
            colorStr = getParameter("bgcolor");
        } catch (Exception e) {
        }
 
        if (colorStr != null) {
            c = Color.decode(colorStr);
        }
 
        jg.setBackground(c);
    }
 
   
    private void positionVertexAt(Object vertex, int x, int y){
        DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        Rectangle2D bounds = GraphConstants.getBounds(attr);
 
        Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight());
 
        GraphConstants.setBounds(attr, newBounds);
 
        // TODO: Clean up generics once JGraph goes generic
        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, attr);
        jgAdapter.edit(cellAttr, null, null, null);
    }
 
   
 
    /**
     * a listenable directed multigraph that allows loops and parallel edges.
     */
    private static class ListenableSimpleDirectedGraph<V, E> extends DefaultListenableGraph<V, E> implements DirectedGraph<V, E>{
        private static final long serialVersionUID = 1L;
 
        ListenableSimpleDirectedGraph(DirectedGraph directGraph){
            super(directGraph);
        }
    }
   
   
   
}