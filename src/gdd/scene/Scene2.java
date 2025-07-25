package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.sprite.BossEnemy;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scene2 extends AbstractGameScene {

    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private Random rand = new Random();
    private BossEnemy boss;
    private long bossSpawnTime = -1;
    private boolean bossSpawned = false;
    private AudioPlayer audioPlayer;

    public Scene2(Game game, Player player) {
        super(game);
        this.player = player;
        setBackground(new Color(0xB83F2C)); // Deep red background
    }

    @Override
    public void start() {
        super.start();
        bossSpawnTime = System.currentTimeMillis() + 5000; // Spawn 5 seconds later

        try {
            audioPlayer = new AudioPlayer("src/audio/BossSong.wav");
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Scene2 music error: " + e.getMessage());
        }
    }

    public void stop() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAsteroidField(g);
        drawPlayers(g);
        drawShots(g);

        if (bossSpawned && boss != null && boss.isVisible()) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);

            // Boss health bar
            int barX = 100;
            int barY = 20;
            int barWidth = 400;
            int barHeight = 20;
            int currentWidth = (int) ((boss.getHealth() / 10000.0f) * barWidth);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            g.setColor(Color.RED);
            g.fillRect(barX, barY, currentWidth, barHeight);

            g.setColor(Color.WHITE);
            g.drawRect(barX, barY, barWidth, barHeight);

            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Boss HP: " + boss.getHealth(), barX + 160, barY + 15);
        }

        
    }

    @Override
    protected void customUpdate() {
        long now = System.currentTimeMillis();

        if (!bossSpawned && now >= bossSpawnTime) {
            int startX = (BOARD_WIDTH / 2) - 64;
            boss = new BossEnemy(startX);
            bossSpawned = true;
        }

        if (bossSpawned && boss != null && boss.isVisible()) {
            boss.move();

            List<Shot> toRemove = new ArrayList<>();
            for (Shot shot : shots) {
                if (!shot.isVisible()) continue;

                Rectangle bossBounds = new Rectangle(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
                Rectangle shotBounds = new Rectangle(shot.getX(), shot.getY(), 5, 10);

                if (bossBounds.intersects(shotBounds)) {
                    boss.takeDamage(40);
                    shot.die();
                    toRemove.add(shot);
                }
            }
            shots.removeAll(toRemove);
        }
    }

    private void drawAsteroidField(Graphics g) {
        int scrollOffset = (frame) % BLOCKHEIGHT;
        int baseRow = (frame) / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2;

        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            int mapRow = (baseRow + screenRow) % MAP.length;
            int y = BOARD_HEIGHT - ((screenRow * BLOCKHEIGHT) - scrollOffset);

            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) continue;

            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    int x = col * BLOCKWIDTH;
                    drawAsteroidCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }
    }

    private void drawAsteroidCluster(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(100 + rand.nextInt(50), 100 + rand.nextInt(50), 100 + rand.nextInt(50)));

        int cx = x + width / 2;
        int cy = y + height / 2;

        g2d.fillOval(cx - 10, cy - 10, 20, 20);
        g2d.fillOval(cx - 14, cy, 18, 18);
        g2d.fillOval(cx + 2, cy - 6, 16, 16);
        g2d.fillOval(cx - 5, cy + 4, 20, 20);
        g2d.fillOval(cx - 8, cy - 2, 14, 14);
    }
}
