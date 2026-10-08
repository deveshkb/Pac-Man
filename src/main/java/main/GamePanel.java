package main;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private int score = 0;

    public void incrementScore(){
        score++;
        repaint();
    }

    final int originalTileSize = 16;
    final int scale = 3;
    final int tileSize = originalTileSize * scale;

    final int maxScreenCol = 16;
    final int maxScreenRow = 12;

    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    Thread gameThread;

    int[][] map = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,1},
            {1,2,1,1,1,1,1,2,1,1,2,1,1,1,2,1},
            {1,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,2,2,2,2,1,1,1,2,1,1,2,1},
            {1,2,1,1,2,1,1,2,1,1,1,2,2,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,1,1,2,1,2,1,1,1,1,2,1,2,1,1,1},
            {1,2,2,2,1,2,2,1,2,2,2,1,2,2,2,1},
            {1,2,1,1,1,1,2,1,2,1,1,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    int[][] originalMap = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,1},
            {1,2,1,1,1,1,1,2,1,1,2,1,1,1,2,1},
            {1,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,2,2,2,2,1,1,1,2,1,1,2,1},
            {1,2,1,1,2,1,1,2,1,1,1,2,2,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,1,1,2,1,2,1,1,1,1,2,1,2,1,1,1},
            {1,2,2,2,1,2,2,1,2,2,2,1,2,2,2,1},
            {1,2,1,1,1,1,2,1,2,1,1,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    Pacman pacman;
    ghost ghost;

    boolean pacmanCaught = false;

    public GamePanel() {

        setPreferredSize(
                new Dimension(screenWidth, screenHeight)
        );

        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        setFocusable(true);
        addKeyListener(this);

        pacman = new Pacman(this);
        ghost = new ghost();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;

        for (int row = 0; row < map.length; row++) {

            for (int col = 0; col < map[row].length; col++) {

                int x = col * tileSize;
                int y = row * tileSize;

                if (map[row][col] == 1) {

                    g.setColor(Color.BLUE);
                    g.fillRect(
                            x, y,
                            tileSize, tileSize
                    );
                }

                if (map[row][col] == 2) {

                    g.setColor(Color.WHITE);
                    int size = 8;

                    g.fillOval(
                            x + tileSize / 2 - size / 2,
                            y + tileSize / 2 - size / 2,
                            size, size
                    );
                }
            }
        }

        if (!pacmanCaught) {
            pacman.draw(g2D);
        }

        ghost.draw(g2D);

        if(pacmanCaught){
            g2D.setColor(Color.RED);
            g2D.setFont(new Font("Arial", Font.BOLD, 40));
            g2D.drawString("GAME OVER", 250, 300);
        }

        g2D.setColor(Color.WHITE);
        g2D.setFont(new Font("Arial", Font.BOLD, 20));
        g2D.drawString("Score: " + score, 10, 25);
    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
        requestFocusInWindow();
    }

    @Override
    public void run() {

        while (gameThread != null) {

            if (!pacmanCaught) {

                pacman.update(map, tileSize, ghost);

                ghost.update(
                        map,
                        tileSize,
                        pacman
                );

                checkCollision();
            }

            repaint();

            try {

                Thread.sleep(16);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void checkCollision() {

        int pacmanCenterX =
                pacman.x + Pacman.IMAGE_SIZE / 2;

        int pacmanCenterY =
                pacman.y + Pacman.IMAGE_SIZE / 2;

        int ghostCenterX =
                ghost.p + ghost.IMAGE_SIZE / 2;

        int ghostCenterY =
                ghost.o + ghost.IMAGE_SIZE / 2;

        int dx = pacmanCenterX - ghostCenterX;
        int dy = pacmanCenterY - ghostCenterY;

        double distance =
                Math.sqrt(dx * dx + dy * dy);

        if (distance <= 24) {
            pacmanCaught = true;
        }
    }

    public void restartGame() {

        pacmanCaught = false;
        score = 0;

        for (int row = 0; row < originalMap.length; row++) {
            for (int col = 0; col < originalMap[row].length; col++) {
                map[row][col] = originalMap[row][col];
            }
        }

        pacman = new Pacman(this);
        ghost = new ghost();

        requestFocusInWindow();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_R && pacmanCaught) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}