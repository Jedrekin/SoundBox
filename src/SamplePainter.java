import gparts.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SamplePainter {
    private static final int colorRGB_INT = Color.WHITE.getRGB();
    private final float[] sample;
    private final float max;
    private final int timeStep;
    private final int step;
    private final double alpha;

    /**
     * @param sample float data of sound sample
     * @param duration length of the sample in [time] s
     */
    public SamplePainter(float[] sample, float duration, int period) {
        this.sample = sample;
        max = maxSampleValue();
        timeStep = 1000 / period;
        step = sample.length / (int) duration;
        alpha = 2 * Math.PI / step;
    }

    /**
     * @return the highest or the lowest float value of sample[]
     */
    private float maxSampleValue() {
        float max = 0;
        for (float sample : this.sample)
            if (sample > max)              max = sample;
            else if ((-1 * sample) > max)  max = -1 * sample;
        return max;
    }

    /**
     * @return sample length
     */
    public int getSampleLen() {
        return sample.length;
    }

    /**
     * @return length of one time step in ms
     */
    public int getTimeStep() {
        return timeStep;
    }

    /**
     * @param canvas that part of the sample is shown in the picture
     */
    public void drawSoundWave(Canvas canvas) {
        BufferedImage img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int maxLength = sample.length / img.getWidth();
        for (int i = 0; i < img.getWidth(); i++) {
            int y = ((int) sample[i * maxLength] * img.getHeight() / 2) / (int) max;
            for (int j = 0; j < Math.abs(y); j++) {
                if (y > 0)
                    img.setRGB(i, img.getHeight()/2 - j - 1, colorRGB_INT);
                else if (y < 0)
                    img.setRGB(i, img.getHeight()/2 + j + 1, colorRGB_INT);
            }
            img.setRGB(i, img.getHeight()/2 - y, colorRGB_INT);
            img.setRGB(i, img.getHeight()/2, colorRGB_INT);
        }
        canvas.setIcon(new ImageIcon(img));
    }

    /**
     * @param canvas that part of the sample is shown in the picture
     * @param begin of sample position
     */
    public void drawSoundWavePart(Canvas canvas, int begin) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (float i = 0; i < width; i += alpha) {
            if (begin + i > sample.length - 1) break; // ??
            img.setRGB(
                    (int) i,
                    height/2 - 1 - ((int) sample[(int)((float) begin + i)] * height / 2) / (int) max,
                    colorRGB_INT);
        }
        canvas.setIcon(new ImageIcon(img));
    }

    /**
     * @param canvas that part of the sample is shown in the picture
     * @param density of image quality
     */
    public void drawSoundWaveCyclic(Canvas canvas, float density) {
        if (density <= 0 || density > 1) throw new IllegalArgumentException();
        BufferedImage img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int sLen = sample.length;
        double alpha = Math.PI * 2 / (sample.length * density);
        double length = (double) img.getWidth() / 4;
        for (double i = 0; i < Math.PI * 2; i += alpha) {
            double value = length * (1 + sample[(int) (i / (2 * Math.PI) * sLen)] / max);
            img.setRGB(
                    (int) (img.getWidth() / 2 - 1 + Math.sin(i) * value),
                    (int) (img.getHeight()/ 2 - 1 - Math.cos(i) * value),
                    colorRGB_INT);
        }
        canvas.setIcon(new ImageIcon(img));
    }

    /**
     * @param canvas that part of the sample is shown in the picture
     * @param begin of sample position
     */
    public void drawSoundWaveCyclicPart(Canvas canvas, int begin) {
        BufferedImage img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int imgW = img.getWidth() / 2;
        double value;
        double length = (double) img.getWidth() / 4;
        for (double i = -Math.PI; i < Math.PI; i += alpha) {
            int pos = (int) (begin + i / alpha);
            if (pos < 0 || pos > sample.length - 1) pos = 0;
            value = length * (1 + sample[pos] / max);
            img.setRGB(
                    (int) (imgW - 1 + Math.sin(i) * value),
                    (int) (imgW - 1 + Math.cos(i) * value),
                    colorRGB_INT);
        }
        canvas.setIcon(new ImageIcon(img));
    }
}
