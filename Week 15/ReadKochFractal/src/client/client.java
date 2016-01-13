/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import calculate.Edge;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import javafx.scene.paint.Color;

/**
 *
 * @author Rob
 */
public class client {
    
    public static void main(String[] args) throws ClassNotFoundException
    {  
       try
       {  
          Socket s = new Socket("localhost", 8189);
          try
          {
            OutputStream outStream = s.getOutputStream();
            InputStream inStream = s.getInputStream();

            ObjectOutputStream out = new ObjectOutputStream(outStream);
            ObjectInputStream in = new ObjectInputStream(inStream);
             
            // send object 
            Edge e = new Edge(1, 1, 2, 2, Color.BLUE);
            System.out.println("sending Edge: "+e.toString());
            out.writeObject(e);
            
            Edge ed = (Edge)in.readObject();
            System.out.println("ontvangen antwoord: "+ ed.toString());
          }
          finally
          {
             s.close();
          }
       }
       catch (IOException e)
       {  
          e.printStackTrace();
       }
    }
}
