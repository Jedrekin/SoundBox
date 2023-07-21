import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
    private final Clip clip;
    private long timePosition;
    public SoundPlayer(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = stream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        clip = (Clip) AudioSystem.getLine(info);
        clip.open(stream);
        timePosition = 0;
    }
    public long getMicrosecondLength() {
        return clip.getMicrosecondLength();
    }
    public long getMicrosecondPosition() {
        return clip.getMicrosecondPosition();
    }
    public boolean isRunning() {
        return clip.isRunning();
    }
    public void play() {
        clip.setMicrosecondPosition(timePosition);
        clip.start();
    }
    public void pause() {
        clip.stop();
        timePosition = clip.getMicrosecondPosition();
    }
    public void reset() {
        timePosition = 0;
        clip.setMicrosecondPosition(timePosition);
        clip.start();
    }

    public void setChangeVolume(float value) { // number between 0 and 1 (loudest)
        FloatControl gainControl = (FloatControl) clip
                .getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(value) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }
}
