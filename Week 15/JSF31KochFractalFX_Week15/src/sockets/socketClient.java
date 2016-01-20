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
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.paint.Color;

/**
 *
 * @author Rob
 */
public class socketClient implements Runnable {

    Socket socket;
    KochFractalFX application;
    double zoom, zoomTranslateX, zoomTranslateY;
    int level, mode;

    public socketClient(KochFractalFX application, double zoom, double zoomTranslateX, double zoomTranslateY, int level, int mode) {
        this.application = application;
        this.zoom = zoom;
        this.zoomTranslateX = zoomTranslateX;
        this.zoomTranslateY = zoomTranslateY;
        this.level = level;
        this.mode = mode;
    }

    @Override
    public void run() {
        try {
            //Open socket
            socket = new Socket("127.0.0.1", 5432);
            //Get input streams
            DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream scanner = new DataInputStream(socket.getInputStream());
            //Send calculation data
            writer.writeDouble(zoom);
            writer.writeDouble(zoomTranslateX);
            writer.writeDouble(zoomTranslateY);
            writer.writeInt(level);
            writer.writeInt(mode);
            writer.flush();
            System.out.println("Sent data");

            //Wait for server to do calculations
            while (scanner.available() == 0) {
                Thread.sleep(level);
            }
            System.out.println("Start drawing");
            //Start reading and drawing
            application.clearKochPanel();
            int counter = 0;
            while (true) {
                if (scanner.available() > 0) {
                    application.drawEdge(new Edge(
                            scanner.readDouble(),
                            scanner.readDouble(),
                            scanner.readDouble(),
                            scanner.readDouble(),
                            Color.hsb(scanner.readDouble(), 1, 1)));
                    counter = 0;
                } else {
                    Thread.sleep(10);
                    counter ++;
                    if(counter > 10) {
                        break;
                    }
                }
            }
            System.out.println("Finished drawing!");
            
            writer.writeChar('H');
            writer.flush();
            writer.close();
            scanner.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Receiving edges failed!");
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
