package model;

/**
 * Énumération des types de cartes spéciales
 * Ces types peuvent être utilisés pour des extensions futures du jeu
 */
public enum CardType {
    NORMAL,     // Carte normale
    SPICY,      // Permet de voler une carte révélée d'un adversaire
    MALUS,      // Cache toutes les cartes du plateau
    BONUS,      // Permet de retourner une carte gratuitement
    BLOCK       // Le joueur suivant saute son tour
}