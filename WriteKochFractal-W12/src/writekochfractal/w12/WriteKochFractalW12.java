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
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import timeutil.TimeStamp;

/**
 *
 * @author jsf3
 */
public class WriteKochFractalW12 implements Observer {
    ArrayList<Edge> edges;
    KochFractal fractal;
    FileOutputStream fos;
    BufferedOutputStream bos;
    ObjectOutputStream oos;
    TimeStamp ts;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WriteKochFractalW12 wkf = new WriteKochFractalW12();
        wkf.start();
        
    }
    
    public void start() {
        ts = new TimeStamp();
        ts.setBegin("Begin Process");
        int level = 0;
        edges = new ArrayList();
        fractal = new KochFractal();
        String choice = "0";
        
        System.out.println("What level kochfractal do you want?");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String levelString = br.readLine();
            level = Integer.parseInt(levelString);
            
            System.out.println("What method do you want to use to export? \n\n"
                    + "1. Binary without buffer \n2. Binary with buffer\n"
                    + "3. Text without buffer \n4. Text with buffer\n");
            
            choice = br.readLine();            
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

        if(choice.equals("1")) {
            exportBinNoBuffer(level);
        } else if(choice.equals("2")) {
            exportBinWithBuffer(level);
        }
    }

    private void exportBinNoBuffer(int level) {
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

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        ts.setEnd("Einde proces");
        System.out.println(ts);
    }
    
    private void exportBinWithBuffer(int level) {
        try {
            fos = new FileOutputStream("/home/jsf3/data/fractal.txt");
            bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(level);
            oos.writeObject(fractal.getNrOfEdges());
            System.out.println("Data saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ts.setEnd("Einde proces");
        System.out.println(ts);
    }
    
//    private void exportTextNoBuffer(int level) {
//        try {
//            fos = new FileOutputStream("/home/jsf3/data/fractal.txt");
//            
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
    }
    
}
