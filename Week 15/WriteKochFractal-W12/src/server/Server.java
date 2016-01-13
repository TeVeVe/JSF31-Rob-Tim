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

/**
 *
 * @author Rob
 */
public class Server {

    public static void main(String[] args) {
        try {
            // establish server socket
            ServerSocket s = new ServerSocket(8189);
            Socket client = s.accept();
            System.out.println("Connected");

            try {

                OutputStream outStream = client.getOutputStream();
                InputStream inStream = client.getInputStream();

                ObjectInputStream in = new ObjectInputStream(inStream);
                ObjectOutputStream out = new ObjectOutputStream(outStream);

                out.writeObject("Hello! Enter BYE to exit.");
                out.flush();

                // echo client Object input
                boolean done = false;
                Object inObject = null;
                while (!done) {
                    try {
                        inObject = in.readObject();
                        if (inObject instanceof Edge) {
                            // change name
                            Edge e = (Edge) inObject;
                            System.out.println("Edge ontvangen: "
                                    + e.toString());
                            out.writeObject(e);
                            out.flush();
                        }
                    } catch (ClassNotFoundException e) {
                        System.out.println("Object type not known");
                    }
                }
            } finally {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
