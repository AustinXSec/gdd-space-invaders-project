package gdd.powerup;

import static gdd.Global.IMG_POWERUP_MULTISHOT;
import gdd.sprite.Player;
import gdd.sprite.Player2;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_MULTISHOT);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth(),
                ii.getIconHeight(), java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    
    public void act() {
        this.y += 2;
    }

    
    public void upgrade(Player player) {
        player.setMultiShotEnabled(true);
        this.die();
    }

    
    public void upgrade(Player2 player2) {
        player2.setMultiShotEnabled(true);
        this.die();
    }
}


