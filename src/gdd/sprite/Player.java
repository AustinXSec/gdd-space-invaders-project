package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;

    private double vx = 0;
    private double vy = 0;
    private double ax = 0;
    private double ay = 0;

    // Separate speed/acceleration for X and Y axes
    private final double ACCEL_X = 0.7;        // Faster side-to-side accel
    private final double ACCEL_Y = 0.4;        // Smoother up/down
    private final double MAX_SPEED_X = 9;      // Faster max speed horizontally
    private final double MAX_SPEED_Y = 6;      // Keep vertical smooth
    private final double FRICTION = 0.15;

    private boolean braking = false;
    private int width;
    private Rectangle bounds = new Rectangle(175, 135, 17, 32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        var scaledImage = ii.getImage().getScaledInstance(
                ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);
        width = getImage().getWidth(null);
    }

    public void act() {
        // Apply acceleration
        vx += ax;
        vy += ay;

        // Apply friction (or braking)
        double friction = braking ? FRICTION * 3 : FRICTION;

        if (ax == 0) {
            if (vx > 0) vx -= friction;
            if (vx < 0) vx += friction;
            if (Math.abs(vx) < friction) vx = 0;
        }

        if (ay == 0) {
            if (vy > 0) vy -= friction;
            if (vy < 0) vy += friction;
            if (Math.abs(vy) < friction) vy = 0;
        }

        // Clamp speed
        vx = Math.max(-MAX_SPEED_X, Math.min(MAX_SPEED_X, vx));
        vy = Math.max(-MAX_SPEED_Y, Math.min(MAX_SPEED_Y, vy));

        // Move
        x += vx;
        y += vy;

        // Keep inside window
        if (x < 0) x = 0;
        if (x > BOARD_WIDTH - width) x = BOARD_WIDTH - width;

        if (y < 0) y = 0;
        if (y > GROUND - PLAYER_HEIGHT) y = GROUND - PLAYER_HEIGHT;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            ax = -ACCEL_X;
        }
        if (key == KeyEvent.VK_RIGHT) {
            ax = ACCEL_X;
        }
        if (key == KeyEvent.VK_UP) {
            ay = -ACCEL_Y;
        }
        if (key == KeyEvent.VK_DOWN) {
            ay = ACCEL_Y;
        }
        if (key == KeyEvent.VK_SHIFT) {
            braking = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            ax = 0;
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            ay = 0;
        }

        if (key == KeyEvent.VK_SHIFT) {
            braking = false;
        }
    }
}
