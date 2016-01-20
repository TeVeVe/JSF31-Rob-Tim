/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import calculate.Edge;
import java.util.ArrayList;
import java.util.List;
import writekochfractal.w12.WriteKochFractalW12;

/**
 *
 * @author Rob
 */
public class EdgeCalcProtocol {
    private static final int WAITING = 0;
    private static final int SENTLEVEL = 1;
    private static final int SPEED = 2;
    private static final int SLOW = 3;
    private static final int FAST = 4;
    private static final int SENT = 5;

    private WriteKochFractalW12 koch;
    private List<Edge> edges;
    private int state = WAITING;
    private int level = 0;
    
    public String processInput(String input) {
        String output = null;
        
        if (state == WAITING) {
            output = "Server:\t What level kochfractal do you need?";
            state = SENTLEVEL;
            //output = "Server:\t What speed do you want?";
            System.out.println(level);
            System.out.println("WAITING");
        } else if (state == SENTLEVEL) {
            level = Integer.parseInt(input);
            
            System.out.println(level);
            if(level > 0 && level < 13) {
                edges = koch.ServerCalcEdges(level);
            }
            output = "Server:\t What speed do you want?";
            state = SENTLEVEL;
            System.out.println(level);
            System.out.println("SENTLEVEL");
        } else if (state == SPEED) {
            if(input.equalsIgnoreCase("slow")) {
                state = SLOW;
            } else if (input.equalsIgnoreCase("fast")) {
                state = FAST;
            }
            System.out.println("SENTLEVEL");
        } else if (state == SLOW) {
            state = SENT;
            System.out.println("SLOW");
        } else if (state == FAST) {
            state = SENT;
            System.out.println("FAST");
        }
        
        return output;
    }
    
    public List<Edge> getEdges() {
        return edges;
    }
}
