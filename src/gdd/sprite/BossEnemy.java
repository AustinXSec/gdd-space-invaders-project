package gdd.sprite;

import gdd.Global;
import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;

public class BossEnemy {

    private float x, y;
    private int width, height;
    private boolean visible;
    private Image image;

    private float dx, dy;
    private final Random rand = new Random();

    private int directionTimer = 0;
    private final int directionChangeInterval = 180;

    private boolean entering = true;
    private final float entranceSpeed = 1.0f;
    private final int entranceY = 100;

    // Dive control
    private boolean diveMode = false;
    private final int diveDuration = 240;
    private int diveTimer = 0;

    // Health
    private int health = 10000;
    private boolean dying = false;

    public BossEnemy(int startX) {
        this.x = startX;
        this.y = -100;
        this.visible = true;

        loadImage();
        randomizeDirection();
    }

    private void loadImage() {
        ImageIcon ii = new ImageIcon("src/images/Boss.png");
        image = ii.getImage();
        width = image.getWidth(null);
        height = image.getHeight(null);
    }

    private void randomizeDirection() {
        float angle = (float) (Math.PI / 4 + rand.nextFloat() * (Math.PI / 2));
        float speed = 1.4f + rand.nextFloat() * 1.4f;

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
        } else {
            directionTimer++;
            diveTimer++;

            if (directionTimer >= directionChangeInterval) {
                randomizeDirection();
                directionTimer = 0;

                if (rand.nextFloat() < 0.25f) {
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

            if (diveMode && diveTimer > diveDuration) {
                diveMode = false;
                if (y > entranceY + 80) dy = -Math.abs(dy);
            }
        }
    }

    public void takeDamage(int damage) {
        if (!visible || dying) return;
        health -= damage;
        if (health <= 0) {
            health = 0;
            die();
        }
    }

    public void die() {
        visible = false;
        dying = true;
        // Optionally add explosion or sound
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
