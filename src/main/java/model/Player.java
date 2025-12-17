package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    private final String name;
    private Team team;      // null en mode solo
    private int score;
    private List<Card> hand;
    private List<Card> revealedThisTurn = new ArrayList<>();  // Cartes révélées ce tour

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
        revealedThisTurn.remove(card);
        card.setOwner(null);
    }

    public void sortHand() {
        Collections.sort(hand);
    }

    /**
     * Vérifie si une carte peut être retournée selon les règles Trio
     * Seules les cartes aux extrémités (non révélées ce tour) peuvent être retournées
     */
    public boolean canFlipCard(Card card) {
        if (!hand.contains(card)) {
            return false;
        }

        // Si la carte a déjà été révélée ce tour, on ne peut pas la retourner à nouveau
        if (revealedThisTurn.contains(card)) {
            return false;
        }

        int cardIndex = hand.indexOf(card);
        int handSize = hand.size();

        // Trouver l'index de la première carte non révélée à gauche
        int leftMostUnrevealed = -1;
        for (int i = 0; i < handSize; i++) {
            if (!revealedThisTurn.contains(hand.get(i))) {
                leftMostUnrevealed = i;
                break;
            }
        }

        // Trouver l'index de la dernière carte non révélée à droite
        int rightMostUnrevealed = -1;
        for (int i = handSize - 1; i >= 0; i--) {
            if (!revealedThisTurn.contains(hand.get(i))) {
                rightMostUnrevealed = i;
                break;
            }
        }

        // La carte doit être soit la plus à gauche, soit la plus à droite parmi les non-révélées
        return (cardIndex == leftMostUnrevealed || cardIndex == rightMostUnrevealed);
    }

    public void markCardRevealed(Card card) {
        if (hand.contains(card) && !revealedThisTurn.contains(card)) {
            revealedThisTurn.add(card);
        }
    }

    /**
     * Réinitialise les cartes révélées ce tour (appelé au début d'un nouveau tour)
     */
    public void resetRevealedThisTurn() {
        revealedThisTurn.clear();
    }

    public List<Card> getRevealedThisTurn() {
        return revealedThisTurn;
    }

    @Override
    public String toString() {
        return "Player{name='" + name + "', score=" + score + ", cards=" + hand.size() + "}";
    }
}