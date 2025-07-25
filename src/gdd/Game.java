package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import gdd.sprite.Player;
import javax.swing.JFrame;

public class Game extends JFrame {

    TitleScene titleScene;
    Scene1 scene1;
    Scene2 scene2;

    public Game() {
        titleScene = new TitleScene(this);
        scene1 = new Scene1(this);

        initUI();
        loadTitle();  // Start with title screen
    }

    private void initUI() {
        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void loadTitle() {
        if (scene1 != null) scene1.stop();
        if (scene2 != null) scene2.stop();
        getContentPane().removeAll();
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        if (titleScene != null) titleScene.stop();
        getContentPane().removeAll();
        add(scene1);
        scene1.start();
        revalidate();
        repaint();
    }

    public void loadScene2() {
        // Stop Scene1 music & timer
        if (scene1 != null) scene1.stop();

        // Get player and start Scene2
        Player currentPlayer = scene1.getPlayer();
        scene2 = new Scene2(this, currentPlayer);

        getContentPane().removeAll();
        add(scene2);
        scene2.start();
        revalidate();
        repaint();
    }
}
