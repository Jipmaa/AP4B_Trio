package model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final List<Card> centerCards;

    public Board() {
        this.centerCards = new ArrayList<>();
    }

    public List<Card> getCenterCards() {
        return centerCards;
    }

    public void addCard(Card card) {
        if (card != null) {
            centerCards.add(card);
        }
    }

    public Card removeCard(Card card) {
        if (centerCards.remove(card)) {
            return card;
        }
        return null;
    }

    public Card removeCardByIndex(int index) {
        if (index >= 0 && index < centerCards.size()) {
            return centerCards.remove(index);
        }
        return null;
    }

    public boolean isEmpty() {
        return centerCards.isEmpty();
    }

    public int size() {
        return centerCards.size();
    }

    @Override
    public String toString() {
        return "Board{" + "centerCards=" + centerCards.size() + '}';
    }
}
