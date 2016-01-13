package calculate;

import javafx.application.Platform;
import readkochfractal.JSF31KochFractalFX;

import java.util.ArrayList;
import java.util.concurrent.*;
import timeutil.TimeStamp;

/**
 * Created by tverv on 23-Sep-15.
 */
public class KochManager{

    private JSF31KochFractalFX _application;
    private KochFractal _koch;
    public ArrayList<Edge> Edges = new ArrayList<>();
    private TimeStamp ts;
    private CyclicBarrier cb;
    private ExecutorService executor;
    private Future<ArrayList<Edge>> futLeft;
    private Future<ArrayList<Edge>> futRight;
    private Future<ArrayList<Edge>> futBottom;

    public KochManager(JSF31KochFractalFX application){
        _application = application;
        _koch = application.Koch;
        ts = new TimeStamp();
        
        cb = new CyclicBarrier(3, new Runnable()
        {
            @Override
            public void run() {
                ts.setEnd("After Calculating");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        _application.setTextCalc(ts.toString());                    }
                });

                System.out.println("requestDraw");
                _application.requestDrawEdges();
            }
        });

        executor = Executors.newFixedThreadPool(3);
    }

    public void changeLevel(int nxt) {

        if(nxt == 0) {
            nxt = 1;
        }

        _koch.setLevel(nxt);
        Edges.clear();

        ts = new TimeStamp();
        ts.setBegin("Before calculating");
        
        _koch.generateBottomEdge();
        _koch.generateLeftEdge();
        _koch.generateRightEdge();
    }

    public synchronized void addEdges(Future<ArrayList<Edge>> fut) {
        try {
            Edges.addAll(fut.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public synchronized void drawEdges() {
        TimeStamp ts = new TimeStamp();
        ts.setBegin("Before drawing");

        _application.clearKochPanel();

        for(Edge e:  Edges) {
            _application.drawEdge(e);
        }

        ts.setEnd("After drawing");

        _application.setTextDraw(ts.toString());
        _application.setTextNrEdges(Integer.toString(_koch.getNrOfEdges()));
    }

    public synchronized void addEdge(Edge e) {
        Edges.add(e);
    }
}
