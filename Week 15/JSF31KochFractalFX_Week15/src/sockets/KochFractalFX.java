/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;


import calculate.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Nico Kuijpers
 */ 
public class KochFractalFX extends Application {

    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;

    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;

    // Labels for progress 
    public Label labelProgressBottom;
    public Label labelProgressRight;
    public Label labelProgressLeft;

    // Koch panel and its size
    private Canvas kochPanel;
    private final int kpWidth = 500;
    private final int kpHeight = 500;
    Thread t = null;
    int currentLevel = 1;
    KochFractalFX application = this;

    @Override
    public void start(Stage primaryStage) {

        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // For debug purposes
        // Make de grid lines visible
        // grid.setGridLinesVisible(true);
        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth, kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);

        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);

        // Button to load edges from text
        Button buttonLoadRT = new Button();
        buttonLoadRT.setText("Real Time");
        buttonLoadRT.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Real time loading with sockets
                loadEdgesRT();
            }
        });
        grid.add(buttonLoadRT, 3, 6);

        // Button to load edges from text buffered
        Button buttonLoadNormal = new Button();
        buttonLoadNormal.setText("Normal");
        buttonLoadNormal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadEdgesNormal();
            }
        });
        grid.add(buttonLoadNormal, 5, 6);

        // Button to fit Koch fractal in Koch panel
        Button buttonFitFractal = new Button();
        buttonFitFractal.setText("Fit Fractal");
        buttonFitFractal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fitFractalButtonActionPerformed(event);
                loadEdgesNormal();
            }
        });
        grid.add(buttonFitFractal, 14, 6);

        // Button to load edges from text buffered
        Button buttonIncreaseLevel = new Button();
        buttonIncreaseLevel.setText("Increase level");
        buttonIncreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentLevel < 10) {
                    setCurrentLevel(currentLevel + 1);
                }
            }
        });
        grid.add(buttonIncreaseLevel, 3, 7);

        // Button to load edges from text buffered
        Button buttonDecreaseLevel = new Button();
        buttonDecreaseLevel.setText("Decrease level");
        buttonDecreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentLevel > 1) {
                    setCurrentLevel(currentLevel - 1);
                }
            }
        });
        grid.add(buttonDecreaseLevel, 5, 7);

        // Add mouse clicked event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        kochPanelMouseClicked(event);
                        loadEdgesNormal();
                    }
                });

        // Add mouse pressed event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        kochPanelMousePressed(event);
                        loadEdgesNormal();
                    }
                });

        // Add mouse dragged event to Koch panel
        kochPanel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                kochPanelMouseDragged(event);
                loadEdgesNormal();
            }
        });

        // Create Koch manager and set initial level
        resetZoom();

        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth + 50, kpHeight + 300);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Load first settings
        loadEdgesNormal();
    }

    public void clearKochPanel() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GraphicsContext gc = kochPanel.getGraphicsContext2D();
                gc.clearRect(0.0, 0.0, kpWidth, kpHeight);
                gc.setFill(Color.BLACK);
                gc.fillRect(0.0, 0.0, kpWidth, kpHeight);
            }
        });
    }

    public synchronized void drawEdge(Edge e) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Graphics
                GraphicsContext gc = kochPanel.getGraphicsContext2D();

                // Set line color
                gc.setStroke(e.color);

                // Set line width depending on level
                if (currentLevel <= 3) {
                    gc.setLineWidth(2.0);
                } else if (currentLevel <= 5) {
                    gc.setLineWidth(1.5);
                } else {
                    gc.setLineWidth(1.0);
                }

                // Draw line
                gc.strokeLine(e.X1, e.Y1, e.X2, e.Y2);
            }
        });
    }

    public synchronized void setCurrentLevel(int lvl) {
        this.currentLevel = lvl;
        this.labelLevel.setText("Level: " + lvl);
    }

    private void fitFractalButtonActionPerformed(ActionEvent event) {
        resetZoom();
    }

    private void kochPanelMouseClicked(MouseEvent event) {
        if (Math.abs(event.getX() - startPressedX) < 1.0
                && Math.abs(event.getY() - startPressedY) < 1.0) {
            double originalPointClickedX = (event.getX() - zoomTranslateX) / zoom;
            double originalPointClickedY = (event.getY() - zoomTranslateY) / zoom;
            if (event.getButton() == MouseButton.PRIMARY) {
                zoom *= 2.0;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                zoom /= 2.0;
            }
            zoomTranslateX = (int) (event.getX() - originalPointClickedX * zoom);
            zoomTranslateY = (int) (event.getY() - originalPointClickedY * zoom);
        }
    }

    private void kochPanelMousePressed(MouseEvent event) {
        startPressedX = event.getX();
        startPressedY = event.getY();
        lastDragX = event.getX();
        lastDragY = event.getY();
    }

    private void resetZoom() {
        int kpSize = Math.min(kpWidth, kpHeight);
        zoom = kpSize;
        zoomTranslateX = (kpWidth - kpSize) / 2.0;
        zoomTranslateY = (kpHeight - kpSize) / 2.0;
    }

    private void kochPanelMouseDragged(MouseEvent event) {
        zoomTranslateX = zoomTranslateX + event.getX() - lastDragX;
        zoomTranslateY = zoomTranslateY + event.getY() - lastDragY;
        lastDragX = event.getX();
        lastDragY = event.getY();
    }

    private void loadEdgesNormal() {
        if (t != null) {
            t.interrupt();
        }
        t = new Thread(new socketClient(this, zoom, zoomTranslateX, zoomTranslateY, currentLevel, 1));
        t.start();
    }

    private void loadEdgesRT() {
        if (t != null) {
            t.interrupt();
        }
        t = new Thread(new socketClient(this, zoom, zoomTranslateX, zoomTranslateY, currentLevel, 2));
        t.start();
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
}
