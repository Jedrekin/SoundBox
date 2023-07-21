package gparts;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FilePanel extends JPanel {
    private static final Color background = Color.GRAY;
    private final List<FileLine> lines;
    public FilePanel(Dimension dimension) {
        super();
        lines = new ArrayList<>();
        setPreferredSize(dimension);
        setBackground(background);
        setVisible(true);
    }
    public void addLine(String text) {
        FileLine fl = new FileLine(text);
        lines.add(fl);
        add(fl);
    }
    private static class FileLine extends JLabel {
        private static final int width = 490;
        private static final int height = 20;
        private static final Color background_Color = Color.DARK_GRAY;
        private static final Color foreground_Color = Color.WHITE;
        public FileLine(String text) {
            super( " " + text);
            setPreferredSize(new Dimension(width, height));
            setBackground(background_Color);
            setOpaque(true);
            setForeground(foreground_Color);
            setVisible(true);
        }

    }
}
