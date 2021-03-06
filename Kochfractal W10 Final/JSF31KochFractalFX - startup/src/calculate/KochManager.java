package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.GenerateEdgeCallable;
import jsf31kochfractalfx.JSF31KochFractalFX;

import java.util.ArrayList;
import java.util.concurrent.*;

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

        
        
        try {
            Callable generateLeftEdge = new GenerateEdgeCallable(nxt,EdgeType.LEFT,cb);
            Callable generateRightEdge = new GenerateEdgeCallable(nxt,EdgeType.RIGHT,cb);
            Callable generateBottomEdge = new GenerateEdgeCallable(nxt,EdgeType.BOTTOM,cb);
            futLeft = executor.submit(generateLeftEdge);
            futRight = executor.submit(generateRightEdge);
            futBottom = executor.submit(generateBottomEdge);
            System.out.println("Threads executed");

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    addEdges(futLeft);
                    try {
                        cb.await();
                    } catch (InterruptedException e) {
                        return;
                    } catch (BrokenBarrierException e) {
                        return;
                    }
                }
            });

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    addEdges(futRight);
                    try {
                        cb.await();
                    } catch (InterruptedException e) {
                        return;
                    } catch (BrokenBarrierException e) {
                        return;
                    }
                }
            });

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    addEdges(futBottom);
                    try {
                        cb.await();
                    } catch (InterruptedException e) {
                        return;
                    } catch (BrokenBarrierException e) {
                        return;
                    }
                }
            });


            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
