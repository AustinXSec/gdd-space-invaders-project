package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {

    private int frame = 0;
    private Image image;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private Game game;

    public TitleScene(Game game) {
        this.game = game;
        // initBoard();
        // initTitle();
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();  // Ensure focus for key input
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        initTitle();
        initAudio();
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }

            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    
    private void initTitle() {
        var ii = new ImageIcon(IMG_TITLE);
        image = ii.getImage();
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/title.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error with playing sound.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, d.width, d.height);

    g.drawImage(image, 0, -15, d.width, d.height, this);

    if (frame % 60 < 30) {
        g.setColor(Color.green);
    } else {
        g.setColor(Color.white);
    }

    g.setFont(g.getFont().deriveFont(32f));
    String text = "Press SPACE to Start";
    int stringWidth = g.getFontMetrics().stringWidth(text);
    int x = (d.width - stringWidth) / 2;
    g.drawString(text, x, 420);

    
    
    g.setColor(Color.green);
    g.setFont(g.getFont().deriveFont(14f));
    String teamTitle = "Team 2 Members:";
    g.drawString(teamTitle, 10, 500);

   
    String[] members = {"Austin Tucker","Lizandro Campos"}; 
    int y = 520;
    for (String member : members) {
        g.drawString(member, 20, y);
        y += 20;
    }

    Toolkit.getDefaultToolkit().sync();
}


    private void update() {
        frame++;
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                // Load the gameplay scene when space is pressed
                game.loadScene2();
            }
        }
    }
}
