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
import javax.swing.JOptionPane;

public class Scene2 extends AbstractGameScene {

    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0},
        {0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0},
        {0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1}
    };

    private Random rand = new Random();
    private BossEnemy boss;
    private long bossSpawnTime = -1;
    private boolean bossSpawned = false;
    private AudioPlayer audioPlayer;

    private int scoreP1;
    private int scoreP2;

    private boolean gameOver = false;
    private boolean gameOverShown = false;

    // --- Crater class and crater list ---

    private static class Crater {
        int x, y, width, height;
        double speed; // pixels per frame

        Crater(int x, int y, int width, int height, double speed) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = speed;
        }

        void update() {
            y += speed;
            if (y > BOARD_HEIGHT) {
                y = -height;
            }
        }

        void draw(Graphics2D g2d) {
            Color craterColor = new Color(150, 80, 50, 150); // translucent reddish-brown
            g2d.setColor(craterColor);
            g2d.fillOval(x, y, width, height);
        }
    }

    private final List<Crater> craters = new ArrayList<>();

    // Constructor
    public Scene2(Game game, Player player, int scoreP1, int scoreP2) {
        super(game);
        this.player = player;
        this.scoreP1 = scoreP1;
        this.scoreP2 = scoreP2;
        setBackground(new Color(0xC86C41));
    }

   // Initialize craters
private void initCraters() {
    craters.clear();
    craters.add(new Crater(100, 100, 50, 25, 1.2));
    craters.add(new Crater(400, 300, 70, 35, 1.0));
    craters.add(new Crater(600, 500, 40, 20, 1.4));
    craters.add(new Crater(300, 700, 60, 30, 1.1));
    craters.add(new Crater(200, 150, 55, 27, 1.3));
    craters.add(new Crater(500, 450, 45, 22, 1.0));
    craters.add(new Crater(700, 600, 65, 32, 1.2));
    craters.add(new Crater(20, 200, 50, 25, 1.0)); 
    craters.add(new Crater(60, 350, 40, 20, 1.1)); 
    craters.add(new Crater(30, 480, 45, 22, 1.2));   
    craters.add(new Crater(80, 600, 55, 28, 1.3));  
    craters.add(new Crater(100, 700, 50, 24, 1.0));  
}

    @Override
    public void start() {
        super.start();

        player.setMaxSpeed(5, 4);
        player2.setMaxSpeed(5, 4);

        bossSpawnTime = System.currentTimeMillis() + 6000;

        initCraters();

        try {
            audioPlayer = new AudioPlayer("src/audio/BossSong.wav");
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Scene2 music error: " + e.getMessage());
        }

        gameOver = false;
        gameOverShown = false;
    }

    public void stop() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawAsteroidField(g);

        // Draw craters independently
        Graphics2D g2d = (Graphics2D) g;
        for (Crater crater : craters) {
            crater.draw(g2d);
        }

        drawPlayers(g);
        drawShots(g);

        if (bossSpawned && boss != null && boss.isVisible()) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);

            // Boss health bar
            int barX = 150;
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

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            g.drawString("Boss HP: " + boss.getHealth(), barX + 160, barY + 15);

            g.setColor(Color.green);
            g.drawString("FRAME: " + frame, 10, 30);

            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
            g.drawString("Player 1 Score: " + scoreP1, 10, 60);
            g.drawString("Player 2 Score: " + scoreP2, BOARD_WIDTH - 220, 60);
        }

        if (gameOver && !gameOverShown) {
            gameOverShown = true;
            javax.swing.SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "You got eaten!");
                this.stop();        // Stop current game timer
                game.loadScene2();  // Restart Scene2
            });
        }
    }

    @Override
    protected void customUpdate() {
        if (gameOver) return;

        // Update craters independently
        for (Crater crater : craters) {
            crater.update();
        }

        long now = System.currentTimeMillis();

        if (!bossSpawned && now >= bossSpawnTime) {
            int startX = (BOARD_WIDTH / 2) - 64;
            boss = new BossEnemy(startX);
            bossSpawned = true;
        }

        if (bossSpawned && boss != null && boss.isVisible()) {
            boss.move();

            // Check boss collisions with players
            Rectangle bossBounds = new Rectangle(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
            Rectangle player1Bounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            Rectangle player2Bounds = new Rectangle(player2.getX(), player2.getY(), player2.getWidth(), player2.getHeight());

            if (bossBounds.intersects(player1Bounds) || bossBounds.intersects(player2Bounds)) {
                gameOver = true;
                return;
            }

            // Check boss collisions with shots
            List<Shot> toRemove = new ArrayList<>();
            for (Shot shot : shots) {
                if (!shot.isVisible()) continue;

                Rectangle shotBounds = new Rectangle(shot.getX(), shot.getY(), 5, 10);

                if (bossBounds.intersects(shotBounds)) {
                    boss.takeDamage(25);
                    shot.die();
                    toRemove.add(shot);

                    // Update score based on which player fired the shot
                    if (shot.getOwner() == 1) {
                        scoreP1 += 10;
                    } else if (shot.getOwner() == 2) {
                        scoreP2 += 10;
                    }
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
