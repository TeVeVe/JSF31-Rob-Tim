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
    public static void main(String[] args) {
        WriteKochFractalW12 wkf = new WriteKochFractalW12();
        wkf.start();
        
    }
    
    public void start() {
        int level = 0;
        edges = new ArrayList();
        fractal = new KochFractal();
        String choice = "0";
        
        file = new File("/home/jsf3/data/fractal.txt");
        if(file.exists()) {
            file.delete();
        }
        
        System.out.println("What level kochfractal do you want?");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String levelString = br.readLine();
            level = Integer.parseInt(levelString);
            
            System.out.println("What method do you want to use to export? \n\n"
                    + "1. Binary without buffer \n2. Binary with buffer\n"
                    + "3. Text without buffer \n4. Text with buffer\n5. Random Access File");
            
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
        } else if(choice.equals("3")) {
            exportTextNoBuffer(level);
        } else if(choice.equals("4")) {
            exportTextWithBuffer(level);
        } else if(choice.equals("5")) {
            RAFWrite(level);
        }
    }

    private void exportBinNoBuffer(int level) {
        try {
            ts = new TimeStamp();
            ts.setBegin("Begin Process");
            
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
            ts = new TimeStamp();
            ts.setBegin("Begin Process");
            
            fos = new FileOutputStream("/home/jsf3/data/fractal.txt");
            bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(level);
            oos.writeObject(fractal.getNrOfEdges());
            
            for(Edge e: edges) {
                oos.writeObject(e);
            }
            
            oos.close();
            fos.close();
            
            System.out.println("Data saved.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ts.setEnd("Einde proces");
        System.out.println(ts);
    }
    
    private void exportTextNoBuffer(int level) {
        try {
            ts = new TimeStamp();
            ts.setBegin("Begin Process");
            
            fos = new FileOutputStream("/home/jsf3/data/fractal.txt");
            PrintWriter pw = new PrintWriter(fos);
            
            pw.print(fractal.getNrOfEdges() + "\n");
            pw.print(level + "\n");
            
            for(Edge e: edges) {
                pw.print(e.X1 + "\n");
                pw.print(e.X2 + "\n");
                pw.print(e.Y1 + "\n");
                pw.print(e.Y2 + "\n");
                pw.print(e.r + "\n");
                pw.print(e.g + "\n");
                pw.print(e.b + "\n");
            }
            
            pw.close();
            fos.close();
            
            System.out.println("Data saved.");
            ts.setEnd("Einde proces");
            System.out.println(ts);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void exportTextWithBuffer(int level) {
        try {
            ts = new TimeStamp();
            ts.setBegin("Begin Process");
            
            fos = new FileOutputStream("/home/jsf3/data/fractal.txt");
            PrintWriter pw = new PrintWriter(fos);
            bw = new BufferedWriter(pw);
            
            bw.write(fractal.getNrOfEdges() + "\n");
            bw.write(level + "\n");
            
            for(Edge e: edges) {
                bw.write(e.X1 + "\n");
                bw.write(e.X2 + "\n");
                bw.write(e.Y1 + "\n");
                bw.write(e.Y2 + "\n");
                bw.write(e.r + "\n");
                bw.write(e.g + "\n");
                bw.write(e.b + "\n");
            }
            
            bw.close();
            fos.close();
            
            System.out.println("Data saved.");
            ts.setEnd("Einde proces");
            System.out.println(ts);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WriteKochFractalW12.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void RAFWrite(int level) {
    try {
        String filePath = file.getPath();
        ts = new TimeStamp();
        ts.setBegin("Begin Process");
        
        RandomAccessFile rafFile = new RandomAccessFile(filePath, "rw");
        rafFile.writeBytes(fractal.getNrOfEdges() + "\n");
        rafFile.writeBytes(level + "\n");
            
        for(Edge e: edges) {
            rafFile.writeBytes(e.X1 + "\n");
            rafFile.writeBytes(e.X2 + "\n");
            rafFile.writeBytes(e.Y1 + "\n");
            rafFile.writeBytes(e.Y2 + "\n");
            rafFile.writeBytes(e.r + "\n");
            rafFile.writeBytes(e.g + "\n");
            rafFile.writeBytes(e.b + "\n");
        }
        
        rafFile.close();
        
        File file2 = new File("/home/jsf3/data/fractal_done.txt");
        if(file2.exists()) {
            file2.delete();
        }
        
        file.renameTo(file2);

        ts.setEnd("Einde proces");
        System.out.println(ts);
    }
    catch(IOException ex) {
        ex.printStackTrace();
    }
}

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
    }    
}
