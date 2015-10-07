package jsf31kochfractalfx;

import calculate.Edge;
import calculate.KochFractal;
import calculate.KochManager;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by tverv on 30-Sep-15.
 */
public class GenerateEdgeRunnable implements Runnable, Observer {
    private final String _type;
    private final KochFractal _koch;
    private final int _level;
    public KochManager _manager;

    public  GenerateEdgeRunnable(KochManager manager, int level, String type){
        _type = type;
        _koch = new KochFractal();
        _manager = manager;
        _level = level;

        _koch.addObserver(this);
    }

    @Override
    public void run() {

        _koch.setLevel(_level);

        switch (_type) {
            case "l":
                _koch.generateLeftEdge();
                break;
            case "r":
                _koch.generateRightEdge();
                break;
            case "b":
                _koch.generateBottomEdge();
                break;
        }

        _manager.addCount();
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        _manager.addEdge((Edge)arg);
    }
}
