package main;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        // Création de la fenêtre du jeu
        JFrame window = new JFrame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Pac-Man");

        // Création et ajout du panneau de jeu
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // Adapte la fenêtre à la taille du GamePanel
        window.pack();

        // Centre la fenêtre à l'écran
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Démarre la boucle du jeu
        gamePanel.startGameThread();
    }
}