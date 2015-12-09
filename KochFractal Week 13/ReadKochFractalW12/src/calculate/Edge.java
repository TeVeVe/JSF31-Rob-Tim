/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

import javafx.scene.paint.Color;

/**
 *
 * @author Peter Boots
 */
public class Edge {
    public double X1, Y1, X2, Y2;
    public transient Color color;
    public double red, green, blue;
    
    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        this.color = color;
        
        color.getHue();
        color.getBrightness();
        color.getSaturation();
    }
    
    private Color getColor() {
        Color getColor = Color.hsb(blue, green, red);
        return getColor;
    }
    
    @Override
    public Edge clone() {
        return new Edge(X1, Y1, X2, Y2, color);
    }
}
