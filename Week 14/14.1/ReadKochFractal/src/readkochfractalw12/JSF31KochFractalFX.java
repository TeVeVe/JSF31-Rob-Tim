/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package readkochfractalw12;

import calculate.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import timeutil.TimeStamp;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


/**
 *
 * @author Nico Kuijpers
 */
public class JSF31KochFractalFX extends Application {
    
    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;
    private TimeStamp ts;
    
    private Scanner scanner;

    // Koch manager
    // TO DO: Create class KochManager in package calculate
    
    // Current level of Koch fractal
    private int currentLevel = 1;
    
    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;
    private Label labelNrEdges;
    private Label labelNrEdgesText;
    private Label labelCalc;
    private Label labelCalcText;
    private Label labelDraw;
    private Label labelDrawText;
    
    
    private File file = new File("/home/jsf3/data/fractal.txt");
    private MappedByteBuffer bbuffer;
    private int level = 1;
    private int noOfEdges;
    
    // Koch panel and its size
    private Canvas kochPanel;
    private final int kpWidth = 500;
    private final int kpHeight = 500;
    
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
        //
        // grid.setGridLinesVisible(true);
        
        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth,kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);
        
        // Labels to present number of edges for Koch fractal
        labelNrEdges = new Label("Nr edges:");
        labelNrEdgesText = new Label();
        grid.add(labelNrEdges, 0, 0, 4, 1);
        grid.add(labelNrEdgesText, 3, 0, 22, 1);
        
        // Labels to present time of calculation for Koch fractal
        labelCalc = new Label("Calculating:");
        labelCalcText = new Label();
        grid.add(labelCalc, 0, 1, 4, 1);
        grid.add(labelCalcText, 3, 1, 22, 1);
        
        // Labels to present time of drawing for Koch fractal
        labelDraw = new Label("Drawing:");
        labelDrawText = new Label();
        grid.add(labelDraw, 0, 2, 4, 1);
        grid.add(labelDrawText, 3, 2, 22, 1);
        
        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);
        
        // Button to fit Koch fractal in Koch panel
        Button buttonFitFractal = new Button();
        buttonFitFractal.setText("Fit Fractal");
        buttonFitFractal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    fitFractalButtonActionPerformed(event);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(JSF31KochFractalFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        grid.add(buttonFitFractal, 14, 6);

        // Add mouse clicked event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    kochPanelMouseClicked(event);
                }
            });
        
        // Add mouse pressed event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_PRESSED,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    kochPanelMousePressed(event);
                }
            });
        
        // Add mouse dragged event to Koch panel
        kochPanel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                kochPanelMouseDragged(event);
            }
        });
        
        // Create Koch manager and set initial level
        resetZoom();
        
        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth+50, kpHeight+170);
        root.getChildren().add(grid);
        
        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        watchFolder();
    }
    
    private void watchFolder() {
        Runnable wf = new Runnable() {

            @Override
            public void run() {
                try {
                    final WatchService watcher;
                    // Voorbeelden van interessante locaties
                    // Path dir = Paths.get("D:\\");
                    Path dir = Paths.get("/home/jsf3/data/");
                    WatchKey key;
                    
                    watcher = FileSystems.getDefault().newWatchService();
                    dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

                    while (true) {
                        key = watcher.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent<Path> ev = (WatchEvent<Path>) event;

                            Path filename = ev.context();
                            Path child = dir.resolve(filename);

                            WatchEvent.Kind kind = ev.kind();
                            if (kind == ENTRY_CREATE) {
                                if(child.getFileName().toString().equals("fractal_done.txt")) {
                                    System.out.println("Bar");
                                    try {
                                        drawEdges();
                                    } catch (ClassNotFoundException ex) {
                                        Logger.getLogger(JSF31KochFractalFX.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                        key.reset();
                    }

                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(JSF31KochFractalFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        
        System.out.println("Foo");
        
        Thread t = new Thread(wf);
        t.start();
    }
    
    public void clearKochPanel() {
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0,0.0,kpWidth,kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0,0.0,kpWidth,kpHeight);
    }
    
    public synchronized void callDrawEdge(final Edge e) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                drawEdge(e);
            }
        });
    }
        
    public void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        
        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);
        
        // Set line color
        gc.setStroke(Color.color(e1.r, e1.g, e1.b, 1));
        
        // Set line width depending on level
        if (currentLevel <= 3) {
            gc.setLineWidth(2.0);
        }
        else if (currentLevel <=5 ) {
            gc.setLineWidth(1.5);
        }
        else {
            gc.setLineWidth(1.0);
        }
        
        // Draw line
        gc.strokeLine(e1.X1,e1.Y1,e1.X2,e1.Y2);
    }
    
    public void drawEdges() throws ClassNotFoundException {
        clearKochPanel();
        try {
            RAFRead(level);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void BinnaryInputStream() throws ClassNotFoundException, FileNotFoundException, IOException {
        ts = new TimeStamp();
        ts.setBegin("Begin proces");
        FileInputStream fis;
        ObjectInputStream in;

        fis = new FileInputStream(file);
        in = new ObjectInputStream(fis);

        level = (int) in.readObject();
        int nrOfEdges = (int) in.readObject();

        for (int i = 0; i < nrOfEdges; i++) {
            drawEdge((Edge) in.readObject());
        }
        in.close();
        
        ts.setEnd("Done");
        System.out.println(ts);
    }
    
    public void BinnaryBufferedInputStream() throws ClassNotFoundException, FileNotFoundException, IOException {
        ts = new TimeStamp();
        ts.setBegin("Begin proces");
        byte [] buffer =null;
        
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        int length = (int) file.length();
        buffer = new byte [length];
        dis.read(buffer);
        dis.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        ObjectInput in = new ObjectInputStream(bis);

        level = (int) in.readObject();
        int nrOfEdges = (int) in.readObject();

        for (int i = 0; i < nrOfEdges; i++) {
            drawEdge((Edge) in.readObject());
        }
        in.close();   
        ts.setEnd("Done");
        System.out.println(ts);        
    }
    
    public void TextBufferendInputStream() throws FileNotFoundException, IOException{
        ts = new TimeStamp();
        ts.setBegin("Begin proces");
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        // We send the number of edges but we don't 
        br.readLine();
        level = Integer.parseInt(br.readLine());
             
        String line;

        int counter = 0;

        double X1 = 0;
        double Y1 = 0;
        double X2 = 0;
        double Y2 = 0;
        double red = 0;
        double green = 0;
        double blue = 0;

        while (br.ready())
        {
            line = br.readLine();

            if (counter == 0) {
                X1 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 1)  {
                X2 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 2)  {
                Y1 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 3)  {
                Y2 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 4)  {
                red = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 5) {
                green = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 6) {
                blue = Double.parseDouble(line);
                counter++;
                drawEdge(new Edge(X1, Y1, X2, Y2, Color.color(red, green, blue, 1)));
                counter = 0;
            }
        }
        
        br.close();
        System.out.println("Done");
        ts.setEnd("Done");
        System.out.println(ts);
    }
    
    public void TextInputStream() throws FileNotFoundException {
        ts = new TimeStamp();
        ts.setBegin("Begin proces");

        FileReader fileReader = new FileReader(file);
        scanner = new Scanner(fileReader);
        int nrOfEdges = Integer.parseInt(scanner.nextLine());
        level = Integer.parseInt(scanner.nextLine());
             
        String line;

        int counter = 0;

        double X1 = 0;
        double Y1 = 0;
        double X2 = 0;
        double Y2 = 0;
        double red = 0;
        double green = 0;
        double blue = 0;

        while (scanner.hasNext())
        {
            line = scanner.next();

            if (counter == 0) {
                X1 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 1)  {
                X2 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 2)  {
                Y1 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 3)  {
                Y2 = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 4)  {
                red = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 5) {
                green = Double.parseDouble(line);
                counter++;
            }
            else if (counter == 6) {
                blue = Double.parseDouble(line);
                counter++;
                drawEdge(new Edge(X1, Y1, X2, Y2, Color.color(red, green, blue, 1)));
                counter = 0;
            }
        }
        
        scanner.close();
        System.out.println("Done");
        ts.setEnd("Done");
        System.out.println(ts);
    }
    
    public void RAFRead(int level) throws FileNotFoundException, IOException { 
        String filePath = "/home/jsf3/data/fractal_done.txt";
        ts = new TimeStamp();
        ts.setBegin("Begin Process");
        
        RandomAccessFile RAFile = new RandomAccessFile(filePath, "r");
        FileChannel fc = RAFile.getChannel();
        bbuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, RAFile.length());

        // We send the number of edges but we don't 
        bbuffer.getInt();
        int noOfEdges = bbuffer.getInt();
        level = bbuffer.getInt();

        double X1 = 0;
        double Y1 = 0;
        double X2 = 0;
        double Y2 = 0;
        double red = 0;
        double green = 0;
        double blue = 0;
        
        for(int i = 0; i < noOfEdges; i++) {
            X1 = bbuffer.getDouble();
            X2 = bbuffer.getDouble();
            Y1 = bbuffer.getDouble();
            Y2 = bbuffer.getDouble();
            red = bbuffer.getDouble();
            green = bbuffer.getDouble();
            blue = bbuffer.getDouble();
            drawEdge(new Edge(X1, Y1, X2, Y2, new Color(red, green, blue, 1.0)));
        }

        ts.setEnd("Einde proces");
        System.out.println(ts);
    }
    
    public void setTextNrEdges(String text) {
        labelNrEdgesText.setText(text);
    }
    
    public void setTextCalc(String text) {
        labelCalcText.setText(text);
    }
    
    public void setTextDraw(String text) {
        labelDrawText.setText(text);
    }
    
    public void requestDrawEdges() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
//                kochManager.drawEdges();
            }
        });
    }
    
    private void increaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel < 12) {
            // resetZoom();
            currentLevel++;
            labelLevel.setText("Level: " + currentLevel);
//            kochManager.changeLevel(currentLevel);
        }
    } 
    
    private void decreaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel > 1) {
            // resetZoom();
            currentLevel--;
            labelLevel.setText("Level: " + currentLevel);
//            kochManager.changeLevel(currentLevel);
        }
    } 

    private void fitFractalButtonActionPerformed(ActionEvent event) throws ClassNotFoundException {
        resetZoom();
        drawEdges();
        //requestDrawEdges();
    }
    
    private void kochPanelMouseClicked(MouseEvent event) {
        if (Math.abs(event.getX() - startPressedX) < 1.0 && 
            Math.abs(event.getY() - startPressedY) < 1.0) {
            double originalPointClickedX = (event.getX() - zoomTranslateX) / zoom;
            double originalPointClickedY = (event.getY() - zoomTranslateY) / zoom;
            if (event.getButton() == MouseButton.PRIMARY) {
                zoom *= 2.0;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                zoom /= 2.0;
            }
            zoomTranslateX = (int) (event.getX() - originalPointClickedX * zoom);
            zoomTranslateY = (int) (event.getY() - originalPointClickedY * zoom);
            requestDrawEdges();
        }
    }                                      

    private void kochPanelMouseDragged(MouseEvent event) {
        zoomTranslateX = zoomTranslateX + event.getX() - lastDragX;
        zoomTranslateY = zoomTranslateY + event.getY() - lastDragY;
        lastDragX = event.getX();
        lastDragY = event.getY();
        requestDrawEdges();
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

    private Edge edgeAfterZoomAndDrag(Edge e) {
        Color color = new Color(e.r, e.g, e.b, 1);
        
        return new Edge(
                e.X1 * zoom + zoomTranslateX,
                e.Y1 * zoom + zoomTranslateY,
                e.X2 * zoom + zoomTranslateX,
                e.Y2 * zoom + zoomTranslateY,
                Color.color(e.r, e.g, e.b));
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
