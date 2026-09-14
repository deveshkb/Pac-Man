package org.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MyPanel extends JPanel implements ActionListener {

    final int PANNEL_WIDTH = 1500;
    final int PANNEL_HEIGTH = 1000;
    Timer timer;
    Pacman pacman;

    MyPanel(){
        this.setPreferredSize(new Dimension(PANNEL_WIDTH, PANNEL_HEIGTH));
        this.setBackground(Color.black);
        pacman = new Pacman();
        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        pacman.draw(g2D);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pacman.update(PANNEL_WIDTH, PANNEL_HEIGTH);
        repaint();
    }
}