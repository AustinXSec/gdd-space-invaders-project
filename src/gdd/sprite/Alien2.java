package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Alien2 extends Sprite {

    private int dx;

    private Image[] frames;
    private int currentFrame = 0;
    private int animationCounter = 0;
    private final int animationSpeed = 6; // Adjust speed as needed

    public Alien2(int x, int y, int dx) {
        this.x = x;
        this.y = y;
        this.dx = dx;

        loadFrames();
    }

    private void loadFrames() {
        frames = new Image[6];
        for (int i = 0; i < frames.length; i++) {
            String path = "src/images/alien" + i + ".png";
            ImageIcon icon = new ImageIcon(path);

            int width = 15 * SCALE_FACTOR;
            int height = 15 * SCALE_FACTOR;

            // Ensure icon is fully loaded before flipping
            Image rawImage = icon.getImage();
            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.drawImage(rawImage, 0, 0, width, height, null);
            g2d.dispose();

            // Flip if coming from right (dx < 0)
            if (dx < 0) {
                frames[i] = flipImageHorizontally(scaledImage);
            } else {
                frames[i] = scaledImage;
            }
        }

        setImage(frames[0]);
    }

    private Image flipImageHorizontally(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flipped.createGraphics();

        // Flip image horizontally
        g2d.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
        g2d.dispose();

        return flipped;
    }

    @Override
    public void act() {
        move();
        animate();
    }

    public void move() {
        x += dx;
    }

    private void animate() {
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            currentFrame = (currentFrame + 1) % frames.length;
            setImage(frames[currentFrame]);
        }
    }
}
