/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import calculate.Edge;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Rob
 */
public class Server {

    private ArrayList<Socket> clients;
            
    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.serverStart();
    }
    
    public void serverStart() {
        ServerSocket server = null;
        boolean listening = true;
        clients = new ArrayList();
        
        try {
            server = new ServerSocket(8189);
            System.out.println("Server started");

            while (listening) {
               
                Thread t = new Thread(new EdgeServerRunnable(server.accept()));
                // start Thread
                t.start();
            }
            server.close();
            
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8189.");
            System.exit(-1);
        }
    }
    
    public Socket getSock(int i) {
        return clients.get(i);
    }
}
