package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.GenerateEdgeRunnable;
import jsf31kochfractalfx.JSF31KochFractalFX;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import timeutil.TimeStamp;

/**
 * Created by tverv on 23-Sep-15.
 */
public class KochManager{

    private JSF31KochFractalFX _application;
    private KochFractal _koch;
    public ArrayList<Edge> Edges = new ArrayList<>();
    public AtomicInteger Count;
    private TimeStamp ts;

    public KochManager(JSF31KochFractalFX application){
        _application = application;
        _koch = application.Koch;
        ts = new TimeStamp();
        Count = new AtomicInteger();
        Count.set(0);
    }

    public void changeLevel(int nxt) {

        if(nxt == 0) {
            nxt = 1;
        }

        _koch.setLevel(nxt);
        Edges.clear();

        ts = new TimeStamp();
        ts.setBegin("Before calculating");

        Count.set(0);

        CyclicBarrier cb = new CyclicBarrier(3, new Runnable()
        {
            @Override
            public void run() {

                ts.setEnd("After Calculating");
                _application.setTextCalc(ts.toString());
                System.out.println("requestDraw");
                _application.requestDrawEdges();
            }
        });
        
        try {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            for(int i = 1; i <= 3; i++)
            {
                Callable generateEdge = new GenerateEdgeRunnable(this,nxt,i,cb);
                Future<ArrayList<Edge>> fut = executor.submit(generateEdge);
                Edges.addAll(fut.get());

            }
            executor.shutdown();
            
//            while (!executor.isTerminated())
//            {
//
//            }
            
            System.out.println("Threads executed");
            cb.reset();
            
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

    public synchronized void addCount() {
        //Count.incrementAndGet();
        //System.out.println("Foo");
        //System.out.println(Count);

        //if(Count.intValue() == 3){
            //System.out.println("Bar");
//            ts.setEnd("After Calculating");
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    _application.setTextCalc(ts.toString());
//                }
//            });
//
//            System.out.println("requestDraw");
//            _application.requestDrawEdges();
        //}
    }
}
