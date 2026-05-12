package game;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameEngine game = new GameEngine();
            GameGUI gui = new GameGUI(game);
            gui.setVisible(true);
        });
    }
}
