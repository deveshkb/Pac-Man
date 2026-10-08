package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // Dimensions de l'écran et des cases
    final int originalTileSize = 16;  //taille initial de la case : 16 px (largeur) * 16 px (hauteur)
    final int scale = 3;
    final int tileSize = originalTileSize * scale; // 48x48  (scale : facteur d'agrandissement)

    final int maxScreenCol = 16;  //nb de colonnes de la fenetre : 16 cases en largeur
    final int maxScreenRow = 12;  //nb de lignes de la fenetre : 16 cases en hauteur

    final int screenWidth = tileSize * maxScreenCol;  //largeur total de la fenêtre (largeur de 48 px 16 fois) = 768 px
    final int screenHeight = tileSize * maxScreenRow;  //hauteur total de la fenêtre (hauteur de 48 px 16 fois) = 576 px

    Thread gameThread;  //GameThread : une variable de type Thread, qui pourra contenir un thread : le moteur de jeu

    // Carte du niveau :
    // 1 = mur, 2 = pastille, 3 = fruit / super-pastille
    int[][] map = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,3,2,2,2,2,2,2,2,2,2,2,2,2,3,1},
        {1,2,1,1,1,2,1,1,1,1,3,1,1,1,2,1},
        {1,2,2,3,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,1,2,2,1,1,3,1,1,2,1},
        {1,3,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,2,2,2,3,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,1,2,2,1,1,3,1,1,2,1},
        {1,2,2,2,2,2,2,2,3,2,2,2,2,2,2,1},
        {1,2,1,1,1,3,1,1,1,1,2,1,1,1,2,1},
        {1,3,2,2,2,2,2,2,2,2,2,2,2,2,3,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // Configuration/création du panneau de jeu
    public GamePanel() {

        this.setPreferredSize(
            new Dimension(screenWidth, screenHeight)  //defini ou configure la dimension de la fenetre
        );

        this.setBackground(Color.BLACK);  //defini la couleur de fond de la fenetre
        this.setDoubleBuffered(true);  //rend l'affichage du jeu plus fluide et évite les clignotements
    }

    // Dessine les éléments de la carte
    @Override
    protected void paintComponent(Graphics g) {  //cette méthode dessine tout ce qu'on voit dans le GamePanel. g est, par analogie, le crayon qui dessine 

        super.paintComponent(g);  //efface le dessin precedent. Ou encore exécute le paintComponent() du JPanel, ce qui efface l'ancien dessin
                                  

        // Parcourt chaque case de la carte
        for (int row = 0; row < map.length; row++) {  // Parcourt toutes les lignes de la carte

            for (int col = 0; col < map[row].length; col++) {  //for (int col = 0; col < map[row].length; col++)                
                                                                                                                                
                int x = col * tileSize;  //x represente l'axe des abscisses x, plus x augmente plus en avance vers la droite : [0*48][1*48][2*48][3*48] -------> x  : À combien de pixels du bord gauche faut-il dessiner la case ? ou distance depuis le bord gauche
                int y = row * tileSize;  // y represente l'axe des ordonnées y, plus y augmente plus en descend vers le bas  :  À combien de pixels du bord supérieur faut-il dessiner la case ? ou distance depuis le bord haut
                                         // on a choisi row avec y car row (ligne) descend tandis que col(colonne) va vers la droite 
                // Dessine les murs
                if (map[row][col] == 1) {

                    g.setColor(Color.BLUE); // (le crayon g) dessine/colore en bleu. this represente le panneau (gamepanel) ou "la feuille de papier"
                    g.fillRect(x, y, tileSize, tileSize); // g (le crayon) dessine un rectangle ou carré rempli (fillRect)à la position x, y et de taille tilesize (largeur), tilesize (hauteur)
                }

                // Dessine les pastilles
                if (map[row][col] == 2) {

                    g.setColor(Color.WHITE);

                    int taillePastille = 8; //taille de la pastille : 8 px

                    // Place la pastille au centre de la case
                    g.fillOval(  // (g (crayon)) dessine un ovale rempli
                        x + tileSize / 2 - taillePastille / 2, // tileSize / 2 : centre de la case
                        y + tileSize / 2 - taillePastille / 2, // taillePastille / 2 : centre du fruit
                        taillePastille,
                        taillePastille
                    );
                }

                // Dessine les fruits / super-pastilles
                if (map[row][col] == 3) {

                    g.setColor(Color.ORANGE);

                    int fruitSize = 18; // 18 px

                    // Place le fruit au centre de la case
                    g.fillOval(
                        x + tileSize / 2 - fruitSize / 2,
                        y + tileSize / 2 - fruitSize / 2,
                        fruitSize,
                        fruitSize
                    );
                }
            }
        }
    }

    // Lance le thread principal du jeu
    public void startGameThread() {
                                        // Un thread est une tâche qui s'exécute toute seule
        gameThread = new Thread(this);  // crée le moteur de jeu, càd le thread qui exécutera la méthode run() au démarrage (gameThread.start() ), grace à : public class GamePanel extends JPanel implements Runnable <--  new Thread(this); : signifie crée un thread qui exécutera le run() de cet objet, càd de "this" qui represente GamePanel actuel
        gameThread.start();  //démarre le moteur de jeu (ou boucle de jeu) et exécute la méthode run()
    }                        //la tache à repeter encore et encore sera : repaint()

    // Boucle principale du jeu
    @Override
    public void run() {  //travail exécuté par le moteur de jeu tant que gameThread != null (càd tant que le jeu tourne)

        while (gameThread != null) {

            // Redessine régulièrement le jeu
            repaint(); //Demande à Java de redessiner le panneau en appellant paintComponent(g)
                       //qd java recoit repaint() il appelle paintComponent(g)
                       
            try {  // essaie d'exécuter ce code
                Thread.sleep(16); // Le thread se met en pause pendant 16 millisecondes ( 1 ms = 1/1000 seconde); 16 ms = 0.016 s
            }    // cette courte pause ralentit la boucle
            catch (InterruptedException e) {  // Si une erreur se produit, récupère-la
                e.printStackTrace();  // Affiche l'erreur dans la console
            }
        }
    }
}