package model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final List<Card> centerCards = new ArrayList<>();

    public Board(List<Card> cards) {
        this.centerCards.addAll(cards);
    }

    public List<Card> getCenterCards() {
        return centerCards;
    }

    public void addCard(Card c) {
        centerCards.add(c);
    }

    public void removeCard(Card c) {
        centerCards.remove(c);
    }

    public Card getCardAt(int index) {
        if (index < 0 || index >= centerCards.size()) return null;
        return centerCards.get(index);
    }

    public int size() {
        return centerCards.size();
    }
}
