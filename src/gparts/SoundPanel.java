package gparts;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class SoundPanel extends JPanel {

    private static final Color background_Color = Color.GRAY;
    private final Label songNameLabel;
    public Button prevButton;
    public Button plstButton;
    public Button nextButton;
    public VolumeBar volumeBar;
    public MusicBar musicBar;
    public SoundPanel(Dimension dimension) {
        super();
        setPreferredSize(dimension);
        setBackground(background_Color);
        songNameLabel   = new Label    ();
        prevButton      = new Button   (" << ");
        plstButton      = new Button   ("||");
        nextButton      = new Button   (" >> ");
        volumeBar       = new VolumeBar(0, 100, 100);
        musicBar        = new MusicBar ();
        add(songNameLabel);
        add(prevButton);
        add(plstButton);
        add(nextButton);
        add(volumeBar);
        add(musicBar);
        setVisible(true);
    }
    private static class Label extends JLabel {
        private static final int width = 490;
        private static final int height = 30;
        private static final Color backgroundColor = Color.DARK_GRAY;
        private static final Color foregroundColor = Color.WHITE;
        public Label() {
            super();
            setPreferredSize(new Dimension(width, height));
            setBackground(backgroundColor);
            setOpaque(true);
            setForeground(foregroundColor);
            setVisible(true);
        }

        public void setText(String newText) {
            super.setText(newText);
        }
    }
    public void setSongNameLabelText(String text) {
        songNameLabel.setText(" " + text);
    }

    private static class Button extends JButton {
        private static final int width = 95;
        private static final int height = 30;
        private static final Color backgroundColor = Color.DARK_GRAY;
        private static final Color foregroundColor = Color.WHITE;

        public Button(String txt, Icon image) {
            super(txt, image);
            setPreferredSize(new Dimension(width, height));
            setBackground(backgroundColor);
            setForeground(foregroundColor);
            setVisible(true);
        }

        public Button(String txt) {
            this(txt, null);
        }
    }
    public void setButtonMouseListener(Button button, Runnable r) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                r.run();
            }
        });
    }

    private static class VolumeBar extends JSlider {
        private static final int width = 190;
        private static final int height = 30;
        private static final Color backgroundColor = Color.DARK_GRAY;
        private static final Color foregroundColor = Color.WHITE;
        public VolumeBar(int min, int max, int value) {
            super(JSlider.HORIZONTAL, min, max, value);
            setPreferredSize(new Dimension(width, height));
            setBackground(backgroundColor);
            setForeground(foregroundColor);
            setVisible(true);
            setUI(new BasicSliderUI() {
                private static final int TRACK_HEIGHT = 8;
                private static final int TRACK_ARC = 5;
                private final RoundRectangle2D.Float trackShape = new RoundRectangle2D.Float();
                private final Color background_Color = Color.GRAY;
                private final Color track_Color = Color.LIGHT_GRAY;
                private final Color thumb_Color = Color.WHITE;
                @Override
                protected void calculateTrackRect() {
                    super.calculateTrackRect();
                    trackRect.y = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
                    trackRect.height = TRACK_HEIGHT;
                    trackShape.setRoundRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height, TRACK_ARC, TRACK_ARC);
                }
                @Override
                protected void calculateThumbLocation() {
                    super.calculateThumbLocation();
                    thumbRect.y = trackRect.y + (trackRect.height - thumbRect.height) / 2;
                }
                @Override
                protected Dimension getThumbSize() {
                    return new Dimension(10, 10);
                }
                @Override
                public void paintTrack(final Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    Shape clip = g2.getClip();
                    // Paint track background.
                    g2.setColor(background_Color);
                    g2.setClip(trackShape);
                    trackShape.y += 1;
                    g2.fill(trackShape);
                    trackShape.y = trackRect.y;
                    g2.setClip(clip);
                    // Paint selected track.
                    g2.clipRect(0, 0, thumbRect.x + thumbRect.width / 2, slider.getHeight());
                    g2.setColor(track_Color);
                    g2.fill(trackShape);
                    g2.setClip(clip);
                }
                @Override
                public void paintThumb(final Graphics g) {
                    g.setColor(thumb_Color);
                    g.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                }
            });
        }
    }
    public void setVolumeBarChangeListener(ChangeListener changeListener) {
        volumeBar.addChangeListener(changeListener);
    }
    public float getVolumeBarBValue() {
        return (float) volumeBar.getValue() / 100;
    }

    private static class MusicBar extends JLabel {
        private static final int width = 490;
        private static final int height = 20;
        private static final int img_WIDTH = 480;
        private static final int img_HEIGHT = 10;
        private static final Color backgroundColor = Color.DARK_GRAY;
        private static final Color foregroundColor = Color.LIGHT_GRAY;
        private int barPosition;
        public MusicBar() {
            super(null, null, SwingConstants.CENTER);
            this.barPosition = 0;
            setPreferredSize(new Dimension(width, height));
            setBackground(backgroundColor);
            setOpaque(true);
            setVisible(true);
            setMusicBarPosition(0);
        }
        public void setMusicBarPosition (double percent) {
            if (barPosition == (int) (img_WIDTH * percent)) return;
            BufferedImage img = new BufferedImage(img_WIDTH, img_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            barPosition = (int) (img_WIDTH * percent);
            for (int x = 1; x < barPosition - 1; x++)
                for (int y = 1; y < img_HEIGHT - 1; y++)
                    img.setRGB(x, y, foregroundColor.getRGB());
            setIcon(new ImageIcon(img));
        }
    }
    public void setMusicBarPosition(double percent) {
        if (percent < 0 || percent > 1) throw new IllegalArgumentException();
        musicBar.setMusicBarPosition(percent);
    }

}
