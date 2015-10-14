package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.GenerateEdgeRunnable;
import jsf31kochfractalfx.JSF31KochFractalFX;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import jsf31kochfractalfx.EdgeType;

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
    }

    public void changeLevel(int nxt) {

        if(nxt == 0) {
            nxt = 1;
        }

        _koch.setLevel(nxt);
        Edges.clear();

        ts = new TimeStamp();
        ts.setBegin("Before calculating");

        
        
        try {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            
            Callable generateLeftEdge = new GenerateEdgeRunnable(nxt,EdgeType.LEFT,cb);
            Callable generateRightEdge = new GenerateEdgeRunnable(nxt,EdgeType.RIGHT,cb);
            Callable generateBottomEdge = new GenerateEdgeRunnable(nxt,EdgeType.BOTTOM,cb);
            Future<ArrayList<Edge>> futLeft = executor.submit(generateLeftEdge);
            Future<ArrayList<Edge>> futRight = executor.submit(generateRightEdge);
            Future<ArrayList<Edge>> futBottom = executor.submit(generateBottomEdge);
            executor.shutdown();
            
            Edges.addAll(futLeft.get());
            Edges.addAll(futRight.get());
            Edges.addAll(futBottom.get());
            System.out.println("Threads executed");
            
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
