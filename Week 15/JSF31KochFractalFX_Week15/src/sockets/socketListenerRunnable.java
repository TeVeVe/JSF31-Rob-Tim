/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rob
 */
public class socketListenerRunnable implements Runnable {
    
    ServerSocket serverSocket;
    ExecutorService pool;
    boolean shutdown;
    
    public socketListenerRunnable(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();
        shutdown = false;
    }

    @Override
    public void run() {
        while(!shutdown) {
            System.out.println(System.currentTimeMillis() + "ms: Waiting for client");
            try {
                Socket socket = serverSocket.accept();
                System.out.println(System.currentTimeMillis() + "ms: Client accepted!");
                socketRunnable run = new socketRunnable(socket);
                pool.execute(run);
            } catch (IOException ex) {
                System.out.println(System.currentTimeMillis() + "ms: Accepting client failed!");
                System.out.println(ex.getMessage());
            }
        }
        System.out.println(System.currentTimeMillis() + "ms: Socket listener stopped!");
    }
    
    public void quit() {
        shutdown = true;
        pool.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println(System.currentTimeMillis() + "ms: Closing serversocket failed");
        }
    }
}
