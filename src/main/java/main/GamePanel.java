package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

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

    Pacman pacman;
    ghost ghost;

    boolean pacmanCaught = false;

    public GamePanel() {

        setPreferredSize(
            new Dimension(screenWidth, screenHeight)
        );

        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        pacman = new Pacman();
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
        
    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
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
}
