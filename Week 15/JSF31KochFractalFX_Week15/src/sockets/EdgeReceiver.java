/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import calculate.Edge;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Rob
 */
public class EdgeReceiver implements Observer{

    private ArrayList<Edge> edges;

    public EdgeReceiver() {
        edges = new ArrayList<>();
    }

    /**
     * Gets the edges of the receiver
     *
     * @return List of edges, not null
     */
    public ArrayList<Edge> getEdges() {
        return this.edges;
    }
    
    /**
     * Gets the count of edges
     * @return 
     */
    public synchronized int getEdgeCount() {
        return this.edges.size();
    }
    
    /**
     * Addes a edge to the list
     * @param edge The edge to add
     */
    public synchronized void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        addEdge((Edge)arg);
    }
}
