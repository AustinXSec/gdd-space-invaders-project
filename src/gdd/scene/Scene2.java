package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import gdd.Global;
import static gdd.Global.*;
import gdd.sprite.Alien2;
import gdd.sprite.BossEnemy;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Scene2 extends AbstractGameScene {
 
     
    private int[][] MAP; 

  private int[][] loadMapFromCSV(String filename) {
    List<int[]> rows = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");
            int[] row = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                row[i] = Integer.parseInt(tokens[i].trim());
            }
            rows.add(row);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return rows.toArray(new int[0][]);
}
    private Random rand = new Random();
    private BossEnemy boss;
    private long bossSpawnTime = -1;
    private boolean bossSpawned = false;
    private AudioPlayer audioPlayer;
    private AudioPlayer missionCompleteAudio;

    private int scoreP1;
    private int scoreP2;

    private List<Alien2> alien2s = new ArrayList<>();
    private int alien2SpawnTimer = 0;

    private boolean gameOver = false;
    private boolean missionComplete = false;

    private final List<Crater> craters = new ArrayList<>();

    private static class Crater {
        int x, y, width, height;
        double speed;

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
    // Base crater color
    Color craterColor = new Color(150, 80, 50, 150);
    g2d.setColor(craterColor);
    g2d.fillOval(x, y, width, height);

    // Inner shadow (darker ellipse inside the crater)
    g2d.setColor(new Color(100, 50, 30, 180));
    g2d.fillOval(x + 4, y + 4, width - 8, height - 8);

    // Crater outline
    g2d.setColor(new Color(60, 30, 20));
    g2d.drawOval(x, y, width, height);

    // Add radial lines (angled grooves)
    int centerX = x + width / 2;
    int centerY = y + height / 2;

    g2d.setStroke(new BasicStroke(1));
    g2d.setColor(new Color(80, 40, 30, 180));

    for (int angle = 0; angle < 360; angle += 30) {
        double rad = Math.toRadians(angle);
        int outerX = centerX + (int)((width / 2) * Math.cos(rad));
        int outerY = centerY + (int)((height / 2) * Math.sin(rad));
        int innerX = centerX + (int)((width / 2 - 6) * Math.cos(rad));
        int innerY = centerY + (int)((height / 2 - 6) * Math.sin(rad));
        g2d.drawLine(outerX, outerY, innerX, innerY);
    }

  
    g2d.setColor(new Color(40, 20, 10, 220));
    g2d.fillOval(centerX - 3, centerY - 2, 6, 4);
}
    }

    public Scene2(Game game, Player player, int scoreP1, int scoreP2) {
        super(game);
        this.player = player;
        this.scoreP1 = scoreP1;
        this.scoreP2 = scoreP2;
        this.MAP = loadMapFromCSV(Global.MAP2_CSV);
        setBackground(new Color(0xC86C41));
    }

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
        player.setMultiShotEnabled(true);
        player2.setMultiShotEnabled(true);
        player.setMaxSpeed(5, 4);
        player2.setMaxSpeed(5, 4);

        bossSpawnTime = System.currentTimeMillis() + 6000;

        initCraters();

        try {
            audioPlayer = new AudioPlayer("src/audio/BossSong.wav");
            audioPlayer.play();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Audio error: " + e.getMessage());
        }

        gameOver = false;
        missionComplete = false;
    }

    public void stop() {
        if (audioPlayer != null) {
            try {
                audioPlayer.stop();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error stopping audio: " + e.getMessage());
            }
        }
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawAsteroidField(g);

        Graphics2D g2d = (Graphics2D) g;
        for (Crater crater : craters) crater.draw(g2d);

        drawPlayers(g);
        drawShots(g);

        for (Alien2 alien2 : alien2s) {
            if (alien2.isVisible()) {
                g.drawImage(alien2.getImage(), alien2.getX(), alien2.getY(), this);
            }
        }

        if (bossSpawned && boss != null && boss.isVisible()) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);

            int barX = 150;
            int barY = 20;
            int barWidth = 400;
            int barHeight = 20;
            int currentWidth = (int) ((boss.getHealth() / 30000.0f) * barWidth);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            g.setColor(Color.RED);
            g.fillRect(barX, barY, currentWidth, barHeight);

            g.setColor(Color.WHITE);
            g.drawRect(barX, barY, barWidth, barHeight);

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            g.drawString("Boss HP: " + boss.getHealth(), barX + 160, barY + 15);

            g.setColor(Color.GREEN);
            g.drawString("FRAME: " + frame, 10, 30);

            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
            g.drawString("Player 1 Score: " + scoreP1, 10, 60);
            g.drawString("Player 2 Score: " + scoreP2, BOARD_WIDTH - 220, 60);
        }

        if (gameOver || missionComplete) {
            g.setColor(Color.black);
            g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

            g.setColor(new Color(0, 32, 48));
            g.fillRect(50, BOARD_WIDTH / 2 - 60, BOARD_WIDTH - 100, 110);
            g.setColor(Color.white);
            g.drawRect(50, BOARD_WIDTH / 2 - 60, BOARD_WIDTH - 100, 110);

            Font font = new Font("Helvetica", Font.BOLD, 16);
            FontMetrics fm = this.getFontMetrics(font);
            g.setFont(font);
            g.setColor(Color.white);

            String message = gameOver ? "You got eaten!" : "Mission Complete!";
            g.drawString(message, (BOARD_WIDTH - fm.stringWidth(message)) / 2, BOARD_WIDTH / 2 - 20);

            if (missionComplete) {
                String scoreMsg1 = "Player 1 Final Score: " + scoreP1;
                String scoreMsg2 = "Player 2 Final Score: " + scoreP2;

                g.drawString(scoreMsg1, (BOARD_WIDTH - fm.stringWidth(scoreMsg1)) / 2, BOARD_WIDTH / 2 + 10);
                g.drawString(scoreMsg2, (BOARD_WIDTH - fm.stringWidth(scoreMsg2)) / 2, BOARD_WIDTH / 2 + 35);
            }
        }
    }

    @Override
    protected void customUpdate() {
        if (gameOver || missionComplete) return;

        for (Crater crater : craters) crater.update();

        long now = System.currentTimeMillis();

       
if (boss != null && !boss.isEntering()) {
     alien2SpawnTimer++;
    if (alien2SpawnTimer > 120) {
        int side = rand.nextInt(2);
        
        int alienHeight = 15 * SCALE_FACTOR;
        int maxY = GROUND - alienHeight;
        int minY = 40;
        int y = rand.nextInt(maxY - minY) + minY;

        int x = (side == 0) ? 0 : BOARD_WIDTH - 40;
        int dx = (side == 0) ? 2 : -2;

        alien2s.add(new Alien2(x, y, dx));
        alien2SpawnTimer = 0;
    }
}

        List<Alien2> toRemove = new ArrayList<>();
        for (Alien2 alien : alien2s) {
            alien.act(); 
            if (alien.getX() < -40 || alien.getX() > BOARD_WIDTH + 40) {
                toRemove.add(alien);
            }

            Rectangle alienBounds = new Rectangle(alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());
            Rectangle p1Bounds = player.getHitbox();
            Rectangle p2Bounds = player2.getHitbox();


            if (alienBounds.intersects(p1Bounds) || alienBounds.intersects(p2Bounds)) {
                gameOver = true;
                if (timer != null) timer.stop();
                return;
            }
        }
        alien2s.removeAll(toRemove);

        if (!bossSpawned && now >= bossSpawnTime) {
            int startX = (BOARD_WIDTH / 2) - 64;
            boss = new BossEnemy(startX);
            bossSpawned = true;
        }

        if (bossSpawned && boss != null && boss.isVisible()) {
            boss.move();

            Rectangle bossBounds = new Rectangle(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
            Rectangle p1Bounds = player.getHitbox();
            Rectangle p2Bounds = player2.getHitbox();
            if (bossBounds.intersects(p1Bounds) || bossBounds.intersects(p2Bounds)) {
                gameOver = true;
                if (timer != null) timer.stop();
                return;
            }

            List<Shot> toRemoveShots = new ArrayList<>();
            for (Shot shot : shots) {
                if (!shot.isVisible()) continue;
                Rectangle shotBounds = new Rectangle(shot.getX(), shot.getY(), 5, 10);
                if (bossBounds.intersects(shotBounds)) {
                    boss.takeDamage(25);
                    shot.die();
                    toRemoveShots.add(shot);
                    if (shot.getOwner() == 1) scoreP1 += 10;
                    else if (shot.getOwner() == 2) scoreP2 += 10;
                }
            }
            shots.removeAll(toRemoveShots);

            if (boss.getHealth() <= 0) {
                missionComplete = true;
                if (timer != null) timer.stop();
            }
        }
    }

    private void drawAsteroidField(Graphics g) {
        int scrollOffset = frame % BLOCKHEIGHT;
        int baseRow = frame / BLOCKHEIGHT;
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
