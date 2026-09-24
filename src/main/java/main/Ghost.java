package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class Ghost {

    // Taille du fantôme (24 pour passer dans un couloir d'une tuile de 32px)
    private final int SIZE = 24;

    // Position en pixels (coin haut-gauche)
    private int x = 450;
    private int y = 300;

    private int speed = 4;

    // 0 = droite, 1 = gauche, 2 = bas, 3 = haut
    private int direction = 0;

    private Random random = new Random();

    // Changement de direction forcé environ toutes les secondes (60 FPS)
    private int stepsBeforeChange = 60;
    private int stepCounter = 0;

    // Appelée à chaque frame depuis GamePanel.update()
    public void update(int[][] mazeLines, int tileSize) {
        int nextX = x;
        int nextY = y;

        switch (direction) {
            case 0: nextX += speed; break;
            case 1: nextX -= speed; break;
            case 2: nextY += speed; break; // en bas, Y augmente
            case 3: nextY -= speed; break;
        }

        if (!isColliding(nextX, nextY, mazeLines, tileSize)) {
            x = nextX;
            y = nextY;
        } else {
            // Mur devant : on essaie une autre direction
            direction = pickFreeDirection(mazeLines, tileSize);
            stepCounter = 0;
        }

        stepCounter++;
        if (stepCounter >= stepsBeforeChange) {
            direction = pickFreeDirection(mazeLines, tileSize);
            stepCounter = 0;
        }
    }

    // Tire au hasard une direction libre (10 essais max).
    // Si rien n'est libre (cul-de-sac), on garde la direction actuelle.
    private int pickFreeDirection(int[][] mazeLines, int tileSize) {
        for (int i = 0; i < 10; i++) {
            int candidate = random.nextInt(4);

            int testX = x;
            int testY = y;
            switch (candidate) {
                case 0: testX += speed; break;
                case 1: testX -= speed; break;
                case 2: testY += speed; break;
                case 3: testY -= speed; break;
            }

            if (!isColliding(testX, testY, mazeLines, tileSize)) {
                return candidate;
            }
        }
        return direction;
    }

    // Renvoie true si le fantôme placé en (px, py) touche un mur
    private boolean isColliding(int px, int py, int[][] mazeLines, int tileSize) {
        Rectangle ghostBox = new Rectangle(px, py, SIZE, SIZE);

        for (int[] line : mazeLines) {
            // line = {x1, y1, x2, y2} en tuiles -> conversion en pixels
            int x1 = line[0] * tileSize;
            int y1 = line[1] * tileSize;
            int x2 = line[2] * tileSize;
            int y2 = line[3] * tileSize;

            Rectangle wall;
            if (y1 == y2) {
                // mur horizontal, épaisseur 4px
                wall = new Rectangle(Math.min(x1, x2), y1 - 2, Math.abs(x2 - x1), 4);
            } else {
                // mur vertical, épaisseur 4px
                wall = new Rectangle(x1 - 2, Math.min(y1, y2), 4, Math.abs(y2 - y1));
            }

            if (ghostBox.intersects(wall)) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillOval(x, y, SIZE, SIZE);
    }
}
