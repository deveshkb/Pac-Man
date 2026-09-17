package org.example;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;

public class Pacman {

    final int IMAGE_SIZE = 50;
    Image image;
    int x = 0;
    int y = 0;
    int xVelocity = 3;
    int yVelocity = 3;

    Pacman(){
        image = new ImageIcon(getClass().getResource("/pacman.png")).getImage();
    }

    public void update(int panelWidth, int panelHeight){
        if (x >= panelWidth - IMAGE_SIZE || x < 0) {
            xVelocity = xVelocity * -1;
        }
        x = x + xVelocity;

        if (y >= panelHeight - IMAGE_SIZE || y < 0) {
            yVelocity = yVelocity * -1;
        }
        y = y + yVelocity;
    }

    public void draw(Graphics2D g2D){
        g2D.drawImage(image, x, y, IMAGE_SIZE, IMAGE_SIZE, null);
    }

    public KeyListener getKeyListener() {
        return null;
    }
}