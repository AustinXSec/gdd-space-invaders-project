package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import gdd.sprite.Player2;  // Import Player2 as well
import javax.swing.ImageIcon;

public class SpeedUp extends PowerUp {

    public SpeedUp(int x, int y) {
        super(x, y);
        // Set image
        ImageIcon ii = new ImageIcon(IMG_POWERUP_SPEEDUP);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth(),
                ii.getIconHeight(),
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        // Move down the screen
        this.y += 2;
    }

   public void upgrade(Player player) {
    player.setMaxSpeed(player.getNormalMaxSpeedX(), player.getNormalMaxSpeedY());
    player.hasSpeedUp = true;  
    this.die();
}

public void upgrade(Player2 player2) {
    player2.setMaxSpeed(player2.getNormalMaxSpeedX(), player2.getNormalMaxSpeedY());
    player2.hasSpeedUp = true;  
    this.die();
}
}