/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movingballsfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author Nico Kuijpers
 */
public class MovingBallsFX extends Application {
    
    private Thread threadDraw;
    private Ball[] ballArray = new Ball[10];
    private Thread[] threadArray = new Thread[10];
    private CheckBox[] checkBoxArray = new CheckBox[10];
    private Circle[] circleArray = new Circle[10];
    private int minX = 100;
    private int maxX = 700;
    private int maxY = 400;
    private int radius = 10;
    private int minCsX = (maxX + minX) / 2 - 100;
    private int maxCsX = (maxX + minX) / 2 + 100;

    @Override
    public void start(Stage primaryStage) {
       
        // Create the scene
        Group root = new Group();
        Scene scene = new Scene(root, maxX, maxY);
        
        // Check boxes
        for (int i = 0; i < checkBoxArray.length; i++) {
            String text;
            if (i < 5) { 
                // Check box for reader
                text = "Reader"+(i+1);
            }
            else {
                // Check box for writer
                text = "Writer"+(i-4);
            }
            final int index = i;
            checkBoxArray[i] = new CheckBox(text);
            checkBoxArray[i].addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        checkBoxMouseClicked(event,index);
                    }
                });
            checkBoxArray[i].setLayoutX(radius);
            checkBoxArray[i].setLayoutY((i*4 + 1)*radius);
            root.getChildren().add(checkBoxArray[i]);
        }
        
        // Indicate entire section
        Rectangle entireSection = new Rectangle(minX,0,maxX-minX,maxY);
        entireSection.setFill(Color.LIGHTYELLOW);
        root.getChildren().add(entireSection);
        
        // Indicate critical section
        Rectangle criticalSection = new Rectangle(minCsX,0,maxCsX-minCsX,maxY);
        criticalSection.setFill(Color.LIGHTGREEN);
        root.getChildren().add(criticalSection);
        
        // Define circles for each ball
        for (int i = 0; i < circleArray.length; i++) {
            CheckBox cb = checkBoxArray[i];
            int y = (int) cb.getLayoutY() + radius;
            if (i < 5) {
                // Reader
                circleArray[i] = new Circle(minX, y, radius, Color.RED);
            }
            else {
                // Writer
                circleArray[i] = new Circle(minX, y, radius, Color.BLUE);
            }
            circleArray[i].setVisible(false);
            root.getChildren().add(circleArray[i]);
        }
        
        // Define title and assign the scene for main window
        primaryStage.setTitle("Moving Balls");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Start thread to draw each 20 ms
        threadDraw = new Thread(new DrawRunnable());
        threadDraw.start();
    }
    
    private void checkBoxMouseClicked(MouseEvent event, int index) {
        CheckBox cb = checkBoxArray[index];
        int y = (int) cb.getLayoutY() + radius;
        if (cb.isSelected() && index < 5) { 
            // Reader selected: new red ball
            Ball b = new Ball(minX, maxX, minCsX, maxCsX, y, Color.RED);
            ballArray[index] = b;
            Thread t = new Thread(new BallRunnable(b));
            threadArray[index] = t;
            circleArray[index].setVisible(true);
            t.start();
        } else if (cb.isSelected() && index >= 5) { 
            // Writer selected: new blue ball
            Ball b = new Ball(minX, maxX, minCsX, maxCsX, y, Color.BLUE);
            ballArray[index] = b;
            Thread t = new Thread(new BallRunnable(b));
            threadArray[index] = t;
            circleArray[index].setVisible(true);
            t.start();
        } else {
            // Reader or writer deselected: remove ball
            threadArray[index].interrupt();
            threadArray[index] = null;
            ballArray[index] = null;
            circleArray[index].setVisible(false);
            circleArray[index].setCenterX(minX);
        }
    }
    
    private void updateCircles() {
        for (int i = 0; i < ballArray.length; i++) {
            Ball b = ballArray[i];
            Circle c = circleArray[i];
            if (b != null) {
                c.setCenterX(b.getXPos());
                c.setCenterY(b.getYPos());
            }
        }
    }

    @Override
    public void stop() {
        threadDraw.interrupt();
        for (int i = 0; i < threadArray.length; i++) {
            if (threadArray[i] != null) {
                threadArray[i].interrupt();
            }
        }
    }
  
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    // Update circles each 20 ms
    private class DrawRunnable implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(20);
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {
                            updateCircles();
                        }
                    });
                }
            } catch (InterruptedException ex) {  
            }
        }
    }
}
