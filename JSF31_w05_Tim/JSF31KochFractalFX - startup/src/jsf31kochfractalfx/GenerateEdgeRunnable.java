package jsf31kochfractalfx;

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
    public KochManager _manager;

    public  GenerateEdgeRunnable(KochFractal koch, KochManager manager, String type){
        _type = type;
        _koch = koch;
        _manager = manager;

        _koch.addObserver(this);
    }

    @Override
    public void run() {

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

    }
}
