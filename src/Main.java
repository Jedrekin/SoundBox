import gparts.Canvas;
import gparts.FilePanel;
import gparts.Frame;
import gparts.SoundPanel;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    private static final Dimension FRAME_DIMENSION = new Dimension(1600, 1000);
    private static final Dimension CYCLIC_CANVAS_DIMENSION = new Dimension(700, 700);
    private static final Dimension SOUND_CANVAS_DIMENSION = new Dimension(700, 200);
    private static final Dimension FILE_PANEL_DIMENSION = new Dimension(500, 700);
    private static final Dimension SOUND_PANEL_DIMENSION = new Dimension(500, 100);
    private static final Color BACKGROUND_COLOR = Color.BLACK;

    private static final int FPS = 30;

    private static FileSystem fileSystem;
    private static SamplePainter samplePainter;
    private static SoundPlayer soundPlayer;

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        Frame frame           = new Frame("SoundBox", FRAME_DIMENSION);
        Canvas cyclicCanvas   = Canvas.createCanvas(CYCLIC_CANVAS_DIMENSION);
        Canvas soundCanvas    = Canvas.createCanvas(SOUND_CANVAS_DIMENSION);
        FilePanel filePanel   = new FilePanel(FILE_PANEL_DIMENSION);
        SoundPanel soundPanel = new SoundPanel(SOUND_PANEL_DIMENSION);

        fileSystem = FileSystem.loadDirectory(new File("sng\\"));
        setMusicSample(fileSystem.getIndexedFile());

        for (File f: fileSystem.getDirectoryContent())
            filePanel.addLine(getNameOfFile(f));

        soundPanel.setButtonMouseListener(soundPanel.prevButton, () -> {
            if (soundPlayer.isRunning()) soundPlayer.pause();
            if ((double) soundPlayer.getMicrosecondPosition() / soundPlayer.getMicrosecondLength() > 0.125) {
                soundPlayer.reset();
            } else {
                if (fileSystem.index - 1 > 0) fileSystem.index--;
                else fileSystem.index = fileSystem.getDirectoryFileCount() - 1;
                try {
                    setMusicSample(fileSystem.getIndexedFile());
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ignored) {}

                startPainting(cyclicCanvas, soundCanvas, soundPanel);
            }
        });

        soundPanel.setButtonMouseListener(soundPanel.plstButton, () -> {
            if (soundPlayer.isRunning()) {
                soundPlayer.pause();
            } else {
                startPainting(cyclicCanvas, soundCanvas, soundPanel);
            }
        });

        soundPanel.setButtonMouseListener(soundPanel.nextButton, () -> {
            if (soundPlayer.isRunning()) soundPlayer.pause();
            if (fileSystem.index + 1 < fileSystem.getDirectoryFileCount()) fileSystem.index++;
            else fileSystem.index = 0;

            try {
                setMusicSample(fileSystem.getIndexedFile());
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ignored) {}

            startPainting(cyclicCanvas, soundCanvas, soundPanel);
        });


        soundPanel.setVolumeBarChangeListener(e -> soundPlayer.setChangeVolume(soundPanel.getVolumeBarBValue()));

        frame.add(new JPanel(){{
            setBackground(BACKGROUND_COLOR);
            add(new JPanel() {{
                setBackground(BACKGROUND_COLOR);
                setPreferredSize(new Dimension(700, 900));
                add(cyclicCanvas);
                add(soundCanvas);
                setVisible(true);
            }});
            add(new JPanel() {{
                setBackground(BACKGROUND_COLOR);
                setPreferredSize(new Dimension(700, 900));
                add(filePanel);
                add(soundPanel);
                setVisible(true);
            }});
            setVisible(true);
        }});

        frame.repaint();
        frame.revalidate();
    }

    private static String getNameOfFile(File file) {
        return file.getName().replaceAll(".wav", "");
    }

    private static void setMusicSample(File sampleFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        FileLoader fileLoader = new FileLoader(sampleFile);
        samplePainter = new SamplePainter(fileLoader.getSamples(), fileLoader.getDurationInSeconds(), FPS);
        soundPlayer = new SoundPlayer(sampleFile);
    }

    private static void startPainting(Canvas cyclicCanvas, Canvas soundCanvas, SoundPanel soundPanel) {

        Thread t = new Thread(() -> {
            soundPanel.setSongNameLabelText(fileSystem.getIndexedFileName());
            int timeStep = samplePainter.getTimeStep();
            do {
                double acPos = (double) soundPlayer.getMicrosecondPosition() / soundPlayer.getMicrosecondLength();
                Thread t1 = new Thread( () -> samplePainter.drawSoundWaveCyclicPart(cyclicCanvas, (int) ( acPos * samplePainter.getSampleLen())) );
                Thread t2 = new Thread( () -> samplePainter.drawSoundWavePart(  soundCanvas, (int) ( acPos * samplePainter.getSampleLen())) );
                soundPanel.setMusicBarPosition(acPos);
                t1.start();
                t2.start();
                try {
                    Thread.sleep(timeStep);
                } catch (InterruptedException ignored) {}
            } while(soundPlayer.isRunning());

            if (soundPlayer.getMicrosecondPosition() == soundPlayer.getMicrosecondLength()) {
                if (fileSystem.index + 1 < fileSystem.getDirectoryFileCount()) fileSystem.index++;
                else fileSystem.index = 0;
                try {
                    setMusicSample(fileSystem.getIndexedFile());
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ignored) {}
                startPainting(cyclicCanvas, soundCanvas, soundPanel);
            }
        });
        soundPlayer.play();
        t.start();
    }

}