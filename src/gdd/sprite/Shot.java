package gdd.sprite;

import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private final int speedY = 20;
    private final int owner; // 1 = Player 1, 2 = Player 2
    private int dx = 0;      // Direction offset for multi-shot (-1, 0, 1)

    public Shot(int x, int y, int owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;

        ImageIcon ii = new ImageIcon("src/images/shot.png");
        var scaled = ii.getImage().getScaledInstance(10, 30, java.awt.Image.SCALE_SMOOTH);
        setImage(scaled);
    }

    // Overloaded constructor for multi-shot with directional offset
    public Shot(int x, int y, int owner, int dx) {
        this(x, y, owner);
        this.dx = dx;
    }

    public void act() {
        y -= speedY;
        x += dx * 5; // Horizontal movement for angled shots

        if (y < 0) {
            die();
        }
    }

    public int getOwner() {
        return owner;
    }

    public int getDx() {
        return dx;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
