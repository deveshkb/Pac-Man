package main;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Représente Pac-Man, son déplacement et les décisions qu'il prend dans le
 * labyrinthe.
 *
 * <p>Lorsqu'un fantôme est proche, Pac-Man privilégie la fuite. Sinon, il
 * recherche la pastille accessible la plus proche avec un parcours en largeur
 * (BFS). Une mémoire courte de ses dernières cases départage les chemins de
 * même longueur afin d'éviter les allers-retours inutiles.</p>
 */
public class Pacman {

    public static final int IMAGE_SIZE = 40;

    // Marge entre le sprite (40 px) et la case (48 px) : 4 px de chaque côté.
    private static final int OFFSET = 4;

    // Si le fantôme est à moins de ce nombre de cases (par le chemin),
    // Pac-Man se met à fuir.
    private static final int FLEE_DISTANCE = 6;

    // La mémoire est volontairement courte : elle évite les boucles sans
    // interdire à Pac-Man de revenir plus tard dans un couloir nécessaire.
    private static final int RECENT_TILE_LIMIT = 12;

    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int DOWN = 2;
    private static final int UP = 3;

    // Ces vecteurs associent chaque direction à son déplacement sur la grille.
    // Les centraliser évite que le mouvement et la recherche de chemin ne se
    // contredisent lorsqu'ils interprètent une direction.
    private static final int[] COLUMN_MOVES = { 1, -1, 0, 0 };
    private static final int[] ROW_MOVES = { 0, 0, 1, -1 };

    Image image;

    int x;
    int y;

    int speed = 3;

    int direction = RIGHT;
    int score = 0;

    Random random = new Random();

    private final ArrayDeque<Integer> recentTiles = new ArrayDeque<>();

    /**
     * Charge le sprite de Pac-Man et le place au départ sur une case libre.
     */
    public Pacman() {

        image = new ImageIcon(
            getClass().getResource("/pacman.png")
        ).getImage();

        // Départ dans un couloir libre, aligné sur une case.
        x = 48 + OFFSET;
        y = 48 + OFFSET;
    }

    /**
     * Met à jour la stratégie, le déplacement et la collecte de Pac-Man.
     *
     * <p>La direction ne change qu'au centre d'une case. Décider au milieu
     * d'un couloir pourrait faire traverser un mur, car Pac-Man ne serait pas
     * aligné avec l'entrée du prochain passage.</p>
     *
     * @param map labyrinthe : 1 est un mur et 2 une pastille
     * @param tileSize taille d'une case du labyrinthe en pixels
     * @param ghost fantôme dont Pac-Man doit s'éloigner si nécessaire
     */
    public void update(int[][] map, int tileSize, ghost ghost) {

        if (isAlignedOnTile(tileSize)) {
            int col = (x - OFFSET) / tileSize;
            int row = (y - OFFSET) / tileSize;

            // On mémorise une case seulement lors de son entrée. Mémoriser
            // chaque pixel fausserait la mémoire en faveur de la case courante.
            rememberTile(col, row, map[0].length);

            // La survie est prioritaire sur la collecte : une pastille ne sert
            // à rien si Pac-Man choisit un chemin qui mène au fantôme.
            if (!flee(map, tileSize, ghost)) {
                chooseDirection(map, tileSize);
            }
        }

        move();
        eatPellet(map, tileSize);
    }

    /**
     * Indique si le sprite est centré sur une case du labyrinthe.
     *
     * @param tileSize taille d'une case en pixels
     * @return {@code true} si un changement de direction est sûr
     */
    private boolean isAlignedOnTile(int tileSize) {

        return (x - OFFSET) % tileSize == 0 &&
               (y - OFFSET) % tileSize == 0;
    }

    /**
     * Ajoute la case actuelle à une mémoire limitée des derniers passages.
     *
     * @param col colonne de la case actuelle
     * @param row ligne de la case actuelle
     * @param mapWidth largeur de la carte, utilisée pour créer un identifiant
     */
    private void rememberTile(int col, int row, int mapWidth) {

        recentTiles.addLast(row * mapWidth + col);

        if (recentTiles.size() > RECENT_TILE_LIMIT) {
            recentTiles.removeFirst();
        }
    }

