package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player2 extends Sprite {

    private static final int START_X = 370; // Slightly offset from Player 1
    private static final int START_Y = 540;

    private double vx = 0;
    private double vy = 0;
    private double ax = 0;
    private double ay = 0;

    private final double ACCEL_X = 0.3;
    private final double ACCEL_Y = 0.7;
    private final double MAX_SPEED_X = 9;
    private final double MAX_SPEED_Y = 6;
    private final double FRICTION = 0.40;

    private boolean braking = false;
    private int width;
    private Rectangle bounds = new Rectangle(175, 135, 17, 32);

    private final int SCALE_FACTOR = 2;
    private Image[] idleFrames = new Image[2];
    private Image[] leftFrames = new Image[2];
    private Image[] rightFrames = new Image[2];

    private int currentFrame = 0;
    private int frameCounter = 0;
    private final int frameDelay = 4;

    private enum State {
        IDLE, TURNING_LEFT, TURNING_RIGHT
    }

    private State currentState = State.IDLE;

    public Player2() {
        initPlayer();
    }

    private void initPlayer() {
        // Load animation frames
        idleFrames[0] = loadScaledImage("src/images/red_1.png", SCALE_FACTOR);
        idleFrames[1] = loadScaledImage("src/images/red_2.png", SCALE_FACTOR);

        leftFrames[0] = loadScaledImage("src/images/redleftturn_1.png", SCALE_FACTOR);
        leftFrames[1] = loadScaledImage("src/images/redleftturn_2.png", SCALE_FACTOR);

        rightFrames[0] = loadScaledImage("src/images/redrightturn_1.png", SCALE_FACTOR);
        rightFrames[1] = loadScaledImage("src/images/redrightturn_2.png", SCALE_FACTOR);

        setImage(idleFrames[0]);
        setX(START_X);
        setY(START_Y);
        width = idleFrames[0].getWidth(null);
    }

    private Image loadScaledImage(String path, int scale) {
        ImageIcon icon = new ImageIcon(path);
        int w = icon.getIconWidth() * scale;
        int h = icon.getIconHeight() * scale;
        return icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    public void act() {
        // Apply acceleration
        vx += ax;
        vy += ay;

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

        updateAnimation();
    }

    private void updateAnimation() {
        frameCounter++;
        if (frameCounter >= frameDelay) {
            currentFrame = (currentFrame + 1) % 2;
            frameCounter = 0;
        }

        switch (currentState) {
            case TURNING_LEFT:
                setImage(leftFrames[currentFrame]);
                break;
            case TURNING_RIGHT:
                setImage(rightFrames[currentFrame]);
                break;
            case IDLE:
            default:
                setImage(idleFrames[currentFrame]);
                break;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A) {
            ax = -ACCEL_X;
            currentState = State.TURNING_LEFT;
        }
        if (key == KeyEvent.VK_D) {
            ax = ACCEL_X;
            currentState = State.TURNING_RIGHT;
        }
        if (key == KeyEvent.VK_W) {
            ay = -ACCEL_Y;
        }
        if (key == KeyEvent.VK_S) {
            ay = ACCEL_Y;
        }
        if (key == KeyEvent.VK_F) {
            // You can handle shooting in Scene1 when this key is pressed
        }
        if (key == KeyEvent.VK_G) {
            braking = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A && ax < 0) {
            ax = 0;
            currentState = State.IDLE;
        }

        if (key == KeyEvent.VK_D && ax > 0) {
            ax = 0;
            currentState = State.IDLE;
        }

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S) {
            ay = 0;
        }

        if (key == KeyEvent.VK_G) {
            braking = false;
        }
    }
}
