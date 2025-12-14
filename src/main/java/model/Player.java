package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    private final String name;
    private Team team;      // null en mode solo
    private int score;
    private List<Card> hand;
    private int lowestRevealedIndex = -1;  // -1 = aucune carte révélée du bas
    private int highestRevealedIndex = -1; // -1 = aucune carte révélée du haut

    public Player(String name) {
        this.name = name;
        this.team = null;
        this.score = 0;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getScore() {
        return score;
    }

    public void addPoint(int pts) {
        this.score += pts;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
        card.setOwner(this);
    }

    public void removeCardFromHand(Card card) {
        hand.remove(card);
        card.setOwner(null);
    }

    public void sortHand() {
        Collections.sort(hand);
    }

    /**
     * Vérifie si une carte peut être retournée selon les règles Trio
     * Seules les cartes aux extrémités (non révélées) peuvent être retournées
     */
    public boolean canFlipCard(Card card) {
        if (!hand.contains(card)) {
            return false;
        }

        int cardIndex = hand.indexOf(card);
        int handSize = hand.size();

        // Première carte (plus petite) - peut être retournée si jamais révélée
        if (cardIndex == 0 && lowestRevealedIndex == -1) {
            return true;
        }

        // Dernière carte (plus grande) - peut être retournée si jamais révélée
        if (cardIndex == handSize - 1 && highestRevealedIndex == -1) {
            return true;
        }

        // Deuxième carte si la première a été révélée
        if (lowestRevealedIndex == 0 && cardIndex == 1) {
            return true;
        }

        // Troisième carte si les deux premières ont été révélées
        if (lowestRevealedIndex == 1 && cardIndex == 2) {
            return true;
        }

        // Avant-dernière carte si la dernière a été révélée
        if (highestRevealedIndex == handSize - 1 && cardIndex == handSize - 2) {
            return true;
        }

        // Avant-avant-dernière carte si les deux dernières ont été révélées
        if (highestRevealedIndex == handSize - 2 && cardIndex == handSize - 3) {
            return true;
        }

        return false;
    }

    public void markCardRevealed(Card card) {
        int cardIndex = hand.indexOf(card);
        if (cardIndex == -1) return;

        // Mettre à jour les indices de cartes révélées
        if (cardIndex <= hand.size() / 2) {
            // Carte du côté bas (petites valeurs)
            lowestRevealedIndex = cardIndex;
        } else {
            // Carte du côté haut (grandes valeurs)
            highestRevealedIndex = cardIndex;
        }
    }

    public int getLowestRevealedIndex() {
        return lowestRevealedIndex;
    }

    public int getHighestRevealedIndex() {
        return highestRevealedIndex;
    }

    @Override
    public String toString() {
        return "Player{name='" + name + "', score=" + score + ", cards=" + hand.size() + "}";
    }
}