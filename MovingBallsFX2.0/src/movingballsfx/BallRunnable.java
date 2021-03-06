/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movingballsfx;

import javafx.scene.paint.Color;

/**
 *
 * @author Peter Boots
 */
public class BallRunnable implements Runnable {

    private Ball ball;
    private RW monitor;
    private boolean Locked;

    public BallRunnable(Ball ball, RW Monitor) {
        this.ball = ball;
        monitor = Monitor;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            
            ball.move();
            
            try {
                
                if (ball.isEnteringCs()) {
                    if (ball.isReader())
                        monitor.enterReader();
                    else
                        monitor.enterWriter();

                }else if (ball.isLeavingCs()){
                    if (ball.isReader()) {
                        monitor.exitReader();
                    }
                    else {
                        if(!Thread.currentThread().isInterrupted()) {
                            monitor.exitWriter();
                            System.out.println("Bar");
                        }
                    }
                }

                Thread.sleep(ball.getSpeed());
                
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                if(ball.isInCs()) {
                    System.out.println("Foo");
                    if(ball.isReader()) {
                        monitor.exitReader();
                    } else {
                        monitor.exitWriter();
                    }
                }

                else if(ball.isEnteringCs()) {
                    monitor.waitingWriterInterupted();
                }

            }
        }
    }
}
