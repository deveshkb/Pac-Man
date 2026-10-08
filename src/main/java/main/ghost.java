package main;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ghost {

    public static final int IMAGE_SIZE = Pacman.IMAGE_SIZE;

    Image image;

    int p;
    int o;

    int speed = 5;

    // 0 = droite, 1 = gauche, 2 = bas, 3 = haut
    int direction = 1;

    Random random = new Random();

    // Constructeur par défaut (compatibilité)
    public ghost() {
        this("game.png", 14 * 48 + 4, 10 * 48 + 4);
    }

    // Constructeur personnalisé
    public ghost(String imageName, int startX, int startY) {
        java.net.URL imgURL = getClass().getResource("/" + imageName);
        if (imgURL != null) {
            this.image = new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Erreur : Image introuvable -> /" + imageName);
        }

        this.p = startX;
        this.o = startY;
    }

    public void update(
        int[][] map,
        int tileSize,
        Pacman pacman
    ) {

        // Comme Pac-Man, le fantôme garde sa direction
        // jusqu'à la fin du couloir.
        if (isAtCenter(tileSize)) {

            chooseDirection(map, tileSize, pacman);
        }

        if (canMove(direction, map, tileSize)) {
            move();
        } else {
            chooseDirection(map, tileSize, pacman);
        }
    }

    private void chooseDirection(
        int[][] map,
        int tileSize,
        Pacman pacman
    ) {

        List<Integer> validDirections =
            getValidDirections(map, tileSize);

        if (validDirections.isEmpty()) {
            return;
        }

        int pacmanCol =
            (pacman.x + Pacman.IMAGE_SIZE / 2) / tileSize;

        int pacmanRow =
            (pacman.y + Pacman.IMAGE_SIZE / 2) / tileSize;

        int currentCol =
            (p + IMAGE_SIZE / 2) / tileSize;

        int currentRow =
            (o + IMAGE_SIZE / 2) / tileSize;

        List<Integer> choices = new ArrayList<>();

        int left = turnLeft(direction);
        int right = turnRight(direction);
        int opposite = oppositeDirection(direction);

        // On ne change de couloir que si le nouveau couloir
        // ne nécessite pas de faire demi-tour.
        if (validDirections.contains(direction)) {
            choices.add(direction);
        }

        if (validDirections.contains(left)) {
            choices.add(left);
        }

        if (validDirections.contains(right)) {
            choices.add(right);
        }

        // Calcule quelle direction rapproche le plus
        // du Pac-Man parmi les directions possibles.
        int bestDirection = direction;
        int bestDistance = Integer.MAX_VALUE;

        for (int candidate : choices) {

            int nextCol = currentCol;
            int nextRow = currentRow;

            if (candidate == 0) nextCol++;
            if (candidate == 1) nextCol--;
            if (candidate == 2) nextRow++;
            if (candidate == 3) nextRow--;

            int distance =
                Math.abs(nextCol - pacmanCol)
                + Math.abs(nextRow - pacmanRow);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestDirection = candidate;
            }
        }

        // Petite part d'aléatoire aux intersections.
        if (choices.size() > 1 &&
            random.nextInt(100) < 15) {

            bestDirection =
                choices.get(
                    random.nextInt(choices.size())
                );
        }

        // Demi-tour seulement si c'est un cul-de-sac.
        if (!validDirections.contains(direction) &&
            choices.isEmpty() &&
            validDirections.contains(opposite)) {

            bestDirection = opposite;
        }

        direction = bestDirection;
    }

    private boolean isAtCenter(int tileSize) {

        int centerX = p + IMAGE_SIZE / 2;
        int centerY = o + IMAGE_SIZE / 2;

        return centerX % tileSize <= speed ||
               centerX % tileSize >= tileSize - speed ||
               centerY % tileSize <= speed ||
               centerY % tileSize >= tileSize - speed;
    }

    private List<Integer> getValidDirections(
        int[][] map,
        int tileSize
    ) {

        List<Integer> directions = new ArrayList<>();

        for (int d = 0; d < 4; d++) {
            if (canMove(d, map, tileSize)) {
                directions.add(d);
            }
        }

        return directions;
    }

    private int turnLeft(int direction) {

        if (direction == 0) return 3;
        if (direction == 1) return 2;
        if (direction == 2) return 0;
        return 1;
    }

    private int turnRight(int direction) {

        if (direction == 0) return 2;
        if (direction == 1) return 3;
        if (direction == 2) return 1;
        return 0;
    }

    private int oppositeDirection(int direction) {

        if (direction == 0) return 1;
        if (direction == 1) return 0;
        if (direction == 2) return 3;
        return 2;
    }

    private void move() {

        switch (direction) {
            case 0: p += speed; break;
            case 1: p -= speed; break;
            case 2: o += speed; break;
            case 3: o -= speed; break;
        }
    }

    public boolean canMove(
        int direction,
        int[][] map,
        int tileSize
    ) {

        int nextX = p;
        int nextY = o;

        switch (direction) {
            case 0: nextX += speed; break;
            case 1: nextX -= speed; break;
            case 2: nextY += speed; break;
            case 3: nextY -= speed; break;
        }

        return isPositionValid(
            nextX, nextY, map, tileSize
        );
    }

    private boolean isPositionValid(
        int nextX,
        int nextY,
        int[][] map,
        int tileSize
    ) {

        int left = nextX;
        int right = nextX + IMAGE_SIZE - 1;
        int top = nextY;
        int bottom = nextY + IMAGE_SIZE - 1;

        int leftCol = left / tileSize;
        int rightCol = right / tileSize;
        int topRow = top / tileSize;
        int bottomRow = bottom / tileSize;

        if (leftCol < 0 || rightCol >= map[0].length ||
            topRow < 0 || bottomRow >= map.length) {
            return false;
        }

        return map[topRow][leftCol] != 1 &&
               map[topRow][rightCol] != 1 &&
               map[bottomRow][leftCol] != 1 &&
               map[bottomRow][rightCol] != 1;
    }

    public void draw(Graphics2D g2D) {

        if (image != null) {
            g2D.drawImage(
                image, p, o,
                IMAGE_SIZE, IMAGE_SIZE, null
            );
        }
    }

    // Getters pour gérer les collisions avec Pac-Man
    public int getX() {
        return p;
    }

    public int getY() {
        return o;
    }
}
