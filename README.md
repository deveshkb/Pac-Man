# Frame et Game Loop

## Description

* `Main.java` : crée et configure la fenêtre principale du jeu.
* `GamePanel.java` : contient le panneau de jeu et gère la boucle principale du jeu.

## Structure du projet

```text
Pac-Man/
      |_src/
          |_main/
               |_Main.java
               |_GamePanel.java
```

## Dépendances entre les fichiers

Les deux fichiers sont séparés, mais ils sont **interdépendants** et doivent fonctionner ensemble.

`Main.java` crée une instance de `GamePanel`, l'ajoute à la fenêtre principale et démarre le thread du jeu.

`GamePanel.java` fournit le panneau de jeu ainsi que la boucle d'exécution nécessaire au fonctionnement du jeu.

**TRES IMPORTANT : Les deux fichiers doivent donc être présents pour que l'application fonctionne correctement.**

## Fonctionnement

Au lancement du programme, `Main.java` :

1. Crée la fenêtre principale avec `JFrame`.
2. Configure le titre et les paramètres de la fenêtre.
3. Crée un objet `GamePanel`.
4. Ajoute le `GamePanel` à la fenêtre.
5. Définit la taille de la fenêtre.
6. Centre la fenêtre sur l'écran.
7. Affiche la fenêtre.
8. Démarre le thread du jeu.

Le `GamePanel` est ensuite responsable de l'exécution de la boucle du jeu.

## Lancement du projet

Pour lancer le projet :

1. Ouvrir le projet dans **NetBeans**.
2. Vérifier que `Main.java` et `GamePanel.java` se trouvent dans le même package :

   ```java
   package main;
   ```
3. Exécuter `Main.java`.
4. La fenêtre du jeu devrait apparaître.


