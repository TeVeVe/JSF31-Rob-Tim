/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import java.io.IOException;
import java.util.Scanner;
/**
 *
 * @author Rob
 */
public class socketServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //Get input
        Scanner scanner = new Scanner(System.in);
        //Show start up message
        System.out.println("Starting server");
        //Start socket listener
        boolean shutdown = false;
        socketListenerRunnable listener = new socketListenerRunnable(5432);
        new Thread(listener).start();
        //Waiting for input
        while(!shutdown) {
            if(scanner.hasNextLine()) {
                if(scanner.nextLine().equals("QUIT")) {
                    shutdown = true;
                }
            }
        }
        listener.quit();
        System.out.println("Stopping server!");
    }
    
}
