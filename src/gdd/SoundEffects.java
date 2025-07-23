package gdd;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundEffects {

    private static Clip loadClip(String path) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error loading sound: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private static void playClip(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    // === Sound effect clips ===
    private static final Clip laser = loadClip("src/audio/laser.wav");
    private static final Clip explosion = loadClip("src/audio/explosion.wav");
    private static final Clip powerup = loadClip("src/audio/powerupSF.wav");

    public static void playLaser() {
    new Thread(() -> {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("src/audio/laser.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            // Wait for clip to finish playing before closing resources
            Thread.sleep(clip.getMicrosecondLength() / 10);
            clip.close();
            audioIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}

    public static void playExplosion() {
        playClip(explosion);
    }

    public static void playPowerUp() {
        playClip(powerup);
    } 

   
}
