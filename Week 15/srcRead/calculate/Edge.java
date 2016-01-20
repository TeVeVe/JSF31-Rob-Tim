/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 *
 * @author Peter Boots
 */
public class Edge implements Serializable {
    public double X1, Y1, X2, Y2;
    public double r,g,b;
    
    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
    }
    
    public Color getColor() {
        Color getColor = Color.hsb(r, g, b);
        return getColor;
    }
    
    @Override
    public Edge clone() {
        return new Edge(X1, Y1, X2, Y2, Color.color(r, g, b));
    }
}
