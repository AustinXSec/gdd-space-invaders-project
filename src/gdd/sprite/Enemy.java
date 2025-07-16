package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Enemy extends Sprite {

    private Bomb bomb;
    private ImageIcon[] frames;
    private int currentFrame = 0;
    private int frameCounter = 0;
    private final int FRAME_DELAY = 10; // Controls animation speed

    public Enemy(int x, int y) {
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {
        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        frames = new ImageIcon[2];
        frames[0] = new ImageIcon("src/images/Alien_1.png");
        frames[1] = new ImageIcon("src/images/Alien_2.png");

        setImage(getScaledImage(frames[0]));
    }

    private java.awt.Image getScaledImage(ImageIcon icon) {
        return icon.getImage().getScaledInstance(
            icon.getIconWidth() * SCALE_FACTOR,
            icon.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH
        );
    }

    public void act(int direction) {
        this.x += direction;
    }

    public void animate() {
        frameCounter++;
        if (frameCounter >= FRAME_DELAY) {
            frameCounter = 0;
            currentFrame = (currentFrame + 1) % frames.length;
            setImage(getScaledImage(frames[currentFrame]));
        }
    }

    public Bomb getBomb() {
        return bomb;
    }

    public class Bomb extends Sprite {
        private boolean destroyed;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
