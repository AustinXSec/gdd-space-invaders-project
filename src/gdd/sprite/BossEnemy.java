package gdd.sprite;

import gdd.Global;
import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;

public class BossEnemy {

    private float x, y;
    private int width, height;
    private boolean visible;

    private Image[] frames;
    private Image image;
    private int currentFrame = 0;
    private int animationSpeed = 5; 
    private int animationCounter = 0;

    private float dx, dy;
    private final Random rand = new Random();
 
    private int directionTimer = 0;
    private final int directionChangeInterval = 180;
  
    private boolean entering = true;
    private final float entranceSpeed = 1.0f;
    private final int entranceY = 100;

    private boolean diveMode = false;
    private final int diveDuration = 240;
    private int diveTimer = 0;

    private boolean risingAfterDive = false;
    private final float risingSpeed = 2.0f;  // pixels per frame for rising

    private int health = 10000;
    private boolean dying = false;

    public BossEnemy(int startX) {
        this.x = startX;
        this.y = -100;
        this.visible = true;

        loadFrames();
        randomizeDirection();
    }

    private void loadFrames() {
        frames = new Image[9];
        for (int i = 0; i < frames.length; i++) {
            String path = "src/images/boss" + i + ".png";
            Image img = new ImageIcon(path).getImage();
            if (img == null || img.getWidth(null) <= 0) {
                System.err.println("Failed to load: " + path);
                img = new ImageIcon("src/images/boss0.png").getImage(); // fallback
            }
            frames[i] = img;
        }

        image = frames[0];
        width = image.getWidth(null);
        height = image.getHeight(null);
    }

    private void randomizeDirection() {
        float angle = (float) (Math.PI / 4 + rand.nextFloat() * (Math.PI / 2));
        float speed = 2.5f + rand.nextFloat() * 3.4f;

        dx = (float) Math.cos(angle) * speed;
        dy = (float) Math.sin(angle) * speed;

        if (x < Global.BOARD_WIDTH / 2) dx = Math.abs(dx);
        if (rand.nextFloat() < 0.2f) dx *= -1;
    }

   public void move() {
    if (!visible) return;

    if (entering) {
        y += entranceSpeed;
        if (y >= entranceY) {
            y = entranceY;
            entering = false;
            directionTimer = 0;
        }
    } else if (risingAfterDive) {
        // Smoothly rise after dive
        y -= risingSpeed;
        if (y <= entranceY + 60) {
            y = entranceY + 60;
            risingAfterDive = false;
            
            directionTimer = 0;
        }
    } else {
        directionTimer++;
        diveTimer++;

        if (directionTimer >= directionChangeInterval) {
            randomizeDirection();
            directionTimer = 0;

            if (rand.nextFloat() < 0.4f) {
                diveMode = true;
                diveTimer = 0;
            }
        }

        x += dx;
        y += dy;

        if (x < 0) {
            x = 0;
            dx = Math.abs(dx);
        } else if (x > Global.BOARD_WIDTH - width) {
            x = Global.BOARD_WIDTH - width;
            dx = -Math.abs(dx);
        }

        int minY = entranceY;
        int maxY = diveMode ? Global.BOARD_HEIGHT - height : entranceY + 60;

        if (y >= maxY) {
            y = maxY;
            dy = -Math.abs(dy);
        } else if (y <= minY) {
            y = minY;
            dy = Math.abs(dy);
        }

        // When dive ends, start rising smoothly
        if (diveMode && diveTimer > diveDuration) {
            diveMode = false;
            risingAfterDive = true;

            // Stop vertical velocity, we handle vertical movement manually now
            dy = 0;
        }
    }

    // Animate frames as before...
    animationCounter++;
    if (animationCounter >= animationSpeed) {
        animationCounter = 0;
        currentFrame = (currentFrame + 1) % frames.length;

        if (frames[currentFrame] != null) {
            image = frames[currentFrame];
        } else {
            image = frames[0]; // fallback
        }
    }
}


    public void takeDamage(int damage) {
        if (!visible || dying) return;
        health -= damage;
        System.out.println("Boss health: " + health);
        if (health <= 0) {
            health = 0;
            die();
        }
    }

    public void die() {
        visible = false;
        dying = true;
        // Add explosion animation or sound here if desired
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isDying() {
        return dying;
    }

    public int getHealth() {
        return health;
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
