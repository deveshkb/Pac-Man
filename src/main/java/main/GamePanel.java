/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16; // 16x16 tile
    final int scale = 2;

    final int tileSize = originalTileSize * scale; // 32x32 tile
    final int maxScreenCol = 28;
    final int maxScreenRow = 24;
    final int screenWidth = tileSize * maxScreenCol; // 896 pixels
    final int screenHeight = tileSize * maxScreenRow; // 768 pixels

    final int FPS = 60;

    Thread gameThread;

    Ghost ghost = new Ghost();//on déclare le fantôme ici, comme
    // n'importe quel autre champ de la classe
    // Couleurs
    private final Color ROYAL_BLUE = new Color(65, 105, 225);
    private final Color GHOST_DOOR_WHITE = Color.WHITE;

    // Lignes du labyrinthe en coordonnées de tuiles (x1, y1, x2, y2)
    private final int[][] mazeLines = {
        // Bordure extérieure
        {2,1,26,1}, {2,23,26,23},

        // Bord gauche avec tunnel de sortie
        {2,1,2,11}, {2,13,2,23},
        {0,11,2,11}, {0,13,2,13},

        // Bord droit avec tunnel d'entrée
        {26,1,26,11}, {26,13,26,23},
        {26,11,28,11}, {26,13,28,13},

        // Séparation centrale haute
        {14,1,14,5},

        // Bloc haut gauche
        {4,3,8,3}, {4,5,8,5}, {4,3,4,5}, {8,3,8,5},

        // Barre haut gauche
        {11,3,11,5},

        // Bloc haut droit
        {16,3,20,3}, {16,5,20,5}, {16,3,16,5}, {20,3,20,5},

        // Barre haut droite
        {23,3,23,5},

        // Barres horizontales hautes
        {4,7,12,7}, {16,7,24,7},

        // T couché gauche
        {4,9,4,13}, {4,11,7,11},

        // Maison des fantômes
        {9,9,13,9}, {15,9,19,9},
        {9,9,9,14}, {19,9,19,14}, {9,14,19,14},

        // Petit bloc droit
        {22,9,24,9}, {22,11,24,11}, {22,9,22,11}, {24,9,24,11},

        // L droit
        {21,13,21,15}, {21,15,24,15},

        // Crochet bas gauche
        {4,15,7,15}, {7,15,7,19},

        // Butée bas gauche
        {2,18,4,18},

        // L bas gauche
        {4,21,9,21}, {9,17,9,21},

        // T bas central
        {12,17,16,17}, {14,17,14,21},

        // Barre bas centrale
        {16,19,16,21},

        // Grand bloc bas droit
        {18,17,24,17}, {18,21,24,21}, {18,17,18,21}, {24,17,24,21}
    };

    // Porte blanche de la maison des fantômes
    private final int[] ghostDoor = {13,9,15,9};

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        // TODO: logique du jeu

        ghost.update(mazeLines, tileSize);//remplace le "// TODO" par cet appel.
        // C'est ce qui fait bouger le fantôme à chaque frame
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner le labyrinthe en bleu roi
        g2.setColor(ROYAL_BLUE);
        g2.setStroke(new BasicStroke(4));

        for (int[] line : mazeLines) {
            int x1 = line[0] * tileSize;
            int y1 = line[1] * tileSize;
            int x2 = line[2] * tileSize;
            int y2 = line[3] * tileSize;
            g2.drawLine(x1, y1, x2, y2);
        }

        // Dessiner la porte de la maison des fantômes en blanc
        g2.setColor(GHOST_DOOR_WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(ghostDoor[0] * tileSize, ghostDoor[1] * tileSize,
                    ghostDoor[2] * tileSize, ghostDoor[3] * tileSize);

    ghost.draw(g2);// tout à la fin de paintComponent(),
        // après avoir dessiné le labyrinthe et la porte
        // C'est ce qui fait apparaître le fantôme à l'écran
    }
}