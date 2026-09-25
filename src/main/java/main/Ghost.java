package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class Ghost {

    // Taille du fantôme (24 pour passer dans un couloir d'une tuile de 32px)
    private final int SIZE = 24;

    // Position en pixel horizontal et vertical
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

       