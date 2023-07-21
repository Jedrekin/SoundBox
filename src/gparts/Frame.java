package gparts;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    /**
     * @param name of created window
     * @param dimension size of crated window including width and height
     */
    public Frame(String name, Dimension dimension) {
        super(name);
        setVisible(true);
        setSize(dimension);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
