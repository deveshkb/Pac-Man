package org.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    final int originalTileSize = 16;
    final int scale = 2;
    final int tileSize = 32;
    final int maxScreenCol = 28;
    final int maxScreenRow = 24;
    final int screenWidth = 896;
    final int screenHeight = 768;
    final int FPS = 60;

    Timer timer;
    Pacman pacman;
    ghost ghosty; // renamed from lowercase "ghost" to follow Java class-naming convention

    private final Color ROYAL_BLUE = new Color(65, 105, 225);
    private final Color GHOST_DOOR_WHITE;
    private final int[][] mazeLines;
    private final int[] ghostDoor;

    public GamePanel() {
        this.GHOST_DOOR_WHITE = Color.WHITE;
        this.mazeLines = new int[][]{{2, 1, 26, 1}, {2, 23, 26, 23}, {2, 1, 2, 11}, {2, 13, 2, 23}, {0, 11, 2, 11}, {0, 13, 2, 13}, {26, 1, 26, 11}, {26, 13, 26, 23}, {26, 11, 28, 11}, {26, 13, 28, 13}, {14, 1, 14, 5}, {4, 3, 8, 3}, {4, 5, 8, 5}, {4, 3, 4, 5}, {8, 3, 8, 5}, {11, 3, 11, 5}, {16, 3, 20, 3}, {16, 5, 20, 5}, {16, 3, 16, 5}, {20, 3, 20, 5}, {23, 3, 23, 5}, {4, 7, 12, 7}, {16, 7, 24, 7}, {4, 9, 4, 13}, {4, 11, 7, 11}, {9, 9, 13, 9}, {15, 9, 19, 9}, {9, 9, 9, 14}, {19, 9, 19, 14}, {9, 14, 19, 14}, {22, 9, 24, 9}, {22, 11, 24, 11}, {22, 9, 22, 11}, {24, 9, 24, 11}, {21, 13, 21, 15}, {21, 15, 24, 15}, {4, 15, 7, 15}, {7, 15, 7, 19}, {2, 18, 4, 18}, {4, 21, 9, 21}, {9, 17, 9, 21}, {12, 17, 16, 17}, {14, 17, 14, 21}, {16, 19, 16, 21}, {18, 17, 24, 17}, {18, 21, 24, 21}, {18, 17, 18, 21}, {24, 17, 24, 21}};
        this.ghostDoor = new int[]{13, 9, 15, 9};
        this.setPreferredSize(new Dimension(896, 768));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        pacman = new Pacman();
        ghosty = new ghost();

        // Wire up keyboard input for Pacman
        this.setFocusable(true);
        this.addKeyListener(pacman.getKeyListener());

        // Single Timer-driven loop replaces the old (dead/unsafe) Thread-based
        // run() loop. Timer callbacks run on the EDT, so Swing repaint calls
        // stay thread-safe. 1000 / FPS now actually uses the FPS field.
        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pacman.update(screenWidth, screenHeight);
        ghosty.update(screenWidth, screenHeight);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // NOTE: pacman.draw() and ghosty.draw() are called BEFORE the maze's
        // color/stroke are set below, so they won't inherit ROYAL_BLUE/4px
        // stroke by accident. Make sure Pacman.draw() and Ghost.draw() each
        // set their own g2.setColor(...) internally (they should not rely on
        // whatever color/stroke was left over from a previous paint call).
        pacman.draw(g2);
        ghosty.draw(g2);

        g2.setColor(this.ROYAL_BLUE);
        g2.setStroke(new BasicStroke(4.0F));
        for (int[] line : this.mazeLines) {
            int x1 = line[0] * 32;
            int y1 = line[1] * 32;
            int x2 = line[2] * 32;
            int y2 = line[3] * 32;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(this.GHOST_DOOR_WHITE);
        g2.setStroke(new BasicStroke(4.0F));
        g2.drawLine(this.ghostDoor[0] * 32, this.ghostDoor[1] * 32, this.ghostDoor[2] * 32, this.ghostDoor[3] * 32);
    }
}

