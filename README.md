
# Pac-Man

Projet Java de jeu Pac-Man réalisé en groupe.

## Prérequis

- Java 21 ou supérieur
- Maven 3.9 ou supérieur (pour une utilisation en ligne de commande)

Le projet suit la structure Maven standard et peut être ouvert avec un IDE compatible Java/Maven, notamment IntelliJ IDEA, NetBeans, Eclipse ou VS Code.

## Structure

```text
Pac-Man/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    └── main/
        ├── java/
        │   └── main/
        │       ├── Main.java
        │       └── GamePanel.java
        └── resources/
            ├── game.png
            └── pacman.png
```

## Lancer avec un IDE

Ouvrir/importer le projet à partir du fichier `pom.xml`. L'IDE reconnaîtra automatiquement la structure Maven.

La classe principale est :

```text
main.Main
```

## Lancer depuis le terminal

À la racine du projet :

```bash
mvn clean package
java -jar target/pacman-game-1.0-SNAPSHOT.jar
```

Pour compiler et lancer directement sans produire le JAR final :

```bash
mvn compile
java -cp target/classes:src/main/resources main.Main
```

Sous Windows, le séparateur du classpath est `;` au lieu de `:`.

## Organisation du dépôt

Les fichiers propres à un IDE (IntelliJ, Eclipse, NetBeans, VS Code, etc.) ne sont pas versionnés. Les fichiers générés par Maven, notamment `target/`, ne sont pas versionnés non plus.

Le fichier `pom.xml` est la configuration commune du projet et permet aux différents IDE compatibles Maven de reconnaître le même projet sans imposer un IDE particulier.
