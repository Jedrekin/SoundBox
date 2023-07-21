package gparts;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JLabel {
    private Canvas(Dimension dimension) {
        super();
        setPreferredSize(dimension);
        setVisible(true);
    }
    public static Canvas createCanvas(Dimension dimension) {
        return new Canvas(dimension);
    }
}
