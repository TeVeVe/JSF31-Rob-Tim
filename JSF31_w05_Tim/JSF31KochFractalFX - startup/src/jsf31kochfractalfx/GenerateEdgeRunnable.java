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
    private final int _type;
    private final KochFractal _koch;
    private final int _level;
    public KochManager _manager;
    private ArrayList<Edge> _edges;
    public CyclicBarrier _cb;

    public  GenerateEdgeRunnable(KochManager manager, int level, int type, CyclicBarrier cb){
        _type = type;
        _koch = new KochFractal();
        _manager = manager;
        _level = level;
        _edges = new ArrayList<>();
        _cb = cb;

        _koch.addObserver(this);
    }

    @Override
    public Object call() throws Exception {

        _koch.setLevel(_level);

        switch (_type) {
            case 1:
                _koch.generateLeftEdge();
                break;
            case 2:
                _koch.generateRightEdge();
                break;
            case 3:
                _koch.generateBottomEdge();
                break;
        }

        System.out.println(_type);
        _manager.addCount();
        
            try {
                _cb.await();
            } catch(BrokenBarrierException e)
            {
                return _edges;
            }
        return null;
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        _edges.add((Edge)arg);
        //_manager.addEdge((Edge)arg);
    }
}
