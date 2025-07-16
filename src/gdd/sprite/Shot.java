package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 27; 
    private static final int V_SPACE = 4;

    private int owner; // 1 = Player 1, 2 = Player 2

    public Shot() {
    }

    public Shot(int x, int y, int owner) {
        this.owner = owner;
        initShot(x, y);
    }

    private void initShot(int x, int y) {
        var ii = new ImageIcon(IMG_SHOT);
        var scaledImage = ii.getImage().getScaledInstance(
            ii.getIconWidth() * SCALE_FACTOR,
            ii.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH
        );
        setImage(scaledImage);
        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }

    public int getOwner() {
        return owner;
    }
}
