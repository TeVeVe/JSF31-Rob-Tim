package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.GenerateEdgeRunnable;
import jsf31kochfractalfx.JSF31KochFractalFX;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import timeutil.TimeStamp;

/**
 * Created by tverv on 23-Sep-15.
 */
public class KochManager implements Observer {

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

        _koch.setLevel(nxt);
        Edges.clear();

        ts = new TimeStamp();
        ts.setBegin("Before calculating");

        Count.set(0);

        Thread leftEdgeThread = new Thread(new GenerateEdgeRunnable(_koch, this, "l"));
        Thread rightEdgeThread = new Thread(new GenerateEdgeRunnable(_koch, this, "r"));
        Thread bottomEdgeThread = new Thread(new GenerateEdgeRunnable(_koch, this, "b"));

        leftEdgeThread.start();
        rightEdgeThread.start();
        bottomEdgeThread.start();


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

    public void addCount() {
        Count.set(Count.intValue() + 1);

        if(Count.intValue() == 3){
            ts.setEnd("After Calculating");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    _application.setTextCalc(ts.toString());
                }
            });

            _application.requestDrawEdges();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Edges.add((Edge)arg);
    }
}
