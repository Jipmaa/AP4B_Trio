package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        createDeck();
    }

    private void createDeck() {
        // Le jeu Trio contient des cartes numérotées de 1 à 12, avec 3 exemplaires de chaque
        for (int i = 0; i < 3; i++) {
            for (int value = 1; value <= 12; value++) {
                cards.add(new Card(value));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    @Override
    public String toString() {
        return "Deck{" + "cards=" + cards.size() + '}';
    }
}
