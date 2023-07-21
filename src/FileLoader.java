import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class FileLoader {
    private final AudioInputStream in;
    private final AudioFormat audioFmt;
    private final File file;

    /**
     * @param file of wav for reading
     * @throws UnsupportedAudioFileException if the File does not point to valid audio file data recognized by the system
     * @throws IOException if an input or output error occurs
     */
    public FileLoader(File file) throws UnsupportedAudioFileException, IOException {
        this.file = file;
        in = AudioSystem.getAudioInputStream(file);
        audioFmt = in.getFormat();
    }

    /**
     * @return sample field of sound values
     * @throws IOException error during file reading
     */
    public float[] getSamples() throws IOException {
        int chans   = audioFmt.getChannels();
        int bytes = audioFmt.getSampleSizeInBits() + 7 >> 3;

        float[] samples = new float[(int) in.getFrameLength()]; // frameLength
        byte[] buf = new byte[chans * bytes * 1024];            // [bufferLength]

        int bRead;
        int pos = 0;
        final int sign = 1 << 15;
        final int mask = -1 << 16;
        while ((bRead = in.read(buf)) > -1) {
            for (int b = 0; b < bRead; ) {
                double sum = 0;
                for (int c = 0; c < chans; c++) {
                    if (bytes == 1) {
                        sum += buf[b++] << 8;
                    } else {
                        int sample = 0;
                        if (audioFmt.isBigEndian()) {
                            sample |= (buf[b++] & 0xFF) << 8;
                            sample |= (buf[b++] & 0xFF);
                            b += bytes - 2;
                        } else {
                            b += bytes - 2;
                            sample |= (buf[b++] & 0xFF);
                            sample |= (buf[b++] & 0xFF) << 8;
                        }
                        if ((sample & sign) == sign) sample |= mask;
                        sum += sample;
                    }
                }
                samples[pos++] = (float) (sum / chans);
            }
        }
        return samples;
    }

    /**
     * @return length of sound sample in seconds
     */
    public float getDurationInSeconds() {
        long audioFileLength = file.length();
        int frameSize = audioFmt.getFrameSize();
        float frameRate = audioFmt.getFrameRate();
        return (audioFileLength / (frameSize * frameRate));
    }

}