/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author bhudy
 */
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.Graphics;

public class GamePanel extends JPanel implements Runnable{
    
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    
    final int tileSize = originalTileSize * scale; //48x46 tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; //768 pixels
    final int screenHeight = tileSize * maxScreenRow; //576 pixels
    
    Thread gameThread;
    
    int[][] map = {
    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
    {1,2,1,1,1,2,1,1,1,1,2,1,1,1,2,1},
    {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
    {1,2,1,1,2,1,1,2,2,1,1,2,1,1,2,1},
    {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
    {1,2,1,1,1,2,1,1,1,1,2,1,1,1,2,1},
    {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
    {1,2,1,1,2,1,1,2,2,1,1,2,1,1,2,1},
    {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
};
    
    
    public GamePanel(){
     
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int row = 0; row < map.length; row++) {

            for(int col = 0; col < map[row].length; col++) {

                int x = col * tileSize;
                int y = row * tileSize;

                // Mur
                if(map[row][col] == 1) {

                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, tileSize, tileSize);
                }

                // Pastille
                if(map[row][col] == 2) {

                    g.setColor(Color.WHITE);

                    int size = 8;

                    g.fillOval(
                        x + tileSize/2 - size/2,
                        y + tileSize/2 - size/2,
                        size,
                        size
                    );
                }
            }
        }
    }
    
    public void startGameThread(){
    
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    
    
    @Override
    public void run() {

       while(gameThread != null) {
           
           //System.out.println("Test");

            repaint();

            try {
                Thread.sleep(16);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}