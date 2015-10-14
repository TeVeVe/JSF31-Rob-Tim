package jsf31kochfractalfx;

import calculate.Edge;
import calculate.KochFractal;
import calculate.KochManager;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by tverv on 30-Sep-15.
 */
public class GenerateEdgeRunnable implements Callable, Observer {
    private final EdgeType _type;
    private final KochFractal _koch;
    private final int _level;
    private ArrayList<Edge> _edges;
    public CyclicBarrier _cb;

    public  GenerateEdgeRunnable(int level, EdgeType type, CyclicBarrier cb){
        _type = type;
        _koch = new KochFractal();
        _level = level;
        _edges = new ArrayList<>();
        _cb = cb;

        _koch.addObserver(this);
    }

    @Override
    public Object call() throws Exception {

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
        
        _cb.await();
        
        return _edges;
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        _edges.add((Edge)arg);
        //_manager.addEdge((Edge)arg);
    }
}
