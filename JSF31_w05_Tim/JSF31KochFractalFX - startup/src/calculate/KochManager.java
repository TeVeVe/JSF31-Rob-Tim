package calculate;

import jsf31kochfractalfx.JSF31KochFractalFX;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import timeutil.TimeStamp;

/**
 * Created by tverv on 23-Sep-15.
 */
public class KochManager implements Observer {

    private JSF31KochFractalFX _application;
    private KochFractal _koch;
    private ArrayList<Edge> _edges = new ArrayList<>();

    public KochManager(JSF31KochFractalFX application){
        _application = application;
        _koch = application.Koch;
    }

    public void changeLevel(int nxt) {
        _koch.setLevel(nxt);

        _edges.clear();

        TimeStamp ts = new TimeStamp();
        ts.setBegin("Before calculating");
        _koch.generateLeftEdge();
        _koch.generateRightEdge();
        _koch.generateBottomEdge();
        ts.setEnd("After Calculating");

        _application.setTextCalc(ts.toString());

        drawEdges();
    }

    public void drawEdges() {
        TimeStamp ts = new TimeStamp();
        ts.setBegin("Before drawing");

        _application.clearKochPanel();

        for(Edge e: _edges) {
            _application.drawEdge(e);
        }

        ts.setEnd("After drawing");

        _application.setTextDraw(ts.toString());
        _application.setTextNrEdges(Integer.toString(_koch.getNrOfEdges()));
    }

    @Override
    public void update(Observable o, Object arg) {
        _edges.add((Edge) arg);
    }
}
