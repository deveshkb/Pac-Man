package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // Dimensions de l'écran et des cases (Mickael)
    final int originalTileSize = 16;
    final int scale = 3;
    final int tileSize = originalTileSize * scale; // 48x48

    final int maxScreenCol = 16;
    final int maxScreenRow = 12;

    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    Thread gameThread;

    // 1 = mur, 2 = pastille, 0 = vide
    int[][] map = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,1,2,1,1,1,1,2,1,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,1,2,2,1,1,2,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,1,2,1,1,1,1,2,1,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,1,2,2,1,1,2,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // Configuration du panneau de jeu
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
    }

    // Pacman mange pastille
    public void update() {
        
        // Trouve la case (colonne et ligne) du labyrinthe où se trouve Pac-Man
        int pacmanCaseX = (pacman.x + 24) / tileSize;
        int pacmanCaseY = (pacman.y + 24) / tileSize;

        // Si la case contient une pastille (2), Pac-Man la mange (elle devient 0)
        if (map[pacmanCaseY][pacmanCaseX] == 2) {
            map[pacmanCaseY][pacmanCaseX] = 0; 
        }
    }

    // Dessine les éléments de la carte (Mickael)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Parcourt chaque case de la carte
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {

                int x = col * tileSize;
                int y = row * tileSize;

                // Dessine les murs
                if (map[row][col] == 1) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, tileSize, tileSize);
                }

                // Dessine les pastilles
                if (map[row][col] == 2) {
                    g.setColor(Color.WHITE);
                    int size = 8;

                    // Place la pastille au centre de la case
                    g.fillOval(
                        x + tileSize / 2 - size / 2,
                        y + tileSize / 2 - size / 2,
                        size,
                        size
                    );
                }
            }
        }
    }

    // Lance le thread principal du jeu
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Boucle principale du jeu
    @Override
    public void run() {
        while (gameThread != null) {

            // Vérifie et met à jour les pastilles mangées par Pac-Man
            update();

            // Redessine régulièrement le jeu
            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
