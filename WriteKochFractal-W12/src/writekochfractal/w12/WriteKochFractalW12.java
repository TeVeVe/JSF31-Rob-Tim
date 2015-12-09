/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writekochfractal.w12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import calculate.Edge;
import calculate.KochFractal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import sun.management.Agent;
import timeutil.TimeStamp;

/**
 *
 * @author jsf3
 */
public class WriteKochFractalW12 implements Observer {
    ArrayList<Edge> edges;
    KochFractal fractal;
    FileOutputStream fos;
    ObjectOutputStream oos;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WriteKochFractalW12 wkf = new WriteKochFractalW12();
        wkf.start();
        
    }
    
    public void start() {
        TimeStamp ts = new TimeStamp();
        ts.setBegin("Begin Process");
        int level = 0;
        edges = new ArrayList();
        fractal = new KochFractal();
        
        System.out.println("What level kochfractal do you want?");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String levelString = br.readLine();
            level = Integer.parseInt(levelString);
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fractal.addObserver(this);
        fractal.setLevel(level);
        
        fractal.generateLeftEdge();
        fractal.generateRightEdge();
        fractal.generateBottomEdge();
        
        System.out.println(edges.size());
        
        
        try {
            fos = new FileOutputStream("/home/jsf3/data/fractal.txt");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(level);
            oos.writeObject(fractal.getNrOfEdges());
            
            for(Edge edge: edges) {
                oos.writeObject(edge);
            }
            
            oos.close();
            fos.close();
            System.out.println("Data saved.");
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        ts.setEnd("Einde proces");
        System.out.println(ts);
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
    }
    
}