    /**
     * Active une fuite si le fantôme est suffisamment proche par le chemin.
     *
     * <p>La distance est calculée dans le labyrinthe, et non à vol d'oiseau :
     * deux personnages séparés par un mur ne doivent pas être considérés
     * proches. Pendant une fuite, le demi-tour est autorisé, car s'éloigner du
     * danger est plus important que conserver une trajectoire fluide.</p>
     *
     * @param map labyrinthe courant
     * @param tileSize taille d'une case en pixels
     * @param ghost fantôme à éviter
     * @return {@code true} si une direction de fuite a été choisie
     */
    private boolean flee(int[][] map, int tileSize, ghost ghost) {

        int col = (x - OFFSET) / tileSize;
        int row = (y - OFFSET) / tileSize;

        int ghostCol = (ghost.p + ghost.IMAGE_SIZE / 2) / tileSize;
        int ghostRow = (ghost.o + ghost.IMAGE_SIZE / 2) / tileSize;

        int[][] dist = distancesFrom(ghostCol, ghostRow, map);

        if (dist[row][col] > FLEE_DISTANCE) {
            return false;
        }

        // On compare les sorties possibles depuis la case courante. Le BFS
        // donne leur vraie distance de fuite en tenant compte des murs.
        List<Integer> best = new ArrayList<>();
        int bestDistance = -1;

        for (int d = 0; d < 4; d++) {

            if (!isFree(d, col, row, map)) {
                continue;
            }

            int nextCol = col + COLUMN_MOVES[d];
            int nextRow = row + ROW_MOVES[d];

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

    /**
     * Calcule les plus courtes distances depuis une case avec un BFS.
     *
     * <p>Chaque déplacement entre deux cases libres coûte une case. Le BFS
     * visite donc les positions dans l'ordre de leur distance et fournit les
     * distances minimales sans avoir besoin d'un algorithme plus coûteux.</p>
     *
     * @param startCol colonne de départ
     * @param startRow ligne de départ
     * @param map labyrinthe à parcourir
     * @return une matrice de distances ; {@link Integer#MAX_VALUE} désigne une
     *         case inaccessible
     */
    private int[][] distancesFrom(int startCol, int startRow, int[][] map) {

        int[][] dist = new int[map.length][map[0].length];

        for (int[] line : dist) {
            java.util.Arrays.fill(line, Integer.MAX_VALUE);
        }

        ArrayDeque<int[]> queue = new ArrayDeque<>();

        dist[startRow][startCol] = 0;
        queue.add(new int[] { startCol, startRow });

        while (!queue.isEmpty()) {

            int[] cell = queue.poll();

            for (int d = 0; d < 4; d++) {

                int c = cell[0] + COLUMN_MOVES[d];
                int r = cell[1] + ROW_MOVES[d];

                if (r < 0 || r >= map.length ||
                    c < 0 || c >= map[0].length ||
                    map[r][c] == 1 ||
                    dist[r][c] != Integer.MAX_VALUE) {
                    continue;
                }

                // Une case est ajoutée une seule fois. La première distance
                // trouvée est donc forcément la plus courte.
                dist[r][c] = dist[cell[1]][cell[0]] + 1;
                queue.add(new int[] { c, r });
            }
        }

        return dist;
    }

    /**
     * Choisit une direction pour atteindre la pastille accessible la plus
     * proche, ou explorer si toutes les pastilles ont disparu.
     *
     * @param map labyrinthe courant
     * @param tileSize taille d'une case en pixels
     */
    private void chooseDirection(int[][] map, int tileSize) {

        int col = (x - OFFSET) / tileSize;
        int row = (y - OFFSET) / tileSize;
        int[] target = closestPellet(col, row, map);

        if (target != null) {
            // On part de la pastille pour connaître, pour chaque sortie de
            // Pac-Man, le nombre exact de cases restant jusqu'à l'objectif.
            int[][] targetDistances = distancesFrom(target[0], target[1], map);
            direction = directionTowardsTarget(
                col,
                row,
                targetDistances,
                map
            );
        } else {
            // Cette solution garde un comportement valable si le niveau n'a
            // plus de pastille, au lieu de laisser Pac-Man bloqué sans choix.
            direction = explorationDirection(col, row, map);
        }
    }

    /**
     * Trouve une pastille minimisant la longueur du chemin depuis Pac-Man.
     *
     * @param col colonne de Pac-Man
     * @param row ligne de Pac-Man
     * @param map labyrinthe contenant les pastilles
     * @return les coordonnées de la pastille visée, ou {@code null} s'il n'en
     *         reste aucune accessible
     */
    private int[] closestPellet(int col, int row, int[][] map) {

        int[][] distances = distancesFrom(col, row, map);
        List<int[]> closest = new ArrayList<>();
        int shortestDistance = Integer.MAX_VALUE;

        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                int distance = distances[r][c];

                if (map[r][c] != 2 || distance == Integer.MAX_VALUE) {
                    continue;
                }

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closest.clear();
                    closest.add(new int[] { c, r });
                } else if (distance == shortestDistance) {
                    closest.add(new int[] { c, r });
                }
            }
        }

