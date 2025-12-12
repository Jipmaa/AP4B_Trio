ATTENTION, CE README A ETE FAIT PAR IA ET N'A PAS ENCORE ETE VERIFIE

# 🎮 TRIO - Le Jeu de Mémoire

Un jeu de mémoire moderne et visuellement attrayant développé en Java avec JavaFX.

## 📋 Table des Matières

- [Fonctionnalités](#fonctionnalités)
- [Structure du Projet](#structure-du-projet)
- [Installation](#installation)
- [Configuration des Cartes](#configuration-des-cartes)
- [Modes de Jeu](#modes-de-jeu)
- [Comment Jouer](#comment-jouer)

## ✨ Fonctionnalités

### Modes de Jeu
- **Mode Solo** : Chaque joueur joue pour soi
- **Mode Équipes** : Les joueurs sont répartis en 2 équipes
- **Mode Picante 🌶️** : Les trios valent 2 points au lieu de 1 (compatible avec tous les modes)

### Interface Graphique
- Design moderne avec effets visuels soignés
- Animations fluides lors du retournement des cartes
- Effets de survol et de clic
- Scoreboard en temps réel
- Menu principal élégant
- Écran de configuration de partie

### Système de Scoring
- Points par trio : 1 point (ou 2 en mode Picante)
- Le joueur qui réussit un trio rejoue
- Affichage des scores individuels et par équipe
- Écran de fin de partie avec classement

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/
│   │   ├── controller/
│   │   │   ├── GameController.java       # Gestion du jeu
│   │   │   └── NavigationController.java # Navigation entre écrans
│   │   ├── model/
│   │   │   ├── Board.java                # Plateau de jeu
│   │   │   ├── Card.java                 # Modèle de carte
│   │   │   ├── CardType.java             # Types de cartes (futures extensions)
│   │   │   ├── Deck.java                 # Paquet de cartes
│   │   │   ├── Game.java                 # Logique du jeu
│   │   │   ├── Player.java               # Modèle de joueur
│   │   │   └── Team.java                 # Modèle d'équipe
│   │   ├── view/
│   │   │   ├── BoardView.java            # Vue du plateau
│   │   │   ├── CardView.java             # Vue d'une carte
│   │   │   ├── GameSetupView.java        # Écran de configuration
│   │   │   ├── GameView.java             # Interface de jeu principale
│   │   │   ├── MainMenuView.java         # Menu principal
│   │   │   └── PlayerView.java           # Vue d'un joueur
│   │   └── Main.java                     # Point d'entrée
│   └── resources/
│       ├── images/
│       │   ├── back.png                  # Image du dos des cartes
│       │   ├── franck.jpg                # Image de fond
│       │   └── ghislain.jpg              # Exemple d'image de carte
│       └── cards.txt                     # Configuration des cartes
```

## 🚀 Installation

### Prérequis
- Java 11 ou supérieur
- JavaFX 11 ou supérieur

### Configuration JavaFX

1. **Télécharger JavaFX SDK** depuis [openjfx.io](https://openjfx.io/)

2. **Configurer dans votre IDE** :

   **IntelliJ IDEA** :
   - File > Project Structure > Libraries
   - Ajouter le dossier `lib` du JavaFX SDK
   - Dans Run Configurations, ajouter VM options :
   ```
   --module-path /chemin/vers/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

   **Eclipse** :
   - Right-click project > Build Path > Add External JARs
   - Ajouter tous les JARs du dossier `lib` de JavaFX
   - Dans Run Configurations > Arguments, ajouter VM arguments :
   ```
   --module-path /chemin/vers/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

3. **Compiler et exécuter** :
   ```bash
   javac --module-path /chemin/vers/javafx-sdk/lib --add-modules javafx.controls -d out src/main/java/**/*.java
   java --module-path /chemin/vers/javafx-sdk/lib --add-modules javafx.controls -cp out main.Main
   ```

## 🎴 Configuration des Cartes

### Format du fichier cards.txt

Le fichier `src/main/resources/cards.txt` définit toutes les cartes du jeu :

```
id;value;imagePath
```

- **id** : Identifiant unique de la carte (entier)
- **value** : Valeur de la carte pour former des trios (entier)
- **imagePath** : Chemin vers l'image (relatif au dossier resources)

### Exemple

```
1;1;images/ghislain.jpg
2;1;images/ghislain.jpg
3;1;images/ghislain.jpg
4;2;images/franck.jpg
5;2;images/franck.jpg
6;2;images/franck.jpg
7;3;images/autre.jpg
8;3;images/autre.jpg
9;3;images/autre.jpg
```

### Règles importantes

1. **Trios** : Pour former un trio, 3 cartes doivent avoir la **même valeur**
2. **Nombre de cartes** : Il est recommandé d'avoir un multiple de 3 cartes
3. **Format des images** : 
   - Formats supportés : PNG, JPG, JPEG
   - Ratio recommandé : 2:3 (portrait)
   - Résolution recommandée : 300x450 pixels minimum

### Ajouter vos propres images

1. Placez vos images dans `src/main/resources/images/`
2. Éditez `cards.txt` pour référencer vos nouvelles images
3. Assurez-vous que les valeurs forment des trios complets

**Exemple d'ajout** :
```
10;4;images/ma_nouvelle_image.png
11;4;images/ma_nouvelle_image.png
12;4;images/ma_nouvelle_image.png
```

## 🎮 Modes de Jeu

### Mode Solo
- 2 à 4 joueurs
- Chaque joueur accumule ses propres points
- Le joueur avec le plus de points gagne

### Mode Équipes
- 2 ou 4 joueurs (2 équipes de 1 ou 2 joueurs)
- Les points sont partagés par équipe
- Répartition automatique des joueurs entre les équipes

### Mode Picante 🌶️
- Option activable avec n'importe quel mode
- Les trios valent 2 points au lieu de 1
- Augmente l'intensité du jeu !

## 🕹️ Comment Jouer

### Règles du Jeu

1. **Objectif** : Former le maximum de trios (3 cartes de même valeur)

2. **Tour de jeu** :
   - Le joueur actif retourne jusqu'à 3 cartes
   - Si les 3 cartes ont la même valeur → **TRIO !**
     - Le joueur gagne 1 point (2 en mode Picante)
     - Les cartes sont retirées du plateau
     - Le joueur rejoue
   - Si les cartes ne forment pas un trio :
     - Les cartes sont retournées face cachée
     - Le tour passe au joueur suivant

3. **Fin de partie** : Quand toutes les cartes ont été trouvées

### Contrôles

- **Clic sur une carte** : Retourner la carte
- **Bouton "Passer le Tour"** : Abandonner le tour en cours
- **Bouton "Menu"** : Retourner au menu principal

## 🎨 Personnalisation

### Changer l'image de fond

Remplacez `src/main/resources/images/franck.jpg` par votre propre image.

### Changer l'image du dos des cartes

Remplacez `src/main/resources/images/back.png` par votre propre image.

### Modifier les couleurs

Éditez les styles dans les fichiers View :
- Couleur principale (or) : `#FFD700`
- Couleur de fond : `rgba(30, 35, 45, 0.95)`
- Couleur de succès : `#4CAF50`

## 🐛 Dépannage

### Les images ne s'affichent pas
- Vérifiez que les images sont dans `src/main/resources/images/`
- Vérifiez les chemins dans `cards.txt`
- Assurez-vous que le format est supporté (PNG, JPG, JPEG)

### Erreur "cards.txt not found"
- Le fichier doit être dans `src/main/resources/`
- Vérifiez que le fichier est inclus dans le build

### Les cartes ne se retournent pas
- Vérifiez que JavaFX est correctement configuré
- Vérifiez la console pour les erreurs

## 📝 Notes de Développement

### Extensions Futures Possibles

Le code est préparé pour des extensions futures :
- **CardType** : Types de cartes spéciales (SPICY, MALUS, BONUS, BLOCK)
- Support de plus de joueurs
- Modes de jeu additionnels
- Système de sauvegarde/chargement
- Statistiques et historique

### Architecture

Le projet suit une architecture MVC (Model-View-Controller) :
- **Model** : Logique métier et données
- **View** : Interface graphique
- **Controller** : Coordination entre Model et View

## 📄 Licence

Ce projet est fourni à titre éducatif.

---

**Bon jeu ! 🎮🎉**
