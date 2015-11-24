package jsf31kochfractalfx;

import calculate.Edge;
import calculate.KochFractal;
import calculate.KochManager;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by tverv on 30-Sep-15.
 */
public class GenerateEdgeTask extends Task<ArrayList<Edge>> implements Observer {
    private final EdgeType _type;
    private final KochFractal _koch;
    private final int _level;
    private ArrayList<Edge> _edges;
    private int progress;
    private int currentEdge;

    public GenerateEdgeTask(int level, EdgeType type){
        _type = type;
        _koch = new KochFractal();
        _level = level;
        _edges = new ArrayList<>();

        _koch.addObserver(this);
    }

    @Override
    public ArrayList<Edge> call() throws Exception {

        progress = 0;
        _koch.setLevel(_level);

        switch (_type.name()) {
            case "LEFT":
                _koch.generateLeftEdge();
                break;
            case "RIGHT":
                _koch.generateRightEdge();
                break;
            case "BOTTOM":
                _koch.generateBottomEdge();
                break;
        }

        System.out.println(_type.name());
        return _edges;
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        _edges.add((Edge)arg);
        
        currentEdge++;
        updateMessage("Number of Edges: " + currentEdge);
        updateProgress(currentEdge, _koch.getNrOfEdges() / 3);
        updateValue(_edges);
        
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            if (isCancelled()) {
                _koch.cancel();
                cancelled();
            }
        }
    }
}
