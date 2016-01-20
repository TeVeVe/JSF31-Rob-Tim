/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import calculate.Edge;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rob
 */
public class socketRunnable implements Runnable, Observer {

    Socket socket;
    double zoom, zoomTranslateX, zoomTranslateY;
    int level, mode, counter;
    DataOutputStream writer;
    DataInputStream scanner;

    public socketRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //Get input streams
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            scanner = new DataInputStream(socket.getInputStream());
            //Get values of zooming
            while (scanner.available() == 0) {
                System.out.println("Waiting");
            }
            zoom = scanner.readDouble();
            zoomTranslateX = scanner.readDouble();
            zoomTranslateY = scanner.readDouble();
            level = scanner.readInt();
            mode = scanner.readInt();
            System.out.println("Received data");
            //Send edges after calculating
            if (mode == 1) {
                //Calculate edges
                EdgeReceiver receiver = new EdgeReceiver();
                KochFractal fractal = new KochFractal();
                fractal.setLevel(level);
                fractal.addObserver(receiver);
                fractal.generateBottomEdge();
                fractal.generateLeftEdge();
                fractal.generateRightEdge();
                //Convert edges to zoom
                ArrayList<Edge> edges = new ArrayList<>();
                for (Edge e : receiver.getEdges()) {
                    edges.add(edgeAfterZoomAndDrag(e));
                }
                System.out.println("Finished calculating");
                //Send edges
                for (Edge e : edges) {
                    //Clear Buffer
                    if (counter == 50) {
                        counter = 0;
                        writer.flush();
                    }
                    counter++;
                    writer.writeDouble(e.X1);
                    writer.writeDouble(e.Y1);
                    writer.writeDouble(e.X2);
                    writer.writeDouble(e.Y2);
                    writer.writeDouble(e.color.getHue());
                }
                writer.flush();
                //ing for client confirmation
                while (scanner.available() == 0) {
                    Thread.sleep(level);
                }
                System.out.println("Finished sending data");
                scanner.close();
                writer.close();
                socket.close();
            }
            //Send edges to client
            if (mode == 2) {
                //Calculate edges
                KochFractal fractal = new KochFractal();
                fractal.setLevel(level);
                fractal.addObserver(this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fractal.generateBottomEdge();
                        fractal.generateLeftEdge();
                        fractal.generateRightEdge();
                        
                        try {
                            writer.flush();
                        } catch (IOException ex) {
                            Logger.getLogger(socketRunnable.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
                //Waiting for client confirmation
                while (scanner.available() == 0) {
                    Thread.sleep(level);
                }
                System.out.println("Finished sending data");
                scanner.close();
                writer.close();
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("Sending client data failed!");
            System.out.println(e.getMessage());
        }
    }

    private Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.X1 * zoom + zoomTranslateX,
                e.Y1 * zoom + zoomTranslateY,
                e.X2 * zoom + zoomTranslateX,
                e.Y2 * zoom + zoomTranslateY,
                e.color);
    }

    @Override
    public void update(Observable o, Object arg) {
        Edge e = edgeAfterZoomAndDrag((Edge) arg);
        try {
            if (counter == 5) {
                //Clear Buffer
                counter = 0;
                writer.flush();
            }
            counter++;
            writer.writeDouble(e.X1);
            writer.writeDouble(e.Y1);
            writer.writeDouble(e.X2);
            writer.writeDouble(e.Y2);
            writer.writeDouble(e.color.getHue());
            Thread.sleep(5);
        } catch (Exception ex) {
            System.out.println("Sending data failed!");
            System.out.println(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
