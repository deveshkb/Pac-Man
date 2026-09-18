package org.example;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;

public class ghost
{
    final int IMAGE_SIZE = 90;
    final int chaseRadius = 250;
    Image image;
    int p = 7;
    int o = 600;
    int speed = 4;

    private final Pacman pacman;

    // 0 = droite, 1 = gauche, 2 = bas, 3 = haut
    int direction = 0;
    Random random = new Random();
    int stepsBeforeChange = 60; // change de direction toutes les 60 frames environ
    int stepCounter = 0;

    ghost(Pacman pacman){
        this.pacman = pacman;
        image = new ImageIcon(getClass().getResource("/game.png")).getImage();
    }

    public void update(int panelWidth, int panelHeight){
        int pacmanCenterX = pacman.x + pacman.IMAGE_SIZE / 2;
        int pacmanCenterY = pacman.y + pacman.IMAGE_SIZE / 2;
        int ghostCenterX = p + IMAGE_SIZE / 2;
        int ghostCenterY = o + IMAGE_SIZE / 2;

        double distanceToPacman = Math.hypot(pacmanCenterX - ghostCenterX, pacmanCenterY - ghostCenterY);

        if (distanceToPacman <= chaseRadius) {
            if (Math.abs(pacmanCenterX - ghostCenterX) > Math.abs(pacmanCenterY - ghostCenterY)) {
                if (pacmanCenterX > ghostCenterX) {
                    p += speed;
                    direction = 0;
                } else {
                    p -= speed;
                    direction = 1;
                }
            } else {
                if (pacmanCenterY > ghostCenterY) {
                    o += speed;
                    direction = 2;
                } else {
                    o -= speed;
                    direction = 3;
                }
            }
        } else {
            switch (direction) {
                case 0: p += speed; break; // droite
                case 1: p -= speed; break; // gauche
                case 2: o += speed; break; // bas
                case 3: o -= speed; break; // haut
            }

            stepCounter++;
            if (stepCounter >= stepsBeforeChange) {
                direction = random.nextInt(4);
                stepCounter = 0;
            }
        }

        // Rebond sur les bords : on inverse juste l'axe concerné
        if (p >= panelWidth - IMAGE_SIZE) { p = panelWidth - IMAGE_SIZE; direction = 1; }
        if (p < 0) { p = 0; direction = 0; }
        if (o >= panelHeight - IMAGE_SIZE) { o = panelHeight - IMAGE_SIZE; direction = 3; }
        if (o < 0) { o = 0; direction = 2; }
    }

    public void draw(Graphics2D g2D){
        g2D.drawImage(image, p, o, IMAGE_SIZE, IMAGE_SIZE, null);
    }
}