        // À distance égale, un choix aléatoire évite qu'un ordre fixe de
        // lecture de la carte donne toujours le même parcours.
        return closest.isEmpty()
            ? null
            : closest.get(random.nextInt(closest.size()));
    }

    /**
     * Choisit le premier pas d'un plus court chemin vers une cible.
     *
     * @param col colonne de Pac-Man
     * @param row ligne de Pac-Man
     * @param targetDistances distances minimales jusqu'à la cible
     * @param map labyrinthe courant
     * @return une direction praticable, privilégiant les cases peu récentes
     */
    private int directionTowardsTarget(
        int col,
        int row,
        int[][] targetDistances,
        int[][] map
    ) {

        List<Integer> choices = allowedDirections(col, row, map);
        int bestDistance = Integer.MAX_VALUE;
        int fewestRecentVisits = Integer.MAX_VALUE;
        List<Integer> bestChoices = new ArrayList<>();

        for (int candidate : choices) {
            int nextCol = col + COLUMN_MOVES[candidate];
            int nextRow = row + ROW_MOVES[candidate];
            int distance = targetDistances[nextRow][nextCol];
            int visits = recentVisitCount(nextCol, nextRow, map[0].length);

            // Une distance plus faible est toujours prioritaire : c'est ce qui
            // garantit que Pac-Man suit réellement un plus court chemin.
            if (distance < bestDistance ||
                (distance == bestDistance && visits < fewestRecentVisits)) {

                bestDistance = distance;
                fewestRecentVisits = visits;
                bestChoices.clear();
                bestChoices.add(candidate);
            } else if (distance == bestDistance &&
                       visits == fewestRecentVisits) {

                bestChoices.add(candidate);
            }
        }

        return bestChoices.get(random.nextInt(bestChoices.size()));
    }

    /**
     * Choisit une sortie peu utilisée lorsqu'il n'y a plus de pastille.
     *
     * @param col colonne de Pac-Man
     * @param row ligne de Pac-Man
     * @param map labyrinthe courant
     * @return une direction qui limite les boucles récentes
     */
    private int explorationDirection(int col, int row, int[][] map) {

        List<Integer> choices = allowedDirections(col, row, map);
        int fewestRecentVisits = Integer.MAX_VALUE;
        List<Integer> bestChoices = new ArrayList<>();

        for (int candidate : choices) {
            int nextCol = col + COLUMN_MOVES[candidate];
            int nextRow = row + ROW_MOVES[candidate];
            int visits = recentVisitCount(nextCol, nextRow, map[0].length);

            if (visits < fewestRecentVisits) {
                fewestRecentVisits = visits;
                bestChoices.clear();
                bestChoices.add(candidate);
            } else if (visits == fewestRecentVisits) {
                bestChoices.add(candidate);
            }
        }

        return bestChoices.get(random.nextInt(bestChoices.size()));
    }

    /**
     * Retourne toutes les directions libres depuis la case actuelle.
     *
     * @param col colonne de Pac-Man
     * @param row ligne de Pac-Man
     * @param map labyrinthe courant
     * @return les sorties autorisées depuis la case actuelle
     */
    private List<Integer> allowedDirections(int col, int row, int[][] map) {

        List<Integer> choices = new ArrayList<>();
        for (int candidate = 0; candidate < 4; candidate++) {
            if (isFree(candidate, col, row, map)) {
                choices.add(candidate);
            }
        }

        // Un demi-tour reste disponible : le plus court chemin vers une
        // pastille peut précisément commencer par revenir sur ses pas.
        // La mémoire courte le rend moins probable en exploration seule.
        return choices;
    }

    /**
     * Compte les passages récents sur une case.
     *
     * @param col colonne à examiner
     * @param row ligne à examiner
     * @param mapWidth largeur de la carte
     * @return le nombre d'apparitions de cette case dans la mémoire courte
     */
    private int recentVisitCount(int col, int row, int mapWidth) {

        int tile = row * mapWidth + col;
        int visits = 0;

        for (int recentTile : recentTiles) {
            if (recentTile == tile) {
                visits++;
            }
        }

        return visits;
    }

    /**
     * Indique si la case voisine dans une direction est praticable.
     *
     * @param d direction testée
     * @param col colonne de départ
     * @param row ligne de départ
     * @param map labyrinthe courant
     * @return {@code true} si la case existe et n'est pas un mur
     */
    private boolean isFree(int d, int col, int row, int[][] map) {

        col += COLUMN_MOVES[d];
        row += ROW_MOVES[d];

        if (row < 0 || row >= map.length ||
            col < 0 || col >= map[0].length) {
            return false;
        }

        return map[row][col] != 1;
    }

    /**
     * Déplace le sprite de quelques pixels dans sa direction actuelle.
     *
     * <p>Le mouvement reste pixel par pixel pour être fluide. Le choix de
     * direction, lui, reste limité aux centres des cases dans {@link #update}
     * afin de préserver l'alignement avec le labyrinthe.</p>
     */
    private void move() {

        switch (direction) {

            case RIGHT:
                x += speed;
                break;

            case LEFT:
                x -= speed;
                break;

            case DOWN:
                y += speed;
                break;

            case UP:
                y -= speed;
                break;
        }
    }

    /**
     * Retire la pastille située sous le centre de Pac-Man et augmente le score.
     *
     * @param map labyrinthe contenant les pastilles
     * @param tileSize taille d'une case en pixels
     */
    private void eatPellet(int[][] map, int tileSize) {

        // Le centre est utilisé plutôt que le coin du sprite pour éviter de
        // manger une pastille de la case voisine pendant une transition.
        int col = (x + IMAGE_SIZE / 2) / tileSize;
        int row = (y + IMAGE_SIZE / 2) / tileSize;

        // La vérification évite un accès hors de la carte si le niveau ou la
        // position de départ est modifié plus tard.
        if (row < 0 || row >= map.length ||
            col < 0 || col >= map[0].length) {
            return;
        }

        // Passer la case à 0 garantit que la même pastille ne peut compter
        // qu'une fois, même si plusieurs images sont dessinées sur la case.
        if (map[row][col] == 2) {
            map[row][col] = 0;
            score++;
        }
    }

    /**
     * Dessine Pac-Man à sa position actuelle, orienté vers son déplacement.
     *
     * @param g2D contexte graphique dans lequel Pac-Man doit être dessiné
     */
    public void draw(Graphics2D g2D) {

        // Convertit la direction de déplacement en angle de rotation.
        // L'angle est en radians, comme attendu par Graphics2D.rotate().
        double rotation = switch (direction) {
            // Gauche : le sprite orienté à droite fait un demi-tour. 180 Deg
            case LEFT -> Math.PI;
            // Bas : rotation dans le sens des aiguilles d'une montre.
            case DOWN -> Math.PI / 2;
            // Haut : rotation dans le sens inverse des aiguilles d'une montre.
            case UP -> -Math.PI / 2;
            // Droite (direction 0) : le sprite a déjà la bonne orientation.
            default -> 0;
        };

        // Crée un contexte indépendant : sa rotation ne touchera pas g2D.
        Graphics2D rotatedGraphics = (Graphics2D) g2D.create();

        // Applique la rotation autour du centre du sprite, pas autour du coin
        // supérieur gauche. Pac-Man reste ainsi exactement à sa position.
        rotatedGraphics.rotate(
            rotation,
            x + IMAGE_SIZE / 2.0,
            y + IMAGE_SIZE / 2.0
        );

        // Dessine l'image après rotation, à la taille prévue pour Pac-Man.
        rotatedGraphics.drawImage(
            image,
            x,
            y,
            IMAGE_SIZE,
            IMAGE_SIZE,
            null
        );

        // Libère la copie du contexte graphique créée avec create().
        rotatedGraphics.dispose();
    }
}
