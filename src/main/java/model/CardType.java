package model;

public enum CardType {
    NORMAL,
    SPICY,      // voler une carte révélée
    MALUS,      // cacher toutes les cartes du board
    BONUS,      // retourner une carte gratuite
    BLOCK       // joueur suivant sauté
}
