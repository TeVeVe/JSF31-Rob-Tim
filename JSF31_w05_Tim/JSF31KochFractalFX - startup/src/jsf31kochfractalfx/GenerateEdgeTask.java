package jsf31kochfractalfx;

import calculate.Edge;
import calculate.KochFractal;
import calculate.KochManager;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by tverv on 30-Sep-15.
 */
public class GenerateEdgeTask extends Task<ArrayList<Edge>> implements Observer {
    private final EdgeType _type;
    private final KochFractal _koch;
    private final int _level;
    private ArrayList<Edge> _edges;
    private int sleepTime;
    private int currentEdge;
    private KochManager km;

    public GenerateEdgeTask(int level, EdgeType type, KochManager km){
        _type = type;
        _koch = new KochFractal();
        _level = level;
        _edges = new ArrayList<>();
        this.km = km;

        _koch.addObserver(this);
    }

    @Override
    public ArrayList<Edge> call() throws Exception {

        sleepTime = 0;
        _koch.setLevel(_level);

        switch (_type.name()) {
            case "LEFT":
                sleepTime = 0;
                _koch.generateLeftEdge();
                break;
            case "RIGHT":
                sleepTime = 1;
                _koch.generateRightEdge();
                break;
            case "BOTTOM":
                sleepTime = 2;
                _koch.generateBottomEdge();
                break;
        }
        
        System.out.println(_type.name());
        return _edges;
    }
    
    @Override
    public void cancelled() {
        super.cancelled();
        updateMessage(currentEdge + " (cancelled)");
        System.out.println(currentEdge + " (cancelled)");
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        if (isCancelled()) {
            _koch.cancel();
            //cancelled();
        }
        _edges.add((Edge)arg);
        
        currentEdge++;
        updateMessage("Number of Edges: " + currentEdge);
        updateProgress(currentEdge, _koch.getNrOfEdges() / 3);

        km.updateEdges((Edge)arg);
        
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            if (isCancelled()) {
                _koch.cancel();
                cancelled();
            }
        }
    }
}
