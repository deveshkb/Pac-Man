package main;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pacman {

    public static final int IMAGE_SIZE = 40;

    // Marge entre le sprite (40 px) et la case (48 px) : 4 px de chaque côté.
    private static final int OFFSET = 4;

    // Probabilité (en %) de tourner à une intersection.
    private static final int TURN_CHANCE = 40;

    // Si le fantôme est à moins de ce nombre de cases (par le chemin),
    // Pac-Man se met à fuir.
    private static final int FLEE_DISTANCE = 6;
    
    private GamePanel gamePanel;

    Image image;

    int x;
    int y;

    int speed = 3;

    // 0 = droite, 1 = gauche, 2 = bas, 3 = haut
    int direction = 0;

    Random random = new Random();

    public Pacman(GamePanel gamePanel) {

        this.gamePanel = gamePanel;

        image = new ImageIcon(
            getClass().getResource("/pacman.png")
        ).getImage();

        x = 48 + OFFSET;
        y = 48 + OFFSET;
    }

    public void update(int[][] map, int tileSize, ghost ghost) {

        // On ne décide qu'au centre d'une case : entre deux cases,
        // Pac-Man continue tout droit, sans hésiter.
        if (isAlignedOnTile(tileSize)) {

            if (!flee(map, tileSize, ghost)) {
                chooseDirection(map, tileSize);
            }
        }

        move();
        eatPellet(map, tileSize);
    }

    // Vrai quand le sprite est exactement posé sur une case.
    private boolean isAlignedOnTile(int tileSize) {

        return (x - OFFSET) % tileSize == 0 &&
               (y - OFFSET) % tileSize == 0;
    }

    // Fuite : si le fantôme est proche, on prend la direction qui nous
    // éloigne le plus de lui. Renvoie false si Pac-Man n'a pas à fuir.
    private boolean flee(int[][] map, int tileSize, ghost ghost) {

        int col = (x - OFFSET) / tileSize;
        int row = (y - OFFSET) / tileSize;

        int ghostCol = (ghost.p + ghost.IMAGE_SIZE / 2) / tileSize;
        int ghostRow = (ghost.o + ghost.IMAGE_SIZE / 2) / tileSize;

        int[][] dist = distancesFrom(ghostCol, ghostRow, map);

        if (dist[row][col] > FLEE_DISTANCE) {
            return false;
        }

        // On cherche la case voisine la plus éloignée du fantôme
        // (le demi-tour est autorisé quand on fuit).
        List<Integer> best = new ArrayList<>();
        int bestDistance = -1;

        for (int d = 0; d < 4; d++) {

            if (!isFree(d, col, row, map)) {
                continue;
            }

            int nextCol = col + (d == 0 ? 1 : d == 1 ? -1 : 0);
            int nextRow = row + (d == 2 ? 1 : d == 3 ? -1 : 0);

            int distance = dist[nextRow][nextCol];

            if (distance > bestDistance) {
                bestDistance = distance;
                best.clear();
                best.add(d);
            } else if (distance == bestDistance) {
                best.add(d);
            }
        }

        if (best.isEmpty()) {
            return false;
        }

        direction = best.get(random.nextInt(best.size()));
        return true;
    }

    // Distance (en cases, en contournant les murs) depuis une case donnée.
    private int[][] distancesFrom(int startCol, int startRow, int[][] map) {

        int[][] dist = new int[map.length][map[0].length];

        for (int[] line : dist) {
            java.util.Arrays.fill(line, Integer.MAX_VALUE);
        }

        ArrayDeque<int[]> queue = new ArrayDeque<>();

        dist[startRow][startCol] = 0;
        queue.add(new int[] { startCol, startRow });

        int[] dc = { 1, -1, 0, 0 };
        int[] dr = { 0, 0, 1, -1 };

        while (!queue.isEmpty()) {

            int[] cell = queue.poll();

            for (int d = 0; d < 4; d++) {

                int c = cell[0] + dc[d];
                int r = cell[1] + dr[d];

                if (r < 0 || r >= map.length ||
                    c < 0 || c >= map[0].length ||
                    map[r][c] == 1 ||
                    dist[r][c] != Integer.MAX_VALUE) {
                    continue;
                }

                dist[r][c] = dist[cell[1]][cell[0]] + 1;
                queue.add(new int[] { c, r });
            }
        }

        return dist;
    }

    private void chooseDirection(int[][] map, int tileSize) {

        int col = (x - OFFSET) / tileSize;
        int row = (y - OFFSET) / tileSize;

        boolean canGoStraight = isFree(direction, col, row, map);

        // Directions de côté (gauche / droite par rapport à la marche).
        List<Integer> sides = new ArrayList<>();

        for (int d = 0; d < 4; d++) {

            if (d != direction &&
                d != oppositeDirection(direction) &&
                isFree(d, col, row, map)) {

                sides.add(d);
            }
        }

        if (canGoStraight) {

            // Chemin libre : il peut continuer, ou tourner s'il y a
            // une intersection (jamais de demi-tour).
            if (!sides.isEmpty() &&
                random.nextInt(100) < TURN_CHANCE) {

                direction = sides.get(random.nextInt(sides.size()));
            }

        } else if (!sides.isEmpty()) {

            // Mur devant : il doit tourner.
            direction = sides.get(random.nextInt(sides.size()));

        } else if (isFree(oppositeDirection(direction), col, row, map)) {

            // Cul-de-sac : demi-tour obligatoire.
            direction = oppositeDirection(direction);
        }
    }

    // La case voisine dans cette direction est-elle libre ?
    private boolean isFree(int d, int col, int row, int[][] map) {

        if (d == 0) col++;
        if (d == 1) col--;
        if (d == 2) row++;
        if (d == 3) row--;

        if (row < 0 || row >= map.length ||
            col < 0 || col >= map[0].length) {
            return false;
        }

        return map[row][col] != 1;
    }

    private int oppositeDirection(int direction) {

        switch (direction) {

            case 0:
                return 1;

            case 1:
                return 0;

            case 2:
                return 3;

            default:
                return 2;
        }
    }

    private void move() {

        switch (direction) {

            case 0:
                x += speed;
                break;

            case 1:
                x -= speed;
                break;

            case 2:
                y += speed;
                break;

            case 3:
                y -= speed;
                break;
        }
    }

    private void eatPellet(int[][] map, int tileSize) {

        int col = (x + IMAGE_SIZE / 2) / tileSize;
        int row = (y + IMAGE_SIZE / 2) / tileSize;

        if (row < 0 || row >= map.length ||
            col < 0 || col >= map[0].length) {
            return;
        }

        if (map[row][col] == 2) {
            map[row][col] = 0;
            gamePanel.incrementScore();
        }
    }
    
    public void draw(Graphics2D g2D) {

        g2D.drawImage(
            image,
            x,
            y,
            IMAGE_SIZE,
            IMAGE_SIZE,
            null
        );
    }
}
