/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import readkochfractal.JSF31KochFractalFX;

/**
 *
 * @author Rob
 */
public class client {
       
    private Socket socket;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private JSF31KochFractalFX koch;
    private Stage stage;
    private Object last;
        
    public static void main(String[] args) throws ClassNotFoundException
    {  
        try {
            client c = new client();
            c.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void start() throws IOException, Exception {
       try {  
          stage = new Stage();
          koch.start(stage);
          socket = new Socket("localhost", 8189);
          System.out.println("Client:\t Client now listening");
          boolean running = true;

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        while(running) {
            
            String input = br.readLine();
                        
            if(input != null) {

            }
            
            System.out.println(in.readLine());
//            sendObjectToServer(input);
//            System.out.println(in.readLine());
//            System.out.println(br.readLine());

            while(!input.equalsIgnoreCase("Bye")) {
                sendObjectToServer(input);
                running = false;
            }
        }
     
            sendObjectToServer("Bye");
            br.close();
            out.close();
            in.close();
        }   catch (IOException e) {  
            e.printStackTrace();
        } finally {
            socket.close();
        } 
    }
    
    public String readMessageFromServer() {
        String returner = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            returner = (String) objectInputStream.readObject();
            System.out.println("Server:\t" + returner);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return returner;
    }
    
    public void sendObjectToServer(Object object) {
        try {
            if(object != last) {
                last = object;
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Client:\t" + object.toString());
                objectOutputStream.writeObject(object);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Object readObjectFromServer() {
        Object returner = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            returner = objectInputStream.readObject();
            System.out.println("Server:\t" + returner);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return returner;
    }
}
