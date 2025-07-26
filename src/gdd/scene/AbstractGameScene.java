package gdd.scene;

import gdd.Game;
import static gdd.Global.*;
import gdd.SoundEffects;
import gdd.powerup.PowerUp;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Player2;
import gdd.sprite.Shot;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public abstract class AbstractGameScene extends JPanel {

    protected Game game;
    protected Timer timer;
    protected int frame;

    protected Player player;
    protected Player2 player2;
    protected List<Shot> shots;
    protected List<PowerUp> powerups;
    protected List<Explosion> explosions;

    public AbstractGameScene(Game game) {
        this.game = game;
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        addKeyListener(new TAdapter());
    }

    public void start() {
        frame = 0;
        shots = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        player = new Player();
        player2 = new Player2();
        requestFocusInWindow();

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();
    }

    public void stop() {
        if (timer != null) timer.stop();
    }

    protected void drawPlayers(Graphics g) {
        if (player.isVisible())
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        if (player2.isVisible())
            g.drawImage(player2.getImage(), player2.getX(), player2.getY(), this);

        if (player.isDying()) player.die();
        if (player2.isDying()) player2.die();
    }

    protected void drawShots(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    protected void updatePlayers() {
        player.act();
        player2.act();
    }

    protected void updateShots() {
        List<Shot> toRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (!shot.isVisible()) continue;

            int newX = shot.getX() + shot.getDx();
            int newY = shot.getY() - 20;
            if (newY < 0) {
                shot.die();
                toRemove.add(shot);
            } else {
                shot.setX(newX);
                shot.setY(newY);
            }
        }
        shots.removeAll(toRemove);
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
            player2.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            player2.keyPressed(e);

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (shots.size() < 10) {
                    int centerX = player.getX() + player.getWidth() / 2 - 6;
                    int y = player.getY();
                    if (player.isMultiShotEnabled()) {
                        shots.add(new Shot(centerX, y, 1));              // Center
                        shots.add(new Shot(centerX - 15, y, 1, -1));     // Left
                        shots.add(new Shot(centerX + 15, y, 1, 1));      // Right
                    } else {
                        shots.add(new Shot(centerX, y, 1));
                    }
                    SoundEffects.playLaser();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_F) {
                if (shots.size() < 10) {
                    int centerX = player2.getX() + player2.getWidth() / 2 - 6;
                    int y = player2.getY();
                    if (player2.isMultiShotEnabled()) {
                        shots.add(new Shot(centerX, y, 2));              // Center
                        shots.add(new Shot(centerX - 15, y, 2, -1));     // Left
                        shots.add(new Shot(centerX + 15, y, 2, 1));      // Right
                    } else {
                        shots.add(new Shot(centerX, y, 2));
                    }
                    SoundEffects.playLaser();
                }
            }
        }
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame++;
            updatePlayers();
            updateShots();
            customUpdate();
            repaint();
        }
    }

    protected abstract void customUpdate(); // Let subclasses define their own update logic
}
