/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import calculate.Edge;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import writekochfractal.w12.WriteKochFractalW12;

/**
 *
 * @author Rob
 */
public class EdgeServerRunnable implements Runnable {
    private Socket socket = null;
    private ArrayList<Edge> edges;
    private WriteKochFractalW12 koch;
    
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;

    public EdgeServerRunnable(Socket socket) {
	this.socket = socket;
        this.koch = new WriteKochFractalW12();
    }

    @Override
    public void run() {
        try {  
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine, outputLine;
            
            EdgeCalcProtocol ecp = new EdgeCalcProtocol();
            outputLine = ecp.processInput(null);
            System.out.println(outputLine);
            
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
                outputLine = ecp.processInput(inputLine);
                out.println(outputLine);
                
             if (outputLine.equalsIgnoreCase("Bye")) {
                out.println("Bye");
                break;
             }
        }
            
        out.close();
        in.close();
        socket.close();
        System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String readMessage() {
        String returner = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            returner = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return returner;
    }

    public void sendObject(Object object) {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(object);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Object readObject() {
        Object returner = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            returner = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return returner;
    }
}



