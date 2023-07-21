package client;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author tungpd
 */
public enum SoundEffect {

    MessageReceive("/audio/boing.wav", false), FileSharing("/audio/alarm.wav", false);

    private Clip clip;
    private boolean isLooped;

    SoundEffect(String filePath, boolean isLooped) {
        try {
            this.isLooped = isLooped;
            URL url = this.getClass().getResource(filePath);
            AudioInputStream audioIS = AudioSystem.getAudioInputStream(url);

            clip = AudioSystem.getClip();
            clip.open(audioIS);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("[SoundEffect]" + e.getMessage());
        }
    }

    public void play() {
        if (clip.isRunning()) {
            clip.stop();
        }

        clip.setFramePosition(0);
        clip.start();

        if (isLooped) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
        }
    }
}
