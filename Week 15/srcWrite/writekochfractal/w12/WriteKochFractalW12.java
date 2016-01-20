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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

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
    BufferedWriter bw;
    TimeStamp ts;
    File file;
    /**
     * @param args the command line arguments
     */    
    
    public ArrayList<Edge> ServerCalcEdges(int level) {
        edges = new ArrayList();
        fractal = new KochFractal();

        fractal.addObserver(this);
        fractal.setLevel(level);

        fractal.generateLeftEdge();
        fractal.generateRightEdge();
        fractal.generateBottomEdge();

        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
    }    
}
