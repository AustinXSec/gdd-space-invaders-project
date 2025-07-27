

package gdd.powerup;

import gdd.sprite.Player;
import gdd.sprite.Player2;
import gdd.sprite.Sprite;


abstract public class PowerUp extends Sprite {
    PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
    }

    abstract public void upgrade(Player player);
    abstract public void upgrade(Player2 player2);
    
}
