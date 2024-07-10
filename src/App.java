import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int tileSize = 32;
        int rows = 16;
        int cols = 16;
        int boardWidth = tileSize * cols;
        int boardHeight = tileSize * rows;

        JFrame frame = new JFrame("Space Invaders");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);

        SpaceInvaders spaceInvaders = new SpaceInvaders();
        frame.add(spaceInvaders);
        frame.pack();
        spaceInvaders.requestFocus();
        frame.setVisible(true);
    }
}
