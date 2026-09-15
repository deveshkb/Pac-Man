package org.example;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.Random;

public class ghost
{
    final int IMAGE_SIZE = 90;
    Image image;
    int p = 7;
    int o = 600;
    int speed = 4;

    // 0 = droite, 1 = gauche, 2 = bas, 3 = haut
    int direction = 0;
    Random random = new Random();
    int stepsBeforeChange = 60; // change de direction toutes les 60 frames environ
    int stepCounter = 0;

    ghost(){
        image = new ImageIcon(getClass().getResource("/game.png")).getImage();
    }

    public void update(int panelWidth, int panelHeight){
        // Déplacement selon la direction actuelle
        switch (direction) {
            case 0: p += speed; break; // droite
            case 1: p -= speed; break; // gauche
            case 2: o += speed; break; // bas
            case 3: o -= speed; break; // haut
        }

        // Rebond sur les bords : on inverse juste l'axe concerné
        if (p >= panelWidth - IMAGE_SIZE) { p = panelWidth - IMAGE_SIZE; direction = 1; }
        if (p < 0) { p = 0; direction = 0; }
        if (o >= panelHeight - IMAGE_SIZE) { o = panelHeight - IMAGE_SIZE; direction = 3; }
        if (o < 0) { o = 0; direction = 2; }

        // Changement de direction aléatoire de temps en temps
        stepCounter++;
        if (stepCounter >= stepsBeforeChange) {
            direction = random.nextInt(4);
            stepCounter = 0;
        }
    }

    public void draw(Graphics2D g2D){
        g2D.drawImage(image, p, o, IMAGE_SIZE, IMAGE_SIZE, null);
    }
}