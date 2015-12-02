package calculate;

import javafx.application.Platform;
import javafx.concurrent.Task;
import jsf31kochfractalfx.GenerateEdgeTask;
import jsf31kochfractalfx.JSF31KochFractalFX;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

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
    GenerateEdgeTask generateLeftEdge;
    GenerateEdgeTask generateRightEdge;
    GenerateEdgeTask generateBottomEdge;

    public KochManager(JSF31KochFractalFX application){
        _application = application;
        _koch = application.Koch;
        ts = new TimeStamp();
        
        cb = new CyclicBarrier(3, () -> {
            ts.setEnd("After Calculating");
            Platform.runLater(() -> {
                try {
                Edges.addAll(generateLeftEdge.get());
                Edges.addAll(generateRightEdge.get());
                Edges.addAll(generateBottomEdge.get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                _application.setTextCalc(ts.toString());
            });

            System.out.println("requestDraw");

            _application.requestDrawEdges();
        });

        executor = Executors.newFixedThreadPool(3);
    }

    public void changeLevel(int nxt) {

        cancel();
        if(nxt == 0) {
            nxt = 1;
        }

        _koch.setLevel(nxt);
        Edges.clear();

        ts = new TimeStamp();
        ts.setBegin("Before calculating");

        
        
        try {
            generateLeftEdge = _application.createTask(EdgeType.LEFT);
            generateRightEdge =_application.createTask(EdgeType.RIGHT);
            generateBottomEdge = _application.createTask(EdgeType.BOTTOM);

            executor.submit(generateLeftEdge);
            executor.submit(generateRightEdge);
            executor.submit(generateBottomEdge);
            

            System.out.println("Threads executed");

//            executor.execute(new Task<Void>() {
//                @Override
//                protected Void call() throws Exception {
//
//                    //_application.clearKochPanel();
//
//                    while (!generateLeftEdge.isDone() || !generateRightEdge.isDone() || !generateBottomEdge.isDone()) {
//
//                        if(!generateLeftEdge.isDone()) {
//                            Edges.addAll(generateLeftEdge.getValue());
//                        }
//
//                        if(!generateRightEdge.isDone()) {
//                            Edges.addAll(generateRightEdge.getValue());
//                        }
//
//                        if(!generateBottomEdge.isDone()) {
//                            Edges.addAll(generateBottomEdge.getValue());
//                        }
//
//                        for(Edge e: Edges) {
//                            _application.drawEdge(e);
//                        }
//                    }
//
//                    return null;
//                }
//            });
            
            executor.execute(() -> {
                try {
                    cb.await();
                } catch (InterruptedException e) {
                    return;
                } catch (BrokenBarrierException e) {
                    return;
                }
            });

            executor.execute(() -> {
                try {
                    cb.await();
                } catch (InterruptedException e) {
                    return;
                } catch (BrokenBarrierException e) {
                    return;
                }
            });

            executor.execute(new Runnable() {
                @Override
                public void run() {
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
    
    public void cancel() {
        try {
            generateBottomEdge.cancel();
            generateLeftEdge.cancel();
            generateRightEdge.cancel();
            //_application.BindBars();
        } catch (NullPointerException exception) {
           
        }
    }
    
    public synchronized void updateEdges(Edge e) {
        Edges.add(e);
        Edge e1 = e.clone();
        e1.color = Color.WHITE;
        _application.callDrawEdge(e1);
    }

    public synchronized void addEdge(Edge e) {
        Edges.add(e);
    }
